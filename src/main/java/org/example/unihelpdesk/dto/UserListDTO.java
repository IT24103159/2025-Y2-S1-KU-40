package org.example.unihelpdesk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserListDTO {
    private String universityId;
    private String name;
    private String email;
    private String specificRole;
}