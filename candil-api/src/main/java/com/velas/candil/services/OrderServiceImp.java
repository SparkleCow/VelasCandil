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
import com.velas.candil.entities.cartItem.CartItem;
import com.velas.candil.entities.order.Order;
import com.velas.candil.entities.order.OrderStatus;
import com.velas.candil.entities.orderItem.OrderItem;
import com.velas.candil.entities.shoppingCart.ShoppingCart;
import com.velas.candil.entities.user.User;
import com.velas.candil.exceptions.cart.CartEmptyException;
import com.velas.candil.exceptions.cart.OrderNotFoundException;
import com.velas.candil.exceptions.infra.InternalServerErrorException;
import com.velas.candil.models.order.OrderItemResponseDto;
import com.velas.candil.models.order.OrderResponseDto;
import com.velas.candil.repositories.OrderRepository;
import com.velas.candil.repositories.ShoppingCartRepository;
import com.velas.candil.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final MercadoPagoProperties mpProperties;

    @Override
    @Transactional
    public OrderResponseDto checkout(Long userId) {
        User user = findUser(userId);

        ShoppingCart cart = shoppingCartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado para el usuario"));

        if (cart.getCartItems().isEmpty()) {
            throw new CartEmptyException("El carrito está vacío");
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

        cart.getCartItems().clear();
        cart.recalculateSubTotal();
        shoppingCartRepository.save(cart);

        return toDto(order);
    }

    @Override
    @Transactional
    public void handleWebhook(Map<String, Object> payload) {
        String type = (String) payload.get("type");
        log.info("Webhook recibido de Mercado Pago. Tipo: {}", type);

        if (!"payment".equals(type)) {
            log.debug("Webhook ignorado (tipo no es 'payment'): {}", type);
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

            log.info("Pago {} — estado MP: {} — externalReference: {}",
                    paymentIdStr, mpStatus, externalReference);

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
                log.warn("No se encontró orden para externalReference={} o paymentId={}",
                        externalReference, paymentIdStr);
                return;
            }

            order.setMercadoPagoPaymentId(paymentIdStr);
            order.setStatus(mapMpStatus(mpStatus));
            orderRepository.save(order);

            log.info("Orden {} actualizada a estado: {}", order.getId(), order.getStatus());

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
                .success(mpProperties.getSuccessUrl() + "?orderId=" + order.getId())
                .failure(mpProperties.getFailureUrl() + "?orderId=" + order.getId())
                .pending(mpProperties.getPendingUrl() + "?orderId=" + order.getId())
                .build();

        PreferenceRequest request = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
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

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
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
}