package com.coursehub.media_stock_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MediaStockServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MediaStockServiceApplication.class, args);
    }
}
