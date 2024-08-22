package com.raf.sk_treci_service.controller;

import com.raf.sk_treci_service.domain.Gym;
import com.raf.sk_treci_service.domain.Training;
import com.raf.sk_treci_service.dto.*;
import com.raf.sk_treci_service.mapper.GymMapper;
import com.raf.sk_treci_service.mapper.TrainingMapper;
import com.raf.sk_treci_service.repository.GymRepository;
import com.raf.sk_treci_service.repository.TrainingRepository;
import com.raf.sk_treci_service.security.CheckSecurity;
import com.raf.sk_treci_service.security.service.TokenService;
import com.raf.sk_treci_service.service.DiscountService;
import com.raf.sk_treci_service.service.GymService;
import com.raf.sk_treci_service.service.TrainingService;
import com.raf.sk_treci_service.sortIt.SortIt;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AllController {
    private GymService gymService;
    private TrainingService trainingService;
    private GymMapper gymMapper;
    private TrainingRepository trainingRepository;
    private DiscountService discountService;
    private TrainingMapper trainingMapper;
    private GymRepository gymRepository;
    private TokenService tokenService;

    public AllController(GymService gymService, GymMapper gymMapper, TrainingMapper trainingMapper, TrainingRepository trainingRepository, GymRepository gymRepository, TokenService tokenService, TrainingService trainingService, DiscountService discountService) {
        this.gymService = gymService;
        this.trainingService = trainingService;
        this.discountService = discountService;
        this.tokenService = tokenService;
        this.gymRepository = gymRepository;
        this.trainingRepository = trainingRepository;
        this.trainingMapper = trainingMapper;
        this.gymMapper = gymMapper;
    }
    ////
    @PostMapping("/training/add")
    @CheckSecurity(roles = {"ROLE_MANAGER", "ROLE_ADMIN"})
    public ResponseEntity<TrainingDto> addTraining(@RequestHeader("Authorization") String authorization, @RequestBody TrainingCreateDto trainingCreateDto) {
        return new ResponseEntity<>(trainingService.add(trainingCreateDto), HttpStatus.OK);
    }

    @GetMapping("/training/all/{id}")
    public ResponseEntity<List<TrainingDto>> findByGymId(@PathVariable("id") Long id){
        List<TrainingDto> trainingDtos = trainingService.findAllFreeByGymId(id);
        return new ResponseEntity<>(trainingDtos, HttpStatus.OK);
    }

    @GetMapping("/training/find-for-user")
    public ResponseEntity<List<TrainingDto>> findTrainingForUser(@RequestHeader("Authorization") String authorization){
        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);
        Long userId = Long.parseLong(claims.get("id").toString());
        List<TrainingDto> trainingDtos = trainingService.findTrainingsForUser(userId);
        return new ResponseEntity<>(trainingDtos, HttpStatus.OK);
    }


    @PostMapping("/training/update")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<TrainingDto> updateTraining(@RequestHeader("Authorization") String authorization, @RequestBody TrainingDto trainingDto) {
        return new ResponseEntity<>(trainingService.update(trainingDto.getId(), trainingDto), HttpStatus.OK);
    }

    @PostMapping("/training/delete")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<TrainingDto> deleteTraining(@RequestHeader("Authorization") String authorization, @RequestBody Long trainingId) {
        trainingService.deleteById(trainingId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/gym/add")
    @CheckSecurity(roles = {"ROLE_MANAGER", "ROLE_ADMIN"})
    public ResponseEntity<GymDto> addGymHall(@RequestHeader("Authorization") String authorization, @RequestBody GymCreateDto gymCreateDto) {
        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);
        Long managerId = Long.parseLong(claims.get("id").toString());
        gymCreateDto.setManagerId(managerId);
        return new ResponseEntity<>(gymService.add(gymCreateDto), HttpStatus.OK);
    }

    @GetMapping("/gym")
    public ResponseEntity<List<GymDto>> findAll(@RequestHeader("Authorization") String authorization){
        List<GymDto> gymList = gymRepository.findAll().stream().map(gymMapper::gymToGymDto).toList();
        return new ResponseEntity<>(gymList, HttpStatus.OK);
    }

    @GetMapping("/gym/manager-find")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<List<TrainingDto>> findAllForManager(@RequestHeader("Authorization") String authorization){
        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);
        Long managerId = Long.parseLong(claims.get("id").toString());
        List<GymDto> gymList = gymRepository.findAllByManagerId(managerId).stream().map(gymMapper::gymToGymDto).toList();
        List<Training> tmpTraining = new ArrayList<>();
        for(GymDto gymDto : gymList){
            tmpTraining.addAll(gymDto.getTrenings());
        }
        List<TrainingDto> trainingDtos = tmpTraining.stream().map(trainingMapper::trainingToTrainingDto).toList();
        return new ResponseEntity<>(trainingDtos, HttpStatus.OK);
    }


    @PutMapping("/gym/update")
    @CheckSecurity(roles = {"ROLE_MANAGER", "ROLE_ADMIN"})
    public ResponseEntity<GymDto> updateGymHall(@RequestHeader("Authorization") String authorization, @RequestBody GymDto gymDto) {
        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);
        Long managerId = Long.parseLong(claims.get("id").toString());
        Gym gym = gymRepository.findGymByManagerId(managerId);
        return new ResponseEntity<>(gymService.update(gym.getId(), gymDto), HttpStatus.OK);
    }


    @PostMapping("/gym/delete")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<GymDto> deleteGymHall(@RequestHeader("Authorization") String authorization, @RequestBody Long id) {
        gymService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping("/filter/findall")
    public ResponseEntity<List<TrainingDto>> findAllTrainings() {
        return new ResponseEntity<>(trainingService.findAll(), HttpStatus.OK);
    }

    @PostMapping("/sessions/sort-by-type/{id}")
    public ResponseEntity<List<TrainingDto>> sortByType(@RequestBody SortIt criteria,@PathVariable("id") Long id) {
        return new ResponseEntity<>(trainingService.sortByType(criteria,id), HttpStatus.OK);
    }

    @PostMapping("/sessions/sort-by-ig/{id}")
    public ResponseEntity<List<TrainingDto>> sortByIG(@RequestBody SortIt criteria,@PathVariable("id") Long id) {
        return new ResponseEntity<>(trainingService.sortByIndividualOrGroup(criteria,id), HttpStatus.OK);
    }

    @PostMapping("/training/zakazi/{id}")
    public ResponseEntity<TrainingDto> scheduleTraining(@RequestHeader("Authorization") String authorization, @PathVariable("id") Long id) {
        // Logic to use userId and trainingId
        // For example, you might need to construct a ZakazivanjeDto object here or directly pass these IDs to the service
        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);
        Long userId = Long.parseLong(claims.get("id").toString());
        return new ResponseEntity<>(trainingService.zakazi(userId, id), HttpStatus.OK);
    }

    @PostMapping("/training/otkazi/{id}")
    public ResponseEntity<TrainingDto> cancelTraining(@RequestHeader("Authorization") String authorization, @PathVariable("id") Long id ) {
        // Logic to use userId and trainingId
        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);
        Long userId = Long.parseLong(claims.get("id").toString());
        return new ResponseEntity<>(trainingService.otkazi(userId, id), HttpStatus.OK);
    }

    @PostMapping("/training/otkazi-manager/{id}")
    public ResponseEntity<TrainingDto> cancelTrainingByManager(@RequestHeader("Authorization") String authorization, @PathVariable("id") Long id ) {
        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);
        Long userId = Long.parseLong(claims.get("id").toString());
        return new ResponseEntity<>(trainingService.otkaziManager(userId, id), HttpStatus.OK);
    }

    @PostMapping("/discount/add")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<DiscountDto> addDiscount(@RequestHeader("Authorization") String authorization, @RequestBody DiscountCreateDto discountCreateDto) {
        return new ResponseEntity<>(discountService.add(discountCreateDto), HttpStatus.OK);
    }

    @PostMapping("/discount/update")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<DiscountDto> updateDiscount(@RequestHeader("Authorization") String authorization, @RequestBody DiscountDto discountDto) {
        return new ResponseEntity<>(discountService.update(discountDto.getId(), discountDto), HttpStatus.OK);
    }

    @PostMapping("/discount/delete")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<DiscountDto> deleteDiscount(@RequestHeader("Authorization") String authorization, @RequestBody Long discountId) {
        discountService.deleteById(discountId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
