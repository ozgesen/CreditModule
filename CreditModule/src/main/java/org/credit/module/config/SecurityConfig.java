package org.credit.module.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // HTTP güvenlik yapılandırması
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF'yi devre dışı bırakıyoruz (yeni yöntem)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register").permitAll()  // Giriş ve kayıt sayfaları herkese açık
                        .anyRequest().authenticated()  // Diğer tüm istekler için kimlik doğrulama gerekli
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")  // Kendi özel giriş sayfanız
                        .permitAll()  // Tüm kullanıcılar için erişilebilir
                )
                .httpBasic(withDefaults());  // HTTP temel kimlik doğrulaması (deprecated ancak yine de kullanılabilir)

        return http.build();  // Yapılandırmayı tamamlıyoruz
    }

    // Şifrelerin güvenli bir şekilde saklanabilmesi için PasswordEncoder bean'i tanımlıyoruz
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // BCrypt ile şifreleme
    }

    // AuthenticationManager Bean'i tanımlıyoruz
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .build();  // AuthenticationManager nesnesini oluşturuyoruz
    }
}
