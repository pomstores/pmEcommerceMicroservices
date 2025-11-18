package com.appGate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main Application Class for PomStores Monolith
 *
 * This unified application consolidates all microservices into a single monolith:
 * - Account Service (payments, wallets, installments)
 * - Inventory Service (products, categories, stock)
 * - RBAC Service (authentication, authorization)
 * - Ordering & Sales Service
 * - Delivery Service
 * - Email Service
 * - Client/Customer Service
 * - Customer Care Service
 * - Cashier Stand Service
 * - Recovery Service
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.appGate.config",       // Unified configs (Security, Swagger, etc.)
    "com.appGate.account",       // Account & payment services
    "com.appGate.cashierstand",  // Cashier stand operations
    "com.appGate.client",        // Client/customer management
    "com.appGate.customercare",  // Customer care services
    "com.appGate.delivery",      // Delivery & rider management
    "com.appGate.email",         // Email notifications
    "com.appGate.inventory",     // Inventory & product management
    "com.appGate.orderingsales", // Orders & sales
    "com.appGate.rbac",          // Authentication & authorization
    "com.appGate.recovery"       // Account recovery
})
public class PomStoresApplication {

	public static void main(String[] args) {
		SpringApplication.run(PomStoresApplication.class, args);
	}

	// Note: OpenAPI/Swagger configuration is now in com.appGate.config.SwaggerConfig
	// Note: Security configuration is now in com.appGate.config.SecurityConfig
}
