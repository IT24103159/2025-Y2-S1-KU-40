package org.example.unihelpdesk.service;

import org.example.unihelpdesk.dto.*;
import org.example.unihelpdesk.model.User;
import java.util.List;
import org.example.unihelpdesk.model.TicketResponse;

public interface TicketService {
    void createTicket(TicketDTO ticketDTO, User student);
    List<TicketListDTO> getUnassignedTickets();
    ViewTicketDTO getTicketDetails(Integer ticketId);
    void handleBySelf(Integer ticketId, String responseMessage, User officer);
    void assignTicket(Integer ticketId, Integer assignToUserId, User officer);
    List<TicketListDTO> getOpenTickets();

    List<TicketResponse> getResponsesByOfficer(Integer officerId);

    List<TicketListDTO> getTicketsAssignedToUser(Integer userId);
    void addResponseByStaff(Integer ticketId, String responseMessage, User staffMember);

    void createCounselingTicket(TicketDTO ticketDTO, User student) throws Exception;

    ViewTicketDTO getDecryptedTicketDetails(Integer ticketId);
    void addEncryptedResponseByStaff(Integer ticketId, String responseMessage, User staffMember);
    List<TicketResponse> getDecryptedResponsesByOfficer(Integer officerId);

    List<TicketListDTO> getTicketsByStudent(Integer studentId);
    ViewTicketDTO getStudentTicketDetailsWithResponses(Integer ticketId);
}