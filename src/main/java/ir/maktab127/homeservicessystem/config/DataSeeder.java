package ir.maktab127.homeservicessystem.config;

import ir.maktab127.homeservicessystem.entity.Admin;
import ir.maktab127.homeservicessystem.entity.enums.Role;
import ir.maktab127.homeservicessystem.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setFirstName("System");
            admin.setLastName("Admin");
            admin.setEmail("admin@system.com");
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setEmailVerified(true);
            adminRepository.save(admin);
            System.out.println("Default admin user created: admin@system.com / Admin123!");
        }
    }
}
