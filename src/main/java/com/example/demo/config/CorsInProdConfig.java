package com.example.demo.config;

import com.example.demo.util.FilterChainRing;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Profile({"prod"})
@Configuration
@RequiredArgsConstructor
public class CorsInProdConfig {
    @Value("${cors.allowed.origin}")
    private String corsAllowedOrigins;

    @Bean
    public FilterChainRing configureCorsConfig() {
        return http -> http
                .authorizeHttpRequests(request -> request
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                )
                .cors(corsConfigurationSourceForTest());
    }

    @Bean
    public Customizer<CorsConfigurer<HttpSecurity>> corsConfigurationSourceForTest() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://main-project-team-4-frontend.vercel.app", "https://www.re-use.store", "http://localhost:5173", "http://localhost:3000", "http://reuse.kro.kr", "https://reuse.kro.kr"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Authorization_Refresh", "Cache-Control", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Authorization_Refresh"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return builder -> builder.configurationSource(source);
    }
}
