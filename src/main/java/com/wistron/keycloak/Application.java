package com.wistron.keycloak;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.wistron.keycloak"})
@MapperScan("com.wistron.keycloak.mapper")
public class Application {


	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}



}
