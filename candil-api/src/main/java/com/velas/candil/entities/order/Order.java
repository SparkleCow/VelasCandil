package com.velas.candil.entities.order;

import com.velas.candil.entities.orderItem.OrderItem;
import com.velas.candil.entities.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    /**
     * ID de preferencia de Mercado Pago (preference_id).
     * Se genera al crear la orden y se usa para redirigir al usuario.
     */
    @Column(length = 100)
    private String mercadoPagoPreferenceId;

    /**
     * ID del pago en Mercado Pago (payment_id).
     * Llega a través del webhook cuando el usuario completa el pago.
     */
    @Column(length = 100)
    private String mercadoPagoPaymentId;

    /**
     * URL de pago generada por Mercado Pago Sandbox.
     * Se envía al frontend para redirigir al usuario.
     */
    @Column(length = 500)
    private String checkoutUrl;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = true)
    private LocalDateTime updatedAt;
}
