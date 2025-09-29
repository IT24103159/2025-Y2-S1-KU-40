package org.example.unihelpdesk.service;

import org.example.unihelpdesk.dto.TicketDTO;
import org.example.unihelpdesk.dto.TicketListDTO;
import org.example.unihelpdesk.model.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.unihelpdesk.model.*;
import org.example.unihelpdesk.repository.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;


@Service
public class TicketServiceImpl implements TicketService {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired private ModuleRepository moduleRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TicketAttachmentRepository attachmentRepository;
    @Autowired private FileStorageService fileStorageService;


    @Override
    @Transactional
    public void createTicket(TicketDTO ticketDTO, User student) {
        Ticket ticket = new Ticket();
        ticket.setStudent(student);
        ticket.setStudentContact(ticketDTO.getStudentContact());
        ticket.setCategory(ticketDTO.getCategory());
        ticket.setSubject(ticketDTO.getSubject());
        ticket.setMessage(ticketDTO.getMessage());

        if ("Academic_Support".equals(ticketDTO.getCategory()) && ticketDTO.getModuleId() != null) {
            Module module = moduleRepository.findById(ticketDTO.getModuleId()).orElse(null);
            ticket.setModule(module);
        }

        Ticket savedTicket = ticketRepository.save(ticket);


        if (ticketDTO.getAttachments() != null && ticketDTO.getAttachments().length > 0) {
            for (MultipartFile file : ticketDTO.getAttachments()) {
                if (!file.isEmpty()) {
                    String filePath = fileStorageService.save(file);

                    TicketAttachment attachment = new TicketAttachment();
                    attachment.setTicket(savedTicket);
                    attachment.setFileName(file.getOriginalFilename());
                    attachment.setFilePath(filePath);
                    attachment.setFileType(file.getContentType());

                    attachmentRepository.save(attachment);
                }
            }
        }
    }

    @Override
    public List<TicketListDTO> getUnassignedTickets() {
        List<Ticket> tickets = ticketRepository.findByStatus("Unassigned");
        return tickets.stream().map(ticket -> {
            TicketListDTO dto = new TicketListDTO();
            dto.setTicketId(ticket.getTicketId());
            dto.setStudentUniversityId(ticket.getStudent().getUniversityId());
            dto.setCategory(ticket.getCategory());
            dto.setCreatedAt(ticket.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
    }
}
