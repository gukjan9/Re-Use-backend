package com.example.demo.config;

import com.example.demo.util.FilterChainRing;
import lombok.RequiredArgsConstructor;
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
        configuration.setAllowedOrigins(Arrays.asList("https://main-project-team-4-frontend.vercel.app", "https://www.re-use.store", "http://localhost:5173", "http://localhost:3000", "http://52.78.5.12/api/auth/kakao/callback", "http://localhost:5173/api/auth/kakao/callback", "http://localhost:5173/kakao", "http://52.78.5.12/kakao"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Authorization_Refresh", "Cache-Control", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Authorization_Refresh"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return builder -> builder.configurationSource(source);
    }
}
