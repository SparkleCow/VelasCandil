package com.velas.candil.controllers;

import com.velas.candil.models.order.OrderResponseDto;
import com.velas.candil.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orders")
@Tag(name = "Orders", description = "Gestion de ordenes y pagos con Mercado Pago")
public class OrderController {

    private final OrderService orderService;

    // ─────────────────────────────────────────────
    //  POST /v1/orders/checkout
    // ─────────────────────────────────────────────
    @Operation(
            summary = "Iniciar checkout",
            description = "Convierte el carrito del usuario en una orden y genera la URL de pago en Mercado Pago Sandbox."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden creada, checkoutUrl lista"),
            @ApiResponse(responseCode = "400", description = "Carrito vacio"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "500", description = "Error al comunicar con Mercado Pago")
    })
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponseDto> checkout(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId) {
        return ResponseEntity.ok(orderService.checkout(userId));
    }

    // ─────────────────────────────────────────────
    //  GET /v1/orders
    // ─────────────────────────────────────────────
    @Operation(
            summary = "Mis ordenes",
            description = "Devuelve todas las ordenes del usuario autenticado, ordenadas de mas reciente a mas antigua."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ordenes"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getMyOrders(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId) {
        return ResponseEntity.ok(orderService.getMyOrders(userId));
    }

    // ─────────────────────────────────────────────
    //  GET /v1/orders/{id}
    // ─────────────────────────────────────────────
    @Operation(
            summary = "Detalle de una orden",
            description = "Devuelve el detalle y estado actual de una orden."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden encontrada"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Parameter(description = "ID de la orden", example = "1")
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(userId, orderId));
    }

    // ─────────────────────────────────────────────
    //  POST /v1/orders/webhook
    //  Publico — sin JWT
    // ─────────────────────────────────────────────
    @Operation(
            summary = "Webhook de Mercado Pago (IPN)",
            description = "Endpoint publico que recibe las notificaciones de pago de Mercado Pago."
    )
    @ApiResponse(responseCode = "200", description = "Notificacion procesada")
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody Map<String, Object> payload) {
        log.info("Webhook MP recibido: {}", payload);
        orderService.handleWebhook(payload);
        return ResponseEntity.ok().build();
    }

    // ─────────────────────────────────────────────
    //  GET /v1/orders/redirect/success
    //  MP redirige aqui tras pago aprobado
    //  Este endpoint redirige al frontend
    //  Publico — sin JWT (MP no envia token)
    // ─────────────────────────────────────────────
    @Operation(summary = "Redirect tras pago aprobado", description = "MP redirige aqui. Este endpoint redirige al frontend Angular.")
    @GetMapping("/redirect/success")
    public ResponseEntity<Void> redirectSuccess(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String payment_id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String preference_id) {

        log.info("MP redirect SUCCESS - orderId: {} | paymentId: {} | status: {}",
                orderId, payment_id, status);

        String frontendUrl = "http://localhost:4200/orders/success"
                + "?orderId=" + (orderId != null ? orderId : "")
                + "&payment_id=" + (payment_id != null ? payment_id : "")
                + "&status=" + (status != null ? status : "approved");

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(frontendUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    // ─────────────────────────────────────────────
    //  GET /v1/orders/redirect/failure
    //  MP redirige aqui tras pago rechazado
    // ─────────────────────────────────────────────
    @Operation(summary = "Redirect tras pago rechazado")
    @GetMapping("/redirect/failure")
    public ResponseEntity<Void> redirectFailure(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String payment_id,
            @RequestParam(required = false) String status) {

        log.info("MP redirect FAILURE - orderId: {} | paymentId: {} | status: {}",
                orderId, payment_id, status);

        String frontendUrl = "http://localhost:4200/orders/failure"
                + "?orderId=" + (orderId != null ? orderId : "")
                + "&status=" + (status != null ? status : "rejected");

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(frontendUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    // ─────────────────────────────────────────────
    //  GET /v1/orders/redirect/pending
    //  MP redirige aqui cuando el pago queda pendiente
    // ─────────────────────────────────────────────
    @Operation(summary = "Redirect tras pago pendiente")
    @GetMapping("/redirect/pending")
    public ResponseEntity<Void> redirectPending(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String payment_id,
            @RequestParam(required = false) String status) {

        log.info("MP redirect PENDING - orderId: {} | paymentId: {} | status: {}",
                orderId, payment_id, status);

        String frontendUrl = "http://localhost:4200/orders/pending"
                + "?orderId=" + (orderId != null ? orderId : "")
                + "&status=" + (status != null ? status : "pending");

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(frontendUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}