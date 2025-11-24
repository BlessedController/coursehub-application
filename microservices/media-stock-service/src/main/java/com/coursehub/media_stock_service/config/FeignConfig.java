package com.coursehub.media_stock_service.config;


import com.coursehub.media_stock_service.client.RetrieveMessageErrorDecoder;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class FeignConfig {

    private final JwtService jwtService;

    @Bean
    public ErrorDecoder errorDecoder() {
        return new RetrieveMessageErrorDecoder();
    }


    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {

            String internalToken = jwtService.generateInternalToken(MEDIA_STOCK_SERVICE_APP_NAME);

            template.header(INTERNAL_TOKEN_HEADER_NAME, "Bearer " + internalToken);
        };
    }
}
