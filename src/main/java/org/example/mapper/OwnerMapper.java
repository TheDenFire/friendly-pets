package org.example.mapper;

import org.example.dto.OwnerDTO;
import org.example.entity.Owner;
import org.example.entity.Pet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OwnerMapper {
    
    @Mapping(target = "petIds", source = "pets", qualifiedByName = "petsToIds")
    OwnerDTO toDTO(Owner owner);

    @Mapping(target = "pets", ignore = true)
    @Mapping(target = "user", ignore = true)
    Owner toEntity(OwnerDTO ownerDTO);

    @Named("petsToIds")
    default List<Long> petsToIds(List<Pet> pets) {
        if (pets == null) {
            return null;
        }
        return pets.stream()
                .map(Pet::getId)
                .collect(Collectors.toList());
    }
} 