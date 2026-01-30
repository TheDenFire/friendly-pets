package org.example.petservice.controller;

import org.example.petservice.dto.PetDto;
import org.example.petservice.entity.Color;
import org.example.petservice.service.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetControllerTest {

    @Mock
    private PetService petService;

    @InjectMocks
    private PetController petController;

    private PetDto testPet;

    @BeforeEach
    void setUp() {
        testPet = new PetDto();
        testPet.setId(1L);
        testPet.setName("TestPet");
        testPet.setType("Dog");
        testPet.setOwnerId(1L);
        testPet.setBirthday(LocalDate.now());
        testPet.setBreed("TestBreed");
        testPet.setColor(Color.BLACK);
    }

    @Test
    void getAllPets() {
        List<PetDto> pets = Arrays.asList(testPet);
        when(petService.getAllPets()).thenReturn(pets);

        ResponseEntity<List<PetDto>> response = petController.getAllPets();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pets, response.getBody());
        verify(petService).getAllPets();
    }

    @Test
    void getPetById() {
        when(petService.getPetById(1L)).thenReturn(testPet);

        ResponseEntity<PetDto> response = petController.getPetById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testPet, response.getBody());
        verify(petService).getPetById(1L);
    }

    @Test
    void createPet() {
        when(petService.createPet(any(PetDto.class))).thenReturn(testPet);

        ResponseEntity<PetDto> response = petController.createPet(testPet);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testPet, response.getBody());
        verify(petService).createPet(eq(testPet));
    }

    @Test
    void updatePet() {
        when(petService.updatePet(eq(1L), any(PetDto.class))).thenReturn(testPet);

        ResponseEntity<PetDto> response = petController.updatePet(1L, testPet);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testPet, response.getBody());
        verify(petService).updatePet(eq(1L), eq(testPet));
    }

    @Test
    void deletePet() {
        ResponseEntity<Void> response = petController.deletePet(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(petService).deletePet(1L);
    }
} 