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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orders")
@Tag(name = "Orders", description = "Gestión de órdenes y pagos con Mercado Pago")
public class OrderController {

    private final OrderService orderService;

    // ─────────────────────────────────────────────
    //  POST /v1/orders/checkout
    //  Convierte el carrito en orden y devuelve la URL de pago
    // ─────────────────────────────────────────────

    @Operation(
            summary = "Iniciar checkout",
            description = "Convierte el carrito del usuario en una orden y genera la URL de pago en Mercado Pago Sandbox. " +
                    "El frontend debe redirigir al usuario a 'checkoutUrl'."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden creada, checkoutUrl lista"),
            @ApiResponse(responseCode = "400", description = "Carrito vacío"),
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
    //  Lista las órdenes del usuario autenticado
    // ─────────────────────────────────────────────

    @Operation(
            summary = "Mis órdenes",
            description = "Devuelve todas las órdenes del usuario autenticado, ordenadas de más reciente a más antigua."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de órdenes"),
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
    //  Consulta el estado de una orden específica
    // ─────────────────────────────────────────────

    @Operation(
            summary = "Detalle de una orden",
            description = "Devuelve el detalle y estado actual de una orden. " +
                    "El frontend puede consultar este endpoint tras ser redirigido por MP para mostrar el resultado."
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
    //  Recibe notificaciones IPN de Mercado Pago
    //  ⚠️ Este endpoint debe ser público (sin JWT)
    // ─────────────────────────────────────────────

    @Operation(
            summary = "Webhook de Mercado Pago (IPN)",
            description = "Endpoint público que recibe las notificaciones de pago de Mercado Pago. " +
                    "No llamar manualmente. Configurar la URL en el panel de MP o en la preferencia."
    )
    @ApiResponse(responseCode = "200", description = "Notificación procesada")
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody Map<String, Object> payload) {
        log.info("Webhook MP recibido: {}", payload);
        orderService.handleWebhook(payload);
        return ResponseEntity.ok().build();
    }
}
