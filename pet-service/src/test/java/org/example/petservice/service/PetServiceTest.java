package org.example.petservice.service;

import org.example.petservice.dto.PetDto;
import org.example.petservice.entity.Pet;
import org.example.petservice.entity.Color;
import org.example.petservice.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private PetService petService;

    private Pet testPet;
    private PetDto testPetDto;

    @BeforeEach
    void setUp() {
        testPet = new Pet();
        testPet.setId(1L);
        testPet.setName("TestPet");
        testPet.setType("Dog");
        testPet.setOwnerId(1L);
        testPet.setBirthday(LocalDate.now());
        testPet.setBreed("TestBreed");
        testPet.setColor(Color.BLACK);

        testPetDto = new PetDto();
        testPetDto.setId(1L);
        testPetDto.setName("TestPet");
        testPetDto.setType("Dog");
        testPetDto.setOwnerId(1L);
        testPetDto.setBirthday(LocalDate.now());
        testPetDto.setBreed("TestBreed");
        testPetDto.setColor(Color.BLACK);
    }

    @Test
    void getAllPets() {
        when(petRepository.findAll()).thenReturn(Arrays.asList(testPet));

        List<PetDto> result = petService.getAllPets();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPet.getName(), result.get(0).getName());
        verify(petRepository).findAll();
    }

    @Test
    void getPetById() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));

        PetDto result = petService.getPetById(1L);

        assertNotNull(result);
        assertEquals(testPet.getName(), result.getName());
        verify(petRepository).findById(1L);
    }

    @Test
    void createPet() {
        when(petRepository.save(any(Pet.class))).thenReturn(testPet);

        PetDto result = petService.createPet(testPetDto);

        assertNotNull(result);
        assertEquals(testPet.getName(), result.getName());
        verify(petRepository).save(any(Pet.class));
    }

    @Test
    void updatePet() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(testPet));
        when(petRepository.save(any(Pet.class))).thenReturn(testPet);

        PetDto result = petService.updatePet(1L, testPetDto);

        assertNotNull(result);
        assertEquals(testPet.getName(), result.getName());
        verify(petRepository).findById(1L);
        verify(petRepository).save(any(Pet.class));
    }
} 