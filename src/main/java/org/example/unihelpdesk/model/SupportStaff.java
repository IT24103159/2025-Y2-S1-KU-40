package org.example.unihelpdesk.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "support_staff")
@Data
public class SupportStaff {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "staff_type", nullable = false)
    private String staffType;


}

