package com.rev.g3.i2.ers2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // any request for /<name>.html gets served from classpath:/pages/<name>.html

        registry.addResourceHandler("/*.html")
                .addResourceLocations("classpath:/pages/");
    }
// redirct when logout
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // logout go straight to the login page
        registry.addRedirectViewController("/", "/login.html");
    }
}