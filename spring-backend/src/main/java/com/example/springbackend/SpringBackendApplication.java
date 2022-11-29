package com.example.springbackend;

import com.example.springbackend.repository.DriverRepository;
import com.example.springbackend.service.TestDataSupplierService;
import com.example.springbackend.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class SpringBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBackendApplication.class, args);
        System.out.println("test");
    }

    @Bean
    CommandLineRunner init(TestDataSupplierService testDataSupplierService) {
        return args -> {
            testDataSupplierService.injectTestData();
        };
    }
}
