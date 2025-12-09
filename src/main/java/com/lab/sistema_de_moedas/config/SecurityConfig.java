package com.lab.sistema_de_moedas.config;

import com.lab.sistema_de_moedas.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ✅ PERMITE TUDO (para testes - depois ajuste!)
                .anyRequest().permitAll()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 🔹 SEUS DOMÍNIOS EXATOS DO VERCEL (copiados da sua imagem)
        configuration.setAllowedOriginPatterns(Arrays.asList(
            // Seus domínios específicos
            "https://moeda-estudantit-front.vercel.app",
            "https://moeda-estudantit-front-cdozeccea-thomaaramos02s-projects.vercel.app",
            
            // Seus novos domínios
            "https://moeda-estudantil-front-thomasramos02s-projects.vercel.app",
            "https://moeda-estudantil-front-git-main-thomasramos02s-projects.vercel.app", 
            "https://moeda-estudantil-front-r9v52a3re-thomasramos02s-projects.vercel.app",
            
            // Wildcards para todos os deploys do Vercel
            "https://*.vercel.app",
            "https://moeda-estudantil-front-*.vercel.app",
            "https://moeda-estudantit-front-*.vercel.app",
            
            // Desenvolvimento local
            "http://localhost:*",
            "http://127.0.0.1:*",
            "http://192.168.*:*"
        ));
        
        // 🔹 Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", 
            "OPTIONS", "PATCH", "HEAD"
        ));
        
        // 🔹 Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",        // Para JWT tokens
            "Content-Type",         // Para JSON
            "Accept",               // Para content negotiation
            "Origin",               // CORS
            "X-Requested-With",     // AJAX
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // 🔹 Headers expostos para o frontend
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Disposition",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));
        
        // 🔹 PERMITE CREDENCIAIS (IMPORTANTE para tokens JWT)
        configuration.setAllowCredentials(true);
        
        // 🔹 Cache de preflight requests (1 hora)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
