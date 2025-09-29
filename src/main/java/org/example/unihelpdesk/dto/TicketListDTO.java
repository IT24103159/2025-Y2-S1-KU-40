package org.example.unihelpdesk.dto;

import lombok.Data;
import java.time.LocalDateTime;
@Data
public class TicketListDTO {
    private Integer ticketId;
    private String studentUniversityId;
    private String category;
    private LocalDateTime createdAt;
}