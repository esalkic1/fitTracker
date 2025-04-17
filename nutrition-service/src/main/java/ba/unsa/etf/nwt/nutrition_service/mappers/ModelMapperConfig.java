package ba.unsa.etf.nwt.nutrition_service.mappers;

import ba.unsa.etf.nwt.nutrition_service.domain.Food;
import ba.unsa.etf.nwt.nutrition_service.domain.Meal;
import ba.unsa.etf.nwt.nutrition_service.dto.FoodDTO;
import ba.unsa.etf.nwt.nutrition_service.dto.MealDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addMappings(new PropertyMap<Food, FoodDTO>() {
            @Override
            protected void configure() {
                map().setMealId(source.getMeal().getId());
            }
        });

        modelMapper.addMappings(new PropertyMap<Meal, MealDTO>() {
            @Override
            protected void configure() {
                map().setUserId(source.getUser().getId());
            }
        });

        return modelMapper;
    }
}
