package org.example.ownerservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "owners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Column(name = "date_of_birthday")
    private LocalDate dateOfBirth;

    @ElementCollection
    @CollectionTable(name = "owner_pets", joinColumns = @JoinColumn(name = "owner_id"))
    @Column(name = "pet_id")
    private List<Long> petIds = new ArrayList<>();

    @Column(name = "user_id", unique = true)
    private Long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Owner owner = (Owner) o;
        return Objects.equals(id, owner.id) &&
               Objects.equals(name, owner.name) &&
               Objects.equals(dateOfBirth, owner.dateOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dateOfBirth);
    }
} 