package org.example.unihelpdesk.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class TicketDTO {
    private String studentContact;
    private String category;
    private Integer moduleId;
    private String subject;
    private String message;
    private MultipartFile[] attachments;
}
