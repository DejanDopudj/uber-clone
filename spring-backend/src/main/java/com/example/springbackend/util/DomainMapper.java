package com.example.springbackend.util;

import com.example.springbackend.dto.display.VehicleDisplayDTO;
import com.example.springbackend.model.Vehicle;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DomainMapper {
    @Bean
    public ModelMapper ModelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        configure(modelMapper);
        addConverters(modelMapper);

        return modelMapper;
    }
    private void configure(ModelMapper modelMapper) {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
    }


    private void addConverters(ModelMapper modelMapper) {

    }
}
