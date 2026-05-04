package com.velas.candil.models.order;

import com.velas.candil.entities.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDto(
        Long id,
        BigDecimal total,
        OrderStatus status,
        String checkoutUrl,
        String mercadoPagoPreferenceId,
        List<OrderItemResponseDto> items,
        LocalDateTime createdAt
) {}
