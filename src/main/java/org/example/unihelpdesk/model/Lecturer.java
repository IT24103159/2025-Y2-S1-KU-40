package org.example.unihelpdesk.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "lecturers")
@Data
public class Lecturer {
    @Id
    private Integer userId;
}