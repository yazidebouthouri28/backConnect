package com.camping.projet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.camping.projet.repository", excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.REGEX, pattern = "com\\.camping\\.projet\\.repository\\.mongo\\..*"))
@EnableMongoRepositories(basePackages = "com.camping.projet.repository.mongo")
public class BackConnectApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackConnectApplication.class, args);
    }

}
