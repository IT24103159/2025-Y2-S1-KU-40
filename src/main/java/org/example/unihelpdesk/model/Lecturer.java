package org.example.unihelpdesk.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "lecturers")
@Data
public class Lecturer {
    @Id
    private Integer userId;

    @ManyToOne // Many lecturers can belong to One faculty
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;
}