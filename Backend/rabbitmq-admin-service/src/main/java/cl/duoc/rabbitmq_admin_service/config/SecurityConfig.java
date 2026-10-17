
package cl.duoc.rabbitmq_admin_service.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            // Permitir solicitudes desde Angular
            .cors(cors ->
                cors.configurationSource(corsConfigurationSource())
            )

            // API REST sin sesiones
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

                // Proteger las operaciones administrativas
                .requestMatchers("/api/rabbitmq/**")
                    .hasAuthority("SCOPE_Pedidos.ReadWrite")

                // Exigir autenticacion para las demas rutas
                .anyRequest().authenticated()
            )

            // Validar JWT emitidos por Microsoft Entra ID
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> {})
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // Permitir el frontend Angular
        configuration.setAllowedOrigins(
            List.of("http://localhost:4200")
        );

        // Metodos HTTP permitidos
        configuration.setAllowedMethods(
            List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
            )
        );

        // Cabeceras permitidas
        configuration.setAllowedHeaders(
            List.of(
                "Authorization",
                "Content-Type"
            )
        );

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**",
            configuration
        );

        return source;
    }
}
