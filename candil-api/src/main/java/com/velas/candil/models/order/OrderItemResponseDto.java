package com.velas.candil.models.order;

import java.math.BigDecimal;

public record OrderItemResponseDto(
        Long candleId,
        String candleName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}
