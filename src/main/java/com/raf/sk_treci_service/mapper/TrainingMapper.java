package com.raf.sk_treci_service.mapper;

import com.raf.sk_treci_service.domain.Training;
import com.raf.sk_treci_service.dto.TrainingCreateDto;
import com.raf.sk_treci_service.dto.TrainingDto;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapper {
    public TrainingDto trainingToTrainingDto(Training training){
        TrainingDto trainingDto = new TrainingDto();
        trainingDto.setId(training.getId());
        trainingDto.setGym(training.getGym());
        trainingDto.setUserList(training.getUserList());
        trainingDto.setName(training.getName());
        trainingDto.setDate(training.getDate());
        trainingDto.setPrice(training.getPrice());
        trainingDto.setTypeOfTrening(training.getTypeOfTrening());
        trainingDto.setIndividualOrGroup(training.getIndividualOrGroup());
        return trainingDto;
    }

    public Training trainingDtoToTraining(TrainingCreateDto trainingCreateDto){
        Training training = new Training();
        training.setName(trainingCreateDto.getName());
        training.setTypeOfTrening(trainingCreateDto.getTypeOfTrening());
        training.setIndividualOrGroup(trainingCreateDto.getIndividualOrGroup());
        training.setPrice(trainingCreateDto.getPrice());
        training.setDate(trainingCreateDto.getDate());
        training.setUserList("");
        training.setGym(trainingCreateDto.getGym());
        return training;
    }
}
