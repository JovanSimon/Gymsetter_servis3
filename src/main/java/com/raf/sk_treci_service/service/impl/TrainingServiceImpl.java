package com.raf.sk_treci_service.service.impl;

import com.raf.sk_treci_service.domain.Discount;
import com.raf.sk_treci_service.domain.Gym;
import com.raf.sk_treci_service.domain.Training;
import com.raf.sk_treci_service.dto.NotiDto;
import com.raf.sk_treci_service.dto.TrainingCreateDto;
import com.raf.sk_treci_service.dto.TrainingDto;
import com.raf.sk_treci_service.exception.NotFoundException;
import com.raf.sk_treci_service.listener.helper.MessageHelper;
import com.raf.sk_treci_service.mapper.GymMapper;
import com.raf.sk_treci_service.mapper.TrainingMapper;
import com.raf.sk_treci_service.repository.DiscountRepository;
import com.raf.sk_treci_service.repository.GymRepository;
import com.raf.sk_treci_service.repository.TrainingRepository;
import com.raf.sk_treci_service.service.GymService;
import com.raf.sk_treci_service.service.TrainingService;
import com.raf.sk_treci_service.sortIt.SortIt;
import io.github.resilience4j.retry.Retry;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrainingServiceImpl implements TrainingService {
    private TrainingRepository trainingRepository;
    private TrainingMapper trainingMapper;
    private DiscountRepository discountRepository;
    private GymRepository gymRepository;
    private GymMapper gymMapper;
    private GymService gymService;
    private RestTemplate userServiceRestTemplate;
    private JmsTemplate jmsTemplate;
    private String orderDestination;
    private MessageHelper messageHelper;
    private Retry userServiceRetry;

    public TrainingServiceImpl(TrainingRepository trainingRepository, TrainingMapper trainingMapper, GymService gymService,
                               DiscountRepository discountRepository, GymMapper gymMapper, GymRepository gymRepository, RestTemplate userServiceRestTemplate,
                               JmsTemplate jmsTemplate, Retry userServiceRetry, MessageHelper messageHelper, @Value("${destination.notify}") String orderDestination) {
        this.trainingRepository = trainingRepository;
        this.trainingMapper = trainingMapper;
        this.discountRepository = discountRepository;
        this.gymRepository = gymRepository;
        this.userServiceRestTemplate = userServiceRestTemplate;
        this.jmsTemplate = jmsTemplate;
        this.messageHelper = messageHelper;
        this.orderDestination = orderDestination;
        this.userServiceRetry = userServiceRetry;
        this.gymService = gymService;
        this.gymMapper = gymMapper;
    }

    @Override
    public List<TrainingDto> findAllFreeByGymId(Long id) {
        List<Training> trainings = trainingRepository.findTrainingsByGymId(id).stream().toList();
        List<TrainingDto> trainingDtos = new ArrayList<>();
        for (Training training : trainings){
            if(training.getIndividualOrGroup().equals("GROUP") && countCommas(training.getUserList()) < 12){
                trainingDtos.add(trainingMapper.trainingToTrainingDto(training));
            }else if(training.getIndividualOrGroup().equals("INDIVIDUAL") && training.getUserList().equals("")){
                trainingDtos.add(trainingMapper.trainingToTrainingDto(training));
            }
        }

        return trainingDtos;
    }

    @Override
    public List<TrainingDto> findAll() {
        List<Training> trainings = trainingRepository.findAll().stream().toList();
        return trainings.stream().map(trainingMapper::trainingToTrainingDto).filter(trainingDto1 -> {
            if(trainingDto1.getTypeOfTrening().equals("GROUP")){
                return countCommas(trainingDto1.getUserList()) < 12;
            }else if(trainingDto1.getTypeOfTrening().equals("INDIVIDUAL")){
                return trainingDto1.getUserList() == null;
            }
            return true;
        }).toList();
    }

    @Override
    public List<TrainingDto> findTrainingsForUser(Long userId) {
        List<TrainingDto> trainingDtos = trainingRepository.findAll().stream().map(trainingMapper::trainingToTrainingDto).toList();
        List<TrainingDto> trainingsToReturn = new ArrayList<>();
        for (TrainingDto trainingDto : trainingDtos){
            String[] users = trainingDto.getUserList().split(",");
            List<Long> usersIds = new ArrayList<>();
            for (String item : users){
                if (!item.isEmpty())
                    usersIds.add(Long.valueOf(item));
            }

            if(usersIds.contains(userId))
                trainingsToReturn.add(trainingDto);
        }

        return trainingsToReturn;
    }

    public int countCommas(String str) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ',') {
                count++;
            }
        }
        return count;
    }

    @Override
    public List<TrainingDto> sortByTime(SortIt sort) {
        List<TrainingDto> all = findAll();

        Comparator<TrainingDto> comparator = Comparator.comparing(TrainingDto::getDate);
        if(sort.getSortDirection() == SortIt.SortDirection.DESC)
            comparator = comparator.reversed();

        return all.stream().sorted(comparator).collect(Collectors.toList());
    }

    @Override
    public List<TrainingDto> sortByType(SortIt sort,Long id) {
        List<TrainingDto> all = findAllFreeByGymId(id);
        String typeFilter = sort.getSortField();

        return all.stream()
                .filter(trainingDto -> trainingDto.getTypeOfTrening().equals(typeFilter))
                .collect(Collectors.toList());
    }

    @Override
    public List<TrainingDto> sortByIndividualOrGroup(SortIt sort, Long id) {
        List<TrainingDto> all = findAllFreeByGymId(id);
        String typeFilter = sort.getSortField();

        return all.stream()
                .filter(trainingDto -> trainingDto.getIndividualOrGroup().equals(typeFilter))
                .collect(Collectors.toList());
    }

    @Override
    public List<TrainingDto> sortByDay(SortIt sort) {
        List<TrainingDto> all = findAll();
        String dayOfWeek = sort.getSortField().toUpperCase();

//        return all.stream()
//                .filter(training -> {
//                    LocalDate date = training.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//                    DayOfWeek day = date.getDayOfWeek();
//                    String dayName = day.getDisplayName(TextStyle.FULL, Locale.getDefault()).toUpperCase();
//                    return dayName.startsWith(dayOfWeek);
//                }).collect(Collectors.toList());
        return null;
    }

    @Override
    public TrainingDto add(TrainingCreateDto trainingCreateDto) {
        Gym gym = gymRepository.findGymById(trainingCreateDto.getGymId());
        trainingCreateDto.setGym(gym);
        Training training = trainingMapper.trainingDtoToTraining(trainingCreateDto);
        gym.getTrenings().add(training);
        gymService.update(gym.getId(), gymMapper.gymToGymDto(gym));
        trainingRepository.save(training);

        return trainingMapper.trainingToTrainingDto(training);
    }

    @Override
    public TrainingDto update(Long id, TrainingDto trainingDto) {
        Training training = trainingRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Client with id: " + id + " not found.")
        );

        training.setPrice(trainingDto.getPrice());
        if (trainingDto.getName() != null)
            training.setName(trainingDto.getName());
        if (trainingDto.getTypeOfTrening() != null)
            training.setTypeOfTrening(trainingDto.getTypeOfTrening());
        if(trainingDto.getIndividualOrGroup() != null)
            training.setIndividualOrGroup(trainingDto.getIndividualOrGroup());

        return trainingMapper.trainingToTrainingDto(trainingRepository.save(training));
    }

    @Override
    public void deleteById(Long id) {
        trainingRepository.deleteById(id);
    }

    public String getEmail(Long userId) throws NotFoundException {
        System.out.println("[" + System.currentTimeMillis() / 1000 + "] Getting dis for user id: " + userId);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Long> entity = new HttpEntity<>(userId, headers);

        try{
            return userServiceRestTemplate.exchange("http://localhost:8084/users/client/mail/" + userId, HttpMethod.GET, entity, String.class).getBody();
        }catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException(String.format("Client with id: %d not found.", userId));
            throw new RuntimeException("Internal server error.");
        } catch (Exception e) {
            throw new RuntimeException("Internal server error.");
        }
    }

    private int incriseNumOfTermins(Long userId) throws NotFoundException {
        System.out.println("[" + System.currentTimeMillis() / 1000 + "] Getting OVOVOV for user id: " + userId);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Long> entity = new HttpEntity<>(userId, headers);
        try {
            return userServiceRestTemplate.exchange("http://localhost:8084/users/client/incirise/" + userId, HttpMethod.GET, entity, Integer.class).getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException(String.format("user with id: %d not found.", userId));
            throw new RuntimeException("Internal server error.");
        } catch (Exception e) {
            throw new RuntimeException("Internal server error.");
        }
    }

    private String getManagerEmail(Long userId) throws NotFoundException {
        System.out.println("[" + System.currentTimeMillis() / 1000 + "] Getting dis for manager id: " + userId);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Long> entity = new HttpEntity<>(userId, headers);
        try {
            return userServiceRestTemplate.exchange("http://localhost:8084/users/manager/mail/" + userId, HttpMethod.GET, entity, String.class).getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException(String.format("Manager with id: %d not found.", userId));
            throw new RuntimeException("Internal server error.");
        } catch (Exception e) {
            throw new RuntimeException("Internal server error.");
        }
    }

    private int getNumberOfTermins(Long userId) throws NotFoundException {
        System.out.println("[" + System.currentTimeMillis() / 1000 + "] Getting temrin count for user id: " + userId);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Long> entity = new HttpEntity<>(userId, headers);

        try {
            return userServiceRestTemplate.exchange("http://localhost:8084/users/client/num-of-termins/" + userId, HttpMethod.GET, entity, Integer.class).getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException(String.format("Client with id: %d not found.", userId));
            throw new RuntimeException("Internal server error.");
        } catch (Exception e) {
            throw new RuntimeException("Internal server error.");
        }
    }

    private int decrementNumberOfTermins(Long userId) throws NotFoundException {
        System.out.println("[" + System.currentTimeMillis() / 1000 + "] Decreasing temrin count for user id: " + userId);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Long> entity = new HttpEntity<>(userId, headers);

        try {
            return userServiceRestTemplate.exchange("http://localhost:8084/users/client/decrement/" + userId, HttpMethod.PUT, entity, Integer.class).getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException(String.format("User with id: %d not found.", userId));
            throw new RuntimeException("Internal server error.");
        } catch (Exception e) {
            throw new RuntimeException("Internal server error.");
        }
    }


    @Override
    public TrainingDto zakazi(Long userId, Long trainingId) {
        Training training = trainingRepository.findTrainingById(trainingId);

        if (training==null) {
            System.out.println("Training not found");
            return null;
        }

        int numberOfTrainings = Retry.decorateSupplier(userServiceRetry, () -> {
            try {
                return getNumberOfTermins(userId);
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }).get();

        Discount discount = discountRepository.findDiscountByGymId(training.getGym().getId());

        int price = training.getPrice();

        if (discount != null && discount.getLimitTrainings() < numberOfTrainings)
                price = price - (price / discount.getDiscount());

        String userList = training.getUserList();
        if(training.getIndividualOrGroup().equals("INDIVIDUAL") && userList.isEmpty()){
            userList += userId;
            training.setUserList(userList);
        }else if (training.getIndividualOrGroup().equals("GROUP") && countCommas(userList) < 12) {
            if (userList.isEmpty()) {
                userList += userId;
            } else {
                userList += "," + userId;
            }
            training.setUserList(userList);
        } else {
            return null;
        }

        int incrisedNum = Retry.decorateSupplier(userServiceRetry, () -> {
            try {
                return incriseNumOfTermins(userId);
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }).get();

        Gym gym = gymRepository.findGymById(training.getGym().getId());
        Long managerId = gym.getManagerId();
        String email = Retry.decorateSupplier(userServiceRetry, () -> {
            try {
                return getManagerEmail(managerId);
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }).get();

        String emailUser = Retry.decorateSupplier(userServiceRetry, () -> {
            try {
                return getEmail(userId);
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }).get();

        NotiDto notiDto = new NotiDto(email, "ZAKAZI_TYPE", managerId);
        jmsTemplate.convertAndSend(orderDestination, messageHelper.createTextMessage(notiDto));

        NotiDto notiDtoUser = new NotiDto(emailUser, "ZAKAZI_TYPE", userId);
        jmsTemplate.convertAndSend(orderDestination, messageHelper.createTextMessage(notiDtoUser));

        training = trainingRepository.save(training);

        return trainingMapper.trainingToTrainingDto(training);
    }

    @Override
    public TrainingDto otkazi(Long userId, Long trainingId) {
        Training training = trainingRepository.findTrainingById(trainingId);

        if (training==null) {
            System.out.println("Trening ne postoji");
            return null;
        }

        String userList = training.getUserList();
        if(userList != null && !userList.isEmpty()){
            List<String> users = new ArrayList<>(Arrays.asList(userList.split(",")));
            users.remove(String.valueOf(userId));
            userList = String.join(",", users);
            training.setUserList(userList);
        }

        training = trainingRepository.save(training);

        Retry.decorateSupplier(userServiceRetry, () ->{
            try {
                return decrementNumberOfTermins(userId);
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }).get();

        Gym gym = gymRepository.findGymById(training.getGym().getId());
        Long managerId = gym.getManagerId();
        String email = Retry.decorateSupplier(userServiceRetry, () -> {
            try {
                return getManagerEmail(managerId);
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }).get();

        String emailUser = Retry.decorateSupplier(userServiceRetry, () -> {
            try {
                return getEmail(userId);
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }).get();

        NotiDto notiDto = new NotiDto(email, "OTKAZI_TYPE", managerId);
        jmsTemplate.convertAndSend(orderDestination, messageHelper.createTextMessage(notiDto));

        NotiDto notiDtoUser = new NotiDto(emailUser, "OTKAZI_TYPE", userId);
        jmsTemplate.convertAndSend(orderDestination, messageHelper.createTextMessage(notiDtoUser));
        /// notifikacije za klijenta i managera

        return trainingMapper.trainingToTrainingDto(training);
    }

    @Override
    public TrainingDto otkaziManager(Long managerId, Long trainingId) {
        Training training = trainingRepository.findTrainingById(trainingId);

        if (training == null) {
            System.out.println("Training not found");
            return null;
        }

        String userList = training.getUserList();
        if (userList != null && !userList.isEmpty()) {
            // Send an email to each user that the training is canceled
            String[] userIdArray = userList.split(",");

            for (String userIdStr : userIdArray) {
                Long userIdss = Long.parseLong(userIdStr);
                String email = Retry.decorateSupplier(userServiceRetry, () -> {
                    try {
                        return getEmail(userIdss);
                    } catch (NotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }).get();

                //ovde ide notifikacija za usera
                NotiDto notiDtoUser = new NotiDto(email, "OTKAZI_TYPE", userIdss);
                jmsTemplate.convertAndSend(orderDestination, messageHelper.createTextMessage(notiDtoUser));
                // Decrement the number of scheduled trainings for each user
                Retry.decorateSupplier(userServiceRetry, () -> {
                    try {
                        return decrementNumberOfTermins(userIdss);
                    } catch (NotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }).get();
            }
        }

        trainingRepository.deleteById(trainingId);


        String email = Retry.decorateSupplier(userServiceRetry, () -> {
            try {
                return getManagerEmail(managerId);
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }).get();

        NotiDto notiDto = new NotiDto(email, "OTKAZI_TYPE", managerId);
        jmsTemplate.convertAndSend(orderDestination, messageHelper.createTextMessage(notiDto));

        return trainingMapper.trainingToTrainingDto(training);
    }


}
