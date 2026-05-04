package com.velas.candil.config.mercadopago;

import com.mercadopago.MercadoPagoConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MercadoPagoConfiguration {

    private final MercadoPagoProperties properties;

    /**
     * Configura el SDK de Mercado Pago con el access token del entorno.
     * En sandbox el token empieza con "TEST-".
     */
    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(properties.getAccessToken());
        log.info("Mercado Pago SDK inicializado. Modo: {}",
                properties.getAccessToken().startsWith("TEST") ? "SANDBOX" : "PRODUCCIÓN");
    }
}
