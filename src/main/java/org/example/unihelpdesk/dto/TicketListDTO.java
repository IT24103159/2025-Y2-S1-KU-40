package org.example.unihelpdesk.dto;

import lombok.Data;
import java.time.LocalDateTime;
@Data
public class TicketListDTO {
    private Integer ticketId;
    private String studentUniversityId;
    private String category;
    private LocalDateTime createdAt;
    private String status;



    public TicketListDTO() {

    }


    public TicketListDTO(Integer ticketId, String studentUniversityId, String category, LocalDateTime createdAt, String status) {
        this.ticketId = ticketId;
        this.studentUniversityId = studentUniversityId;
        this.category = category;
        this.createdAt = createdAt;
        this.status = status;
    }

    public TicketListDTO(Integer ticketId, String studentUniversityId, String category, LocalDateTime createdAt) {
        this.ticketId = ticketId;
        this.studentUniversityId = studentUniversityId;
        this.category = category;
        this.createdAt = createdAt;

    }

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public String getStudentUniversityId() {
        return studentUniversityId;
    }

    public void setStudentUniversityId(String studentUniversityId) {
        this.studentUniversityId = studentUniversityId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}