package org.credit.module.service;

import org.credit.module.data.AppUser;
import org.credit.module.data.Customer;
import org.credit.module.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;

    @Autowired
    private Status status;

    @Autowired
    private CustomerService customerService;

    @Autowired
    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Hash the password
    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    // Verify the password
    public boolean checkPassword(String rawPassword, String username) {
        Optional<AppUser> appUser = appUserRepository.findByUsername(username);
        if (appUser.isPresent()) {
            return passwordEncoder.matches(encryptPassword(rawPassword), appUser.get().getPassword());
        } else {
            return false;
        }
    }

    public Long checkRole(String rawPassword, String username, Long id) {
        //Authorized
        if (checkPassword(username, rawPassword) ) {
            Optional<AppUser> appUser = appUserRepository.findByUsername(username);
            if(appUser.get().getRole() == "ADMIN") {
                return 1L;
            } else {
                Optional<Customer> appUserCustomer = customerService.getCustomer(id);
                if(appUserCustomer.isPresent()) {
                    return appUserCustomer.get().getId();
                } else {
                    return 0L;
                }
            }

        } else {
            return 0L;
        }
    }

    public String getRole(String rawPassword, String username) {
        //Authorized
        if (checkPassword(username, rawPassword) ) {
            Optional<AppUser> appUser = appUserRepository.findByUsername(username);
            if(appUser.get().getRole() == "ADMIN") {
                return "admin";
            } else {
                return "customer";
            }

        } else {
            return "not_user";
        }
    }

    public Long createAppUser(AppUser user) {
        user.setPassword(encryptPassword(user.getPassword()));
        AppUser savedUser = appUserRepository.save(user);
        return savedUser.getId();

    }


}
