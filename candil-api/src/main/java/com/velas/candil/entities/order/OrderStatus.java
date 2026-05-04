package com.velas.candil.entities.order;

public enum OrderStatus {
    /**
     * Orden creada, esperando redirección a la pasarela de pago.
     */
    PENDING,

    /**
     * El usuario fue redirigido a Mercado Pago pero aún no ha pagado.
     */
    IN_PROCESS,

    /**
     * Pago aprobado por Mercado Pago.
     */
    PAID,

    /**
     * Pago rechazado por la pasarela.
     */
    FAILED,

    /**
     * Orden cancelada (por el usuario o expirada).
     */
    CANCELLED,

    /**
     * Pago reembolsado.
     */
    REFUNDED
}
