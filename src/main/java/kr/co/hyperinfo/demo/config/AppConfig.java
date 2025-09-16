package kr.co.hyperinfo.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class AppConfig {

    @PostConstruct
    public void init() {
        log.info("#### AppConfig.init ####");
    }

    @Bean
    public String exampleBean() {
        log.info("#### AppConfig.exampleBean ####");
        return "This is an example bean";
    }

}
