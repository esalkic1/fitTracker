package ba.unsa.etf.nwt.workout_service.mappers;

import ba.unsa.etf.nwt.workout_service.domain.*;
import ba.unsa.etf.nwt.workout_service.dto.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addMappings(new PropertyMap<Workout, WorkoutDTO>() {
            @Override
            protected void configure() {
                map().setUserId(source.getUser().getId());
            }
        });

        modelMapper.addMappings(new PropertyMap<WorkoutTemplate, WorkoutTemplateDTO>() {
            @Override
            protected void configure() {
                map().setUserId(source.getUser().getId());
            }
        });

        modelMapper.addMappings(new PropertyMap<ExerciseTemplate, ExerciseTemplateDTO>() {
            @Override
            protected void configure() {
                map().setExerciseDetailsId(source.getExerciseDetails().getId());
                map().setWorkoutTemplateId(source.getWorkoutTemplate().getId());
            }
        });

        modelMapper.addMappings(new PropertyMap<Exercise, ExerciseDTO>() {
            @Override
            protected void configure() {
                map().setWorkoutId(source.getWorkout().getId());
                map().setExerciseDetailsId(source.getExerciseDetails().getId());
            }
        });

        modelMapper.addMappings(new PropertyMap<ExerciseMetadata, ExerciseMetadataDTO>() {
            @Override
            protected void configure() {
                map().setExerciseId(source.getExercise().getId());
            }
        });

        return modelMapper;
    }
}
