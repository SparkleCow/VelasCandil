package com.velas.candil.repositories;

import com.velas.candil.entities.order.Order;
import com.velas.candil.entities.order.OrderStatus;
import com.velas.candil.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByCreatedAtDesc(User user);

    List<Order> findByUserAndStatusOrderByCreatedAtDesc(User user, OrderStatus status);

    Optional<Order> findByMercadoPagoPreferenceId(String preferenceId);

    Optional<Order> findByMercadoPagoPaymentId(String paymentId);
}
