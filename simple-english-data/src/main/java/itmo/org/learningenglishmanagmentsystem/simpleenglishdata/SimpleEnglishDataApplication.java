package itmo.org.learningenglishmanagmentsystem.simpleenglishdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"itmo.org.learningenglishmanagmentsystem"})
public class SimpleEnglishDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleEnglishDataApplication.class, args);
    }

}
