package com.velas.candil.config.initializer;

import com.velas.candil.entities.candle.Candle;
import com.velas.candil.entities.user.Role;
import com.velas.candil.entities.user.User;
import com.velas.candil.models.candle.CategoryEnum;
import com.velas.candil.models.candle.FeatureEnum;
import com.velas.candil.models.candle.MaterialEnum;
import com.velas.candil.models.user.RoleEnum;
import com.velas.candil.repositories.CandleRepository;
import com.velas.candil.repositories.RoleRepository;
import com.velas.candil.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final CandleRepository candleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initData() {
        return args -> {
            createRoleIfNotExists(List.of(RoleEnum.USER, RoleEnum.ADMIN));
            createCandlesIfNotExists();
            createAdminUserIfNotExists();
        };
    }

    private void createRoleIfNotExists(List<RoleEnum> roleEnums) {
        roleEnums.forEach(role ->
                roleRepository.findByRole(role)
                        .orElseGet(() ->
                                roleRepository.save(
                                        Role.builder()
                                                .role(role)
                                                .build()
                                )
                        )
        );
    }

    private void createAdminUserIfNotExists() {

        String username = "SparkleAdmin";

        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }

        Role adminRole = roleRepository.findByRole(RoleEnum.ADMIN)
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

        Role userRole = roleRepository.findByRole(RoleEnum.USER)
                .orElseThrow(() -> new RuntimeException("USER role not found"));

        User admin = User.builder()
                .firstName("Sparkle")
                .lastName("Admin")
                .username(username)
                .email("sparkle@admin.com")
                .password(passwordEncoder.encode("123456789"))
                .enabled(true)
                .roles(new java.util.HashSet<>(List.of(adminRole, userRole)))
                .build();

        userRepository.save(admin);
    }

    private void createCandlesIfNotExists() {

        if (candleRepository.count() > 0) return;

        Candle lavanda = Candle.builder()
                .name("Lavender Calm")
                .description("A relaxing lavender scented candle.")
                .principalImage("/images/lavanda.jpg")
                .stock(50)
                .manufacturingCost(new BigDecimal("14000"))
                .profit(new BigDecimal("21000"))
                .price(new BigDecimal("35000"))
                .materialEnums(Set.of(MaterialEnum.SOY_WAX))
                .featureEnums(Set.of(FeatureEnum.SCENTED))
                .categories(Set.of(CategoryEnum.AROMATIC))
                .images(List.of("/images/lavanda.jpg", "/images/lavanda.jpg"))
                .build();

        Candle vainilla = Candle.builder()
                .name("Vanilla Dream")
                .description("Warm vanilla scent for cozy spaces.")
                .principalImage("/images/vanilla.jpg")
                .stock(40)
                .manufacturingCost(new BigDecimal("12800"))
                .profit(new BigDecimal("19200"))
                .price(new BigDecimal("32000"))
                .materialEnums(Set.of(MaterialEnum.BEESWAX))
                .featureEnums(Set.of(FeatureEnum.SCENTED))
                .categories(Set.of(CategoryEnum.DECORATIVE))
                .images(List.of("/images/vanilla.jpg", "/images/vanilla.jpg"))
                .build();

        Candle sandalwood = Candle.builder()
                .name("Sandalwood Ritual")
                .description("Deep woody aroma for meditation and grounding.")
                .principalImage("/images/sandal.jpg")
                .stock(30)
                .manufacturingCost(new BigDecimal("15200"))
                .profit(new BigDecimal("22800"))
                .price(new BigDecimal("38000"))
                .materialEnums(Set.of(MaterialEnum.SOY_WAX))
                .featureEnums(Set.of(FeatureEnum.SCENTED))
                .categories(Set.of(CategoryEnum.AROMATIC))
                .images(List.of("/images/sandal.jpg", "/images/sandal.jpg"))
                .build();

        Candle citrus = Candle.builder()
                .name("Citrus Energy")
                .description("Fresh citrus blend to energize your mornings.")
                .principalImage("/images/citrus.jpg")
                .stock(45)
                .manufacturingCost(new BigDecimal("12000"))
                .profit(new BigDecimal("18000"))
                .price(new BigDecimal("30000"))
                .materialEnums(Set.of(MaterialEnum.SOY_WAX))
                .featureEnums(Set.of(FeatureEnum.SCENTED))
                .categories(Set.of(CategoryEnum.DECORATIVE))
                .images(List.of("/images/citrus.jpg", "/images/citrus.jpg"))
                .build();

        Candle ocean = Candle.builder()
                .name("Ocean Breeze")
                .description("Clean marine scent inspired by coastal winds.")
                .principalImage("/images/ocean.jpg")
                .stock(35)
                .manufacturingCost(new BigDecimal("13600"))
                .profit(new BigDecimal("20400"))
                .price(new BigDecimal("34000"))
                .materialEnums(Set.of(MaterialEnum.BEESWAX))
                .featureEnums(Set.of(FeatureEnum.SCENTED))
                .categories(Set.of(CategoryEnum.DECORATIVE))
                .images(List.of("/images/ocean.jpg", "/images/ocean.jpg"))
                .build();

        Candle cinnamon = Candle.builder()
                .name("Cinnamon Spice")
                .description("Warm spicy aroma perfect for cozy evenings.")
                .principalImage("/images/cinnamon.jpg")
                .stock(25)
                .manufacturingCost(new BigDecimal("14400"))
                .profit(new BigDecimal("21600"))
                .price(new BigDecimal("36000"))
                .materialEnums(Set.of(MaterialEnum.SOY_WAX))
                .featureEnums(Set.of(FeatureEnum.HANDMADE))
                .categories(Set.of(CategoryEnum.DECORATIVE))
                .images(List.of("/images/cinnamon.jpg", "/images/cinnamon.jpg"))
                .build();

        Candle rose = Candle.builder()
                .name("Rose Harmony")
                .description("Soft floral scent for romantic atmospheres.")
                .principalImage("/images/rose.jpg")
                .stock(28)
                .manufacturingCost(new BigDecimal("14800"))
                .profit(new BigDecimal("22200"))
                .price(new BigDecimal("37000"))
                .materialEnums(Set.of(MaterialEnum.BEESWAX))
                .featureEnums(Set.of(FeatureEnum.HANDMADE))
                .categories(Set.of(CategoryEnum.RELIGIOUS))
                .images(List.of("/images/rose.jpg", "/images/rose.jpg"))
                .build();

        Candle coffee = Candle.builder()
                .name("Coffee House")
                .description("Rich roasted coffee scent for focus and comfort.")
                .principalImage("/images/coffee.jpg")
                .stock(50)
                .manufacturingCost(new BigDecimal("13200"))
                .profit(new BigDecimal("19800"))
                .price(new BigDecimal("33000"))
                .materialEnums(Set.of(MaterialEnum.SOY_WAX))
                .featureEnums(Set.of(FeatureEnum.SCENTED))
                .categories(Set.of(CategoryEnum.DECORATIVE))
                .images(List.of("/images/coffee.jpg", "/images/coffee.jpg"))
                .build();

        candleRepository.saveAll(List.of(lavanda, vainilla,
                sandalwood, citrus, ocean, cinnamon, rose, coffee));
    }
}