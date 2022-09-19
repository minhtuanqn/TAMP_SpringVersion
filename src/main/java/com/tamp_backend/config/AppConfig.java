package com.tamp_backend.config;

import com.tamp_backend.resolver.RequestPaginationResolver;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Configuration for application
 */
@Configuration
public class AppConfig implements WebMvcConfigurer {
    /**
     * Add Pagination resolver
     * @param resolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new RequestPaginationResolver());
    }

    /**
     * Config for api information in swagger
     * @return API information
     */
    private ApiInfo apiInfo() {
        return new ApiInfo(
                "TAMP API",
                "API for TAMP platform",
                "1.0",
                "Terms of service",
                new Contact("TAMP Vietnix", "www.tampvietnix.com", "minhtuanqn320@gmail.com"),
                "License of API",
                "API license URL",
                Collections.emptyList());
    }

    /**
     * Build security context
     * @return SecurityContext
     */
    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    /**
     * Config for authorization
     * @return list of security reference
     */
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("Bearer ", authorizationScopes));
    }

    /**
     * config for api key
     * @return API key
     */
    private ApiKey apiKey() {
        return new ApiKey("Bearer ", "X-TAMP-Token", "header");
    }

    /**
     * Config for swagger
     * @return Docket
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * config for model mapper
     * @return Model mapper
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STANDARD);
        return modelMapper;
    }

    /**
     * config cors
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }
}
