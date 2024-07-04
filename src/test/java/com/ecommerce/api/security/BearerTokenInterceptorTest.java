package com.ecommerce.api.security;

import com.ecommerce.api.balance.BalanceController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({BalanceController.class, TestConfig.class })
public class BearerTokenInterceptorTest {


    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testValidToken() throws Exception {
        mockMvc.perform(get("/api/balance/{userId}", 1)
                        .header("Authorization", "Bearer validToken1234123123"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testMissingToken() throws Exception {
        mockMvc.perform(get("/api/balance/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testInvalidTokenPrefix() throws Exception {
        mockMvc.perform(get("/api/balance/1")
                        .header("Authorization", "Invalid validToken1234"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testShortToken() throws Exception {
        mockMvc.perform(get("/api/balance/1")
                        .header("Authorization", "Bearer short"))
                .andExpect(status().isUnauthorized());
    }


}

@TestConfiguration
class TestConfig implements WebMvcConfigurer {
    @Bean
    public BearerTokenInterceptor bearerTokenInterceptor() {
        return new BearerTokenInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(bearerTokenInterceptor()).addPathPatterns("/api/**");
    }
}













