export type OrderStatus =
    | 'PENDING'
    | 'IN_PROCESS'
    | 'PAID'
    | 'FAILED'
    | 'CANCELLED'
    | 'REFUNDED'

export interface OrderItemResponseDto {
    candleId: number;
    candleName: string;
    quantity: number;
    unitPrice: number;
    subtotal: number;
}

export interface OrderResponseDto {
    id: number;
    total: number;
    status: OrderStatus;
    checkoutUrl: string | null;
    mercadoPagoPreferenceId: string | null;
    items: OrderItemResponseDto[];
    createdAt: string;
}
