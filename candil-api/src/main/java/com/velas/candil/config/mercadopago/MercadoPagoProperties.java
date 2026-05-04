package com.velas.candil.config.mercadopago;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mercadopago")
public class MercadoPagoProperties {

    /**
     * Access token del vendedor (seller) en modo Sandbox.
     * Se obtiene en: https://www.mercadopago.com.co/developers/panel/credentials
     * Formato: TEST-XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX
     */
    private String accessToken;

    /**
     * URL base a la que MP redirige tras éxito. Apunta a tu frontend.
     */
    private String successUrl;

    /**
     * URL base a la que MP redirige tras fallo o rechazo.
     */
    private String failureUrl;

    /**
     * URL base a la que MP redirige cuando el usuario cancela.
     */
    private String pendingUrl;

    /**
     * URL pública donde MP enviará los webhooks (debe ser accesible desde internet).
     * En local usa ngrok: https://xxxx.ngrok.io/v1/orders/webhook
     */
    private String webhookUrl;
}
