package org.example.unihelpdesk.service;

import org.example.unihelpdesk.dto.*;
import org.example.unihelpdesk.model.User;
import java.util.List;

public interface TicketService {
    void createTicket(TicketDTO ticketDTO, User student);
    List<TicketListDTO> getUnassignedTickets();
}