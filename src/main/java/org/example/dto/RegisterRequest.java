package org.example.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String ownerName;
    private LocalDate ownerDateOfBirth;
} 