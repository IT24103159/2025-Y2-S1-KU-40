package org.example.unihelpdesk.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Integer userId;
    private String universityId;
    private String password;
    private String email;
    private String name; // Combined name
    private String role; // Role can be "Student", "Lecturer", or a staff type like "IT_Support"
}