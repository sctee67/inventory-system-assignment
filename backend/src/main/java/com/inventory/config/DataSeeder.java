package com.inventory.config;

import com.inventory.entity.Product;
import com.inventory.entity.User;
import com.inventory.entity.enums.Role;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded, skipping.");
            return;
        }

        log.info("Seeding demo data...");

        User demo = new User();
        demo.setUsername("demo");
        demo.setEmail("demo@example.com");
        demo.setPassword(passwordEncoder.encode("password123"));
        demo.setFullName("Demo User");
        demo.setRole(Role.USER);
        userRepository.save(demo);

        createProduct("MacBook Pro 16\"", "Apple M3 Pro chip, 18GB RAM, 512GB SSD", "Electronics", "ELEC-MBP-001", 25, new BigDecimal("2499.99"), demo);
        createProduct("Dell UltraSharp 27\" Monitor", "4K UHD, USB-C, IPS Panel", "Electronics", "ELEC-MON-002", 8, new BigDecimal("549.99"), demo);
        createProduct("Wireless Keyboard", "Bluetooth mechanical keyboard, compact layout", "Electronics", "ELEC-KEY-003", 45, new BigDecimal("79.99"), demo);
        createProduct("USB-C Hub Adapter", "7-in-1 multiport adapter with HDMI", "Electronics", "ELEC-HUB-004", 0, new BigDecimal("34.99"), demo);
        createProduct("Ergonomic Office Chair", "Adjustable lumbar support, mesh back", "Furniture", "FURN-CHR-001", 12, new BigDecimal("399.99"), demo);
        createProduct("Standing Desk", "Electric height adjustable, 60\" x 30\"", "Furniture", "FURN-DSK-002", 3, new BigDecimal("599.99"), demo);
        createProduct("A4 Copy Paper (5 Reams)", "80gsm white multipurpose paper", "Office Supplies", "OFFC-PPR-001", 150, new BigDecimal("24.99"), demo);
        createProduct("Ballpoint Pen Set (50 pack)", "Medium point, blue ink", "Office Supplies", "OFFC-PEN-002", 5, new BigDecimal("12.99"), demo);
        createProduct("Cordless Drill Set", "20V lithium-ion, 68-piece accessories", "Tools", "TOOL-DRL-001", 0, new BigDecimal("89.99"), demo);
        createProduct("Safety Glasses (12 pack)", "ANSI Z87.1 certified, anti-fog", "Tools", "TOOL-SGS-002", 30, new BigDecimal("29.99"), demo);

        log.info("Demo data seeded: 1 user, {} products", productRepository.count());
        log.info("Login with username: demo / password: password123");
    }

    private void createProduct(String name, String description, String category, String sku,
                               int quantity, BigDecimal price, User user) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setCategory(category);
        product.setSku(sku);
        product.setQuantity(quantity);
        product.setPrice(price);
        product.setUser(user);
        product.updateStatus();
        productRepository.save(product);
    }
}
