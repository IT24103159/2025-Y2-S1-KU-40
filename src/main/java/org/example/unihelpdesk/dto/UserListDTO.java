package org.example.unihelpdesk.dto;

import lombok.Data;

@Data
public class UserListDTO {
    private String universityId;
    private String name;
    private String email;
    private String specificRole;
    private String facultyName;


    public UserListDTO() {}
    
    public UserListDTO(String universityId, String name, String email, String specificRole, String facultyName) {
        this.universityId = universityId;
        this.name = name;
        this.email = email;
        this.specificRole = specificRole;
        this.facultyName = facultyName;
    }

    public UserListDTO(String universityId, String name, String email, String specificRole) {
    }
}