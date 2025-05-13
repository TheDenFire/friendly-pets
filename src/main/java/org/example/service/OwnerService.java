package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.OwnerDTO;
import org.example.entity.Color;
import org.example.entity.IEntity;
import org.example.entity.Owner;
import org.example.entity.Pet;
import org.example.mapper.OwnerMapper;
import org.example.repository.OwnerRepository;
import org.example.specification.OwnerSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OwnerService implements IService<OwnerDTO, Long, Owner>{

    private final OwnerRepository ownerRepository;
    private final OwnerMapper ownerMapper;

    @Override
    public OwnerDTO create(OwnerDTO ownerDTO) {
        Owner owner = ownerMapper.toEntity(ownerDTO);
        Owner savedOwner = ownerRepository.save(owner);
        return ownerMapper.toDTO(savedOwner);
    }

    @Override
    public OwnerDTO getById(Long id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        return ownerMapper.toDTO(owner);
    }

    @Override
    public List<OwnerDTO> getAll(Color color,
                                 String breed,
                                 String name,
                                 LocalDate birthday,
                                 LocalDate startDate,
                                 LocalDate endDate) {
        Specification<Owner> spec = Specification
                .where(OwnerSpecifications.hasName(name))
                .and(OwnerSpecifications.hasBirthday(birthday));

        List<Owner> owners = ownerRepository.findAll(spec);
        return owners.stream()
                .map(ownerMapper::toDTO)
                .toList();
    }

    @Override
    public OwnerDTO update(OwnerDTO ownerDTO, Long id) {
        Owner existingOwner = ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Owner updatedOwner = ownerMapper.toEntity(ownerDTO);
        updatedOwner.setId(existingOwner.getId());
        
        Owner savedOwner = ownerRepository.save(updatedOwner);
        return ownerMapper.toDTO(savedOwner);
    }

    @Override
    public void delete(Long id) {
        ownerRepository.deleteById(id);
    }
}