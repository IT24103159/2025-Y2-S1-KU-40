package org.example.unihelpdesk.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.unihelpdesk.model.TicketAttachment;
import java.time.LocalDateTime;
import java.util.List;
import org.example.unihelpdesk.model.TicketResponse;

@Data
public class ViewTicketDTO {
    // Ticket Details
    private Integer ticketId;
    private String subject;
    private String message;
    private String category;
    private String status;
    private LocalDateTime createdAt;
    private String contactNumber;

    // Student Details
    private String studentUniversityId;
    private String studentName;
    private String studentEmail;
    private String studentFaculty;

    // Module Details (if applicable)
    private String moduleName;

    // Attachments
    private List<TicketAttachment> attachments;

    @Setter
    @Getter
    private List<TicketResponse> responses;


}