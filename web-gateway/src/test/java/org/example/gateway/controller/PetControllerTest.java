package org.example.gateway.controller;

import org.example.gateway.dto.PetDTO;
import org.example.gateway.service.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

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

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PetController petController;

    private PetDTO testPet;

    @BeforeEach
    void setUp() {
        testPet = new PetDTO();
        testPet.setId(1L);
        testPet.setName("TestPet");
        testPet.setType("Dog");
        testPet.setOwnerId(1L);
        testPet.setBirthday(LocalDate.now());
        testPet.setBreed("TestBreed");
        testPet.setColor("Black");
    }

    @Test
    void getAllPets() {
        List<PetDTO> pets = Arrays.asList(testPet);
        when(petService.getAllPets()).thenReturn(pets);

        ResponseEntity<List<PetDTO>> response = petController.getAllPets();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pets, response.getBody());
        verify(petService).getAllPets();
    }

    @Test
    void getPetById() {
        when(petService.getPetById(1L)).thenReturn(testPet);

        ResponseEntity<PetDTO> response = petController.getPetById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testPet, response.getBody());
        verify(petService).getPetById(1L);
    }

    @Test
    void updatePet() {
        when(petService.updatePet(eq(1L), any(PetDTO.class))).thenReturn(testPet);

        ResponseEntity<PetDTO> response = petController.updatePet(1L, testPet);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testPet, response.getBody());
        verify(petService).updatePet(eq(1L), eq(testPet));
    }

    @Test
    void addFriendToPet() {
        when(petService.addFriendToPet(1L, 2L)).thenReturn(testPet);

        ResponseEntity<PetDTO> response = petController.addFriendToPet(1L, 2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testPet, response.getBody());
        verify(petService).addFriendToPet(1L, 2L);
    }

    @Test
    void getMyPets() {
        List<PetDTO> pets = Arrays.asList(testPet);
        when(petService.getMyPets()).thenReturn(pets);

        ResponseEntity<List<PetDTO>> response = petController.getMyPets(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pets, response.getBody());
        verify(petService).getMyPets();
    }
} 