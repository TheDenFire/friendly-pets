package org.example.mapper;

import org.example.dto.PetDTO;
import org.example.entity.Pet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PetMapper {
    
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "friendIds", source = "friends", qualifiedByName = "friendsToIds")
    PetDTO toDTO(Pet pet);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "friends", ignore = true)
    Pet toEntity(PetDTO petDTO);

    @Named("friendsToIds")
    default List<Long> friendsToIds(Set<Pet> friends) {
        if (friends == null) {
            return null;
        }
        return friends.stream()
                .map(Pet::getId)
                .collect(Collectors.toList());
    }
} 