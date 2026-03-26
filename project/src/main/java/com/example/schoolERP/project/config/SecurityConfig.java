package com.example.schoolERP.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.example.schoolERP.project.customHandler.CustomSuccessHandler;
import com.example.schoolERP.project.repository.UserRepository;
import com.example.schoolERP.project.service.CustonUserDetailsService;

@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;
    private final CustomSuccessHandler customSuccessHandler;

    public SecurityConfig(UserRepository userRepository,
                          CustomSuccessHandler customSuccessHandler) {
        this.userRepository = userRepository;
        this.customSuccessHandler = customSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustonUserDetailsService(userRepository);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // IMPORTANT LINE
            .authenticationProvider(daoAuthenticationProvider())

            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/", "/login", "/register", "/css/**", "/js/**").permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/faculty/**").hasAnyRole("FACULTY","ADMIN")
                    .requestMatchers("/student/**").hasAnyRole("STUDENT","FACULTY","ADMIN")
                    .anyRequest().authenticated()
            )

            .formLogin(login -> login
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .successHandler(customSuccessHandler)
                    .permitAll()
            )

            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
            );

        return http.build();
    }
}