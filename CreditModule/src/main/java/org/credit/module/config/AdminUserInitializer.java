package org.credit.module.config;
import org.credit.module.data.AppUser;
import org.credit.module.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserInitializer(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (appUserRepository.findByUsername("admin").isEmpty()) {
            AppUser adminUser = new AppUser();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin")); // Şifreyi şifreleyelim
            adminUser.setRole("ADMIN");

            appUserRepository.save(adminUser);
            System.out.println("Admin user created");
        } else {
            System.out.println("Admin user created.");
        }
    }
}
