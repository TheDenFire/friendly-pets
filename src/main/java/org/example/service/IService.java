package org.example.service;

import org.example.entity.Color;

import java.time.LocalDate;
import java.util.List;

public interface IService<DTO, Long, Entity> {
    DTO getById(Long id);
    List<DTO> getAll(Color color, String breed, String name, LocalDate date, LocalDate startDate, LocalDate endDate);
    DTO create(DTO dto);
    DTO update(DTO dto, Long id);
    void delete(Long id);
}
