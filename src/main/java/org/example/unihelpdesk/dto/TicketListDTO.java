package org.example.unihelpdesk.dto;

import lombok.Data;
import java.time.LocalDateTime;
@Data
public class TicketListDTO {
    private Integer ticketId;
    private String studentUniversityId;
    private String category;
    private LocalDateTime createdAt;


    public TicketListDTO() {

    }

    // 3. All-arguments constructor (එකවර දත්ත දාලා object එක හදන්න)
    public TicketListDTO(Integer ticketId, String studentUniversityId, String category, LocalDateTime createdAt) {
        this.ticketId = ticketId;
        this.studentUniversityId = studentUniversityId;
        this.category = category;
        this.createdAt = createdAt;
    }

    // 4. Public Getters and Setters (Thymeleaf වලට දත්ත කියවන්න අත්‍යවශ්‍යම කොටස)
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
}