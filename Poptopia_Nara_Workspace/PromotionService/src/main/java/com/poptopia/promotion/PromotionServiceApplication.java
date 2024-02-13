package com.poptopia.promotion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "NARASIMHARAO MANNEPALLI REST API", version = "1.0",
description = "NARASIMHARAO MANNEPALLI REST API description...",
contact = @Contact(name = "NARASIMHARAO MANNEPALLI")),
security = {@SecurityRequirement(name = "bearerToken")}
)
public class PromotionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PromotionServiceApplication.class, args);
	}

}
