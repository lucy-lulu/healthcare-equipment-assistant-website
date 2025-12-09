package com.unimelbproject.healthcareequipmentassistant.config;

import com.unimelbproject.healthcareequipmentassistant.models.User;
import com.unimelbproject.healthcareequipmentassistant.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Data Initializer Component
 * Automatically creates test users when the application starts
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if no users exist
        if (userRepo.count() == 0) {
            System.out.println("🔄 Initializing test users...");

            // Create test users
            createUser("testuser", "testpassword", "testuser@example.com", User.UserRole.sales);
            createUser("partner_jane", "Password1!", "jane@partner.com", User.UserRole.partner);
            createUser("sales_john", "Password1!", "john@novis.com", User.UserRole.sales);
            createUser("ot_emily", "Password1!", "emily@ot.org", User.UserRole.ot);
            createUser("admin_steve", "Password1!", "steve@novis.com", User.UserRole.admin);

            System.out.println("✅ Test users initialized successfully!");
            System.out.println("📋 Available login accounts:");
            System.out.println("   - testuser / testpassword (sales)");
            System.out.println("   - partner_jane / Password1! (partner)");
            System.out.println("   - sales_john / Password1! (sales)");
            System.out.println("   - ot_emily / Password1! (ot)");
            System.out.println("   - admin_steve / Password1! (admin)");
        }
    }

    private void createUser(String username, String password, String email, User.UserRole role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        userRepo.save(user);
    }
}