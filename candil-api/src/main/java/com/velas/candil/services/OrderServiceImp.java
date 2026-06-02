package com.velas.candil.services;

import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import com.velas.candil.config.mercadopago.MercadoPagoProperties;
import com.velas.candil.entities.candle.Candle;
import com.velas.candil.entities.cartItem.CartItem;
import com.velas.candil.entities.order.Order;
import com.velas.candil.exceptions.cart.CartNotFoundException;
import com.velas.candil.exceptions.users.UserNotFoundException;
import com.velas.candil.models.order.OrderStatus;
import com.velas.candil.entities.orderItem.OrderItem;
import com.velas.candil.entities.shoppingCart.ShoppingCart;
import com.velas.candil.entities.user.User;
import com.velas.candil.exceptions.cart.CartEmptyException;
import com.velas.candil.exceptions.cart.OrderNotFoundException;
import com.velas.candil.exceptions.infra.InternalServerErrorException;
import com.velas.candil.models.order.OrderItemResponseDto;
import com.velas.candil.models.order.OrderResponseDto;
import com.velas.candil.repositories.CandleRepository;
import com.velas.candil.repositories.OrderRepository;
import com.velas.candil.repositories.ShoppingCartRepository;
import com.velas.candil.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final MercadoPagoProperties mpProperties;
    private final CandleRepository candleRepository;

    @Override
    @Transactional
    public OrderResponseDto checkout(Long userId) {
        User user = findUser(userId);

        ShoppingCart cart = shoppingCartRepository.findByUser(user)
                .orElseThrow(() -> new CartNotFoundException("ShoppingCart not found for user " + user.getId()));

        if (cart.getCartItems().isEmpty()) {
            throw new CartEmptyException("Cart is empty");
        }

        Order orderEntity = Order.builder()
                .user(user)
                .total(cart.getSubTotal())
                .status(OrderStatus.PENDING)
                .build();

        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> buildOrderItem(cartItem, orderEntity))
                .toList();

        orderEntity.setItems(orderItems);
        Order order = orderRepository.save(orderEntity);

        try {
            Preference preference = createMercadoPagoPreference(order);
            order.setMercadoPagoPreferenceId(preference.getId());
            order.setCheckoutUrl(preference.getSandboxInitPoint());
            order = orderRepository.save(order);

        } catch (MPApiException e) {
            String body = e.getApiResponse() != null ? e.getApiResponse().getContent() : "sin body";
            log.error("MP API Error - Status: {} | Body: {}", e.getStatusCode(), body);
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);
            throw new InternalServerErrorException("Error al procesar el pago con Mercado Pago");
        } catch (MPException e) {
            log.error("MP Error: {}", e.getMessage());
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);
            throw new InternalServerErrorException("Error al procesar el pago con Mercado Pago");
        }

        return toDto(order);
    }

    @Override
    @Transactional
    public void handleWebhook(Map<String, Object> payload) {
        String type = (String) payload.get("type");

        if (!"payment".equals(type)) {
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        if (data == null || data.get("id") == null) {
            log.warn("Webhook sin 'data.id': {}", payload);
            return;
        }

        String paymentIdStr = data.get("id").toString();

        try {
            PaymentClient paymentClient = new PaymentClient();
            Payment payment = paymentClient.get(Long.parseLong(paymentIdStr));

            String externalReference = payment.getExternalReference();
            String mpStatus = payment.getStatus();

            Order order = null;

            if (externalReference != null) {
                try {
                    Long orderId = Long.parseLong(externalReference);
                    order = orderRepository.findById(orderId).orElse(null);
                } catch (NumberFormatException ignored) {}
            }

            if (order == null) {
                order = orderRepository.findByMercadoPagoPaymentId(paymentIdStr).orElse(null);
            }

            if (order == null) {
                log.warn("No se encontro orden para externalReference={} o paymentId={}",
                        externalReference, paymentIdStr);
                return;
            }

            OrderStatus previousStatus = order.getStatus();
            OrderStatus newStatus = mapMpStatus(mpStatus);

            order.setMercadoPagoPaymentId(paymentIdStr);
            order.setStatus(newStatus);
            orderRepository.save(order);

            if (newStatus == OrderStatus.PAID && previousStatus != OrderStatus.PAID) {
                validateStock(order);
                updateStock(order);
                clearUserCart(order.getUser());
            }

        } catch (MPApiException e) {
            String body = e.getApiResponse() != null ? e.getApiResponse().getContent() : "sin body";
            log.error("MP API Error consultando pago {} - Status: {} | Body: {}", paymentIdStr, e.getStatusCode(), body);
        } catch (MPException e) {
            log.error("Error al consultar pago {} en Mercado Pago: {}", paymentIdStr, e.getMessage());
        }
    }

    @Override
    public List<OrderResponseDto> getMyOrders(Long userId) {
        User user = findUser(userId);
        return orderRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public OrderResponseDto getOrderById(Long userId, Long orderId) {
        User user = findUser(userId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Orden no encontrada"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new OrderNotFoundException("Orden no encontrada");
        }

        return toDto(order);
    }

    private Preference createMercadoPagoPreference(Order order) throws MPException, MPApiException {
        PreferenceClient client = new PreferenceClient();

        String successUrl = mpProperties.getSuccessUrl() != null
                ? mpProperties.getSuccessUrl()
                : "http://localhost:4200/orders/success";
        String failureUrl = mpProperties.getFailureUrl() != null
                ? mpProperties.getFailureUrl()
                : "http://localhost:4200/orders/failure";
        String pendingUrl = mpProperties.getPendingUrl() != null
                ? mpProperties.getPendingUrl()
                : "http://localhost:4200/orders/pending";

        List<PreferenceItemRequest> items = order.getItems().stream()
                .map(item -> PreferenceItemRequest.builder()
                        .id(item.getCandleId().toString())
                        .title(item.getCandleName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .currencyId("COP")
                        .build())
                .toList();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(successUrl + "?orderId=" + order.getId())
                .failure(failureUrl + "?orderId=" + order.getId())
                .pending(pendingUrl + "?orderId=" + order.getId())
                .build();

        PreferenceRequest request = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
                .autoReturn("approved")
                .notificationUrl(mpProperties.getWebhookUrl())
                .externalReference(order.getId().toString())
                .build();

        return client.create(request);
    }

    private OrderItem buildOrderItem(CartItem cartItem, Order order) {
        return OrderItem.builder()
                .order(order)
                .candleId(cartItem.getCandle().getId())
                .candleName(cartItem.getCandle().getName())
                .quantity(cartItem.getQuantity())
                .unitPrice(cartItem.getPriceSnapshot())
                .subtotal(cartItem.getSubtotal())
                .build();
    }

    private OrderStatus mapMpStatus(String mpStatus) {
        return switch (mpStatus) {
            case "approved"  -> OrderStatus.PAID;
            case "rejected"  -> OrderStatus.FAILED;
            case "cancelled" -> OrderStatus.CANCELLED;
            case "refunded"  -> OrderStatus.REFUNDED;
            case "in_process",
                 "authorized",
                 "pending"   -> OrderStatus.IN_PROCESS;
            default          -> OrderStatus.IN_PROCESS;
        };
    }

    private void clearUserCart(User user) {
        shoppingCartRepository.findByUser(user).ifPresent(cart -> {
            cart.getCartItems().clear();
            cart.recalculateSubTotal();
            shoppingCartRepository.save(cart);
        });
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private OrderResponseDto toDto(Order order) {
        List<OrderItemResponseDto> itemDtos = order.getItems().stream()
                .map(i -> new OrderItemResponseDto(
                        i.getCandleId(),
                        i.getCandleName(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getSubtotal()))
                .toList();

        return new OrderResponseDto(
                order.getId(),
                order.getTotal(),
                order.getStatus(),
                order.getCheckoutUrl(),
                order.getMercadoPagoPreferenceId(),
                itemDtos,
                order.getCreatedAt());
    }

    private void updateStock(Order order) {
        List<Long> candleIds = order.getItems().stream()
                .map(OrderItem::getCandleId)
                .toList();

        Map<Long, Candle> candles = candleRepository.findAllById(candleIds)
                .stream()
                .collect(Collectors.toMap(Candle::getId, c -> c));

        for (OrderItem item : order.getItems()) {
            Candle candle = candles.get(item.getCandleId());

            if (candle == null) {
                throw new IllegalStateException("Candle no encontrada: " + item.getCandleId());
            }

            if (candle.getStock() < item.getQuantity()) {
                log.error("Stock insuficiente - candle: {} | stock: {} | requerido: {}",
                        candle.getId(), candle.getStock(), item.getQuantity());
                throw new IllegalStateException("Stock insuficiente");
            }

            candle.removeStock(item.getQuantity());
        }
    }

    private void validateStock(Order order) {
        List<Long> candleIds = order.getItems().stream()
                .map(OrderItem::getCandleId)
                .toList();

        Map<Long, Candle> candles = candleRepository.findAllById(candleIds)
                .stream()
                .collect(Collectors.toMap(Candle::getId, c -> c));

        for (OrderItem item : order.getItems()) {
            Candle candle = candles.get(item.getCandleId());

            if (candle == null) {
                throw new IllegalStateException("Candle not found: " + item.getCandleId());
            }

            if (candle.getStock() < item.getQuantity()) {
                log.error("Stock insuficiente al momento del pago - candle: {} | stock: {} | requerido: {}",
                        candle.getId(), candle.getStock(), item.getQuantity());
                throw new IllegalStateException("Stock no longer available");
            }
        }
    }
}