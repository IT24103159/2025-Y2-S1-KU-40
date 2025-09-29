package org.example.unihelpdesk.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "modules")
@Data
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "module_id")
    private Integer moduleId;

    @Column(name = "module_code", nullable = false, unique = true)
    private String moduleCode;

    @Column(name = "module_name", nullable = false)
    private String moduleName;

    @ManyToOne
    @JoinColumn(name = "lecturer_id")
    private User lecturer;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;
}