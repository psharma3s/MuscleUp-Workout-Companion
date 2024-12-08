package com.techelevator.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // This method configures global CORS settings
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Allow all origins during development (works for any frontend port)
        registry.addMapping("/**")  // Apply to all API paths
                .allowedOrigins("*") // Allow requests from all origins (for development)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow common HTTP methods
                .allowedHeaders("*")  // Allow all headers
                .allowCredentials(true)  // Allow cookies and authorization headers
                .maxAge(3600); // Set maximum cache time for CORS preflight requests (optional)
    }
}
