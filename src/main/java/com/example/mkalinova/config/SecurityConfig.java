package com.example.mkalinova.config;

import com.example.mkalinova.app.user.repo.UserRepository;
import com.example.mkalinova.app.user.service.MyUserDetailsService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {
    private final MyUserDetailsService myUserDetailService;
    private final UserRepository userRepository;

    public SecurityConfig(MyUserDetailsService myUserDetailService, UserRepository userRepository) {
        this.myUserDetailService = myUserDetailService;
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(
                                "/css/**", "/js/**", "/images/**"   // статични ресурси
                        ).permitAll()
                        .requestMatchers("login").permitAll()
                         .anyRequest().permitAll()

//                         .anyRequest().authenticated()
                )
                // стандартният формуляр за логин (вграден)
                .formLogin(formLogin -> {
                    formLogin.loginPage("/login")
                            .usernameParameter("username")
                            .passwordParameter("password")
                            .defaultSuccessUrl("/", true)
                            .failureForwardUrl("/login-error");
                })
                // стандартно logout поведение (/logout)
                .logout(logout -> logout.logoutSuccessUrl("/"))
        // CSRF по подразбиране е включен – добре е за класически уеб форми
        ;
        return http.build();
    }
}
