package com.poptopia.promotion.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PoptopiaConfig {

	@Bean
	public ModelMapper getModelMapper(){
	  return new ModelMapper();
	}
}