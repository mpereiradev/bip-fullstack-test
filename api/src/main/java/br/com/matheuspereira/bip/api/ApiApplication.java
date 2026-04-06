package br.com.matheuspereira.bip.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication(scanBasePackages = {"br.com.matheuspereira.bip.api", "br.com.matheuspereira.bip.ejb"})
@EntityScan(basePackages = "br.com.matheuspereira.bip.ejb.entity")
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
