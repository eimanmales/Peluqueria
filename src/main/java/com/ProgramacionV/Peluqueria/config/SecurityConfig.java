package com.ProgramacionV.Peluqueria.config;

import com.ProgramacionV.Peluqueria.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // Habilita @PreAuthorize en controllers
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    /**
     * Contraseñas en texto plano (sin encriptación).
     * NOTA: En producción reemplazar por BCryptPasswordEncoder.
     */
    @SuppressWarnings("deprecation")
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // Spring Security 7 (Spring Boot 4): el UserDetailsService va en el constructor
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos y login son públicos
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .requestMatchers("/login", "/login?error", "/login?logout").permitAll()

                // Solo ADMIN puede gestionar usuarios y ver la bitácora
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // ADMIN y RECEPCIONISTA pueden gestionar citas y clientes
                .requestMatchers("/citas/**", "/clientes/**").hasAnyRole("ADMIN", "RECEPCIONISTA")

                // ADMIN y PELUQUERO acceden al área de peluquero
                .requestMatchers("/peluquero/**").hasAnyRole("ADMIN", "PELUQUERO")

                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")        // URL que procesa el POST del formulario
                .defaultSuccessUrl("/home", true)    // Redirige al home después de login exitoso
                .failureUrl("/login?error=true")     // Redirige con error si las credenciales fallan
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            // Manejo de acceso denegado (403)
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/acceso-denegado")
            );

        return http.build();
    }
}
