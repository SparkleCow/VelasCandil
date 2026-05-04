package com.velas.candil.services;

import com.velas.candil.models.order.OrderResponseDto;

import java.util.List;
import java.util.Map;

public interface OrderService {

    /**
     * Convierte el carrito del usuario en una orden y genera la preferencia de pago en MP.
     * Devuelve la orden con la checkoutUrl para redirigir al frontend.
     */
    OrderResponseDto checkout(Long userId);

    /**
     * Lista todas las órdenes del usuario autenticado.
     */
    List<OrderResponseDto> getMyOrders(Long userId);

    /**
     * Obtiene una orden específica del usuario autenticado.
     */
    OrderResponseDto getOrderById(Long userId, Long orderId);

    /**
     * Recibe la notificación (webhook) de Mercado Pago y actualiza el estado de la orden.
     * MP envía: { "type": "payment", "data": { "id": "12345678" } }
     */
    void handleWebhook(Map<String, Object> payload);
}
