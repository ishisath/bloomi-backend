package com.eadcw.FlowerGiftApp.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    /**
     * Create ModelMapper bean
     * ModelMapper is used for DTO to Entity conversions
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}