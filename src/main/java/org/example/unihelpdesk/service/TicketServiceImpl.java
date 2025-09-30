package org.example.unihelpdesk.service;

import org.example.unihelpdesk.dto.TicketDTO;
import org.example.unihelpdesk.dto.TicketListDTO;
import org.example.unihelpdesk.model.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.unihelpdesk.model.*;
import org.example.unihelpdesk.repository.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;
import org.example.unihelpdesk.dto.ViewTicketDTO;
import java.util.ArrayList;


@Service
public class TicketServiceImpl implements TicketService {
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired private ModuleRepository moduleRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TicketAttachmentRepository attachmentRepository;
    @Autowired private FileStorageService fileStorageService;
    @Autowired private TicketResponseRepository responseRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private EncryptionService encryptionService;
    @Autowired private UserService userService;



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

    @Override
    public ViewTicketDTO getTicketDetails(Integer ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));

        ViewTicketDTO dto = new ViewTicketDTO();

        // Ticket Details
        dto.setTicketId(ticket.getTicketId());
        dto.setSubject(ticket.getSubject());
        dto.setMessage(ticket.getMessage());
        dto.setCategory(ticket.getCategory());
        dto.setStatus(ticket.getStatus());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setContactNumber(ticket.getStudentContact());
        dto.setAttachments(ticket.getAttachments());

        // Student Details
        User studentUser = ticket.getStudent();
        dto.setStudentUniversityId(studentUser.getUniversityId());
        dto.setStudentName(studentUser.getName());
        dto.setStudentEmail(studentUser.getEmail());

        // Student's Faculty
        Student studentDetails = studentRepository.findById(studentUser.getUserId()).orElse(null);
        if (studentDetails != null && studentDetails.getFaculty() != null) {
            dto.setStudentFaculty(studentDetails.getFaculty().getFacultyName());
        } else {
            dto.setStudentFaculty("N/A");
        }

        // Module Details
        if (ticket.getModule() != null) {
            dto.setModuleName(ticket.getModule().getModuleName());
        }

        return dto;
    }

    @Override
    @Transactional
    public void handleBySelf(Integer ticketId, String responseMessage, User officer) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();
        ticket.setStatus("Answered"); // Status එක Open කරනවා
        ticket.setAssignedTo(officer); // තමන්ටම assign කරගන්නවා
        ticketRepository.save(ticket);

        TicketResponse response = new TicketResponse();
        response.setTicket(ticket);
        response.setResponder(officer);
        response.setResponseMessage(responseMessage);
        responseRepository.save(response);
    }

    @Override
    @Transactional
    public void assignTicket(Integer ticketId, Integer assignToUserId, User officer) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));

        User assignedUser = userRepository.findById(assignToUserId)
                .orElseThrow(() -> new RuntimeException("User to assign not found with ID: " + assignToUserId));

        ticket.setStatus("Assigned");
        ticket.setAssignedTo(assignedUser);
        ticketRepository.save(ticket);
    }

    @Override
    public List<TicketListDTO> getOpenTickets() {
        // "Open" Tickets යනු "Assigned" සහ "Answered" කළ ටිකට් වේ
        List<String> openStatuses = Arrays.asList("Assigned", "Answered");
        List<Ticket> tickets = ticketRepository.findByStatusIn(openStatuses);

        return tickets.stream().map(ticket -> new TicketListDTO(
                ticket.getTicketId(),
                ticket.getStudent().getUniversityId(),
                ticket.getCategory(),
                ticket.getCreatedAt()
        )).collect(Collectors.toList());
    }

    @Override
    public List<TicketResponse> getResponsesByOfficer(Integer officerId) {
        User officer = userRepository.findById(officerId)
                .orElseThrow(() -> new RuntimeException("Officer not found with ID: " + officerId));
        return responseRepository.findByResponder(officer);
    }

    @Override
    public List<TicketListDTO> getTicketsAssignedToUser(Integer userId) {
        User assignedUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // "Assigned" status එකේ තියෙන tickets විතරක් ගන්නවා
        List<Ticket> tickets = ticketRepository.findByAssignedTo(assignedUser).stream()
                .filter(ticket -> "Assigned".equals(ticket.getStatus()))
                .collect(Collectors.toList());

        return tickets.stream().map(ticket -> new TicketListDTO(
                ticket.getTicketId(),
                ticket.getStudent().getUniversityId(),
                ticket.getCategory(),
                ticket.getCreatedAt()
        )).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addResponseByStaff(Integer ticketId, String responseMessage, User staffMember) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));

        // Security check: Ticket එක මේ staff member ට assign කරලද කියලා බලනවා
        if (!ticket.getAssignedTo().getUserId().equals(staffMember.getUserId())) {
            throw new SecurityException("You are not authorized to respond to this ticket.");
        }

        ticket.setStatus("Answered"); // Status එක "Answered" කරනවා
        ticketRepository.save(ticket);

        TicketResponse response = new TicketResponse();
        response.setTicket(ticket);
        response.setResponder(staffMember);
        response.setResponseMessage(responseMessage);
        responseRepository.save(response);
    }

    @Override
    @Transactional
    public void createCounselingTicket(TicketDTO ticketDTO, User student) throws Exception {
        // Find an available counsellor (You can improve this logic, e.g., round-robin)
        List<User> counselors = userService.findStaffByType("Counselor");
        if (counselors.isEmpty()) {
            throw new Exception("No counsellors are available at the moment.");
        }
        User assignedCounselor = counselors.get(0); // Assign to the first available counsellor

        Ticket ticket = new Ticket();
        ticket.setStudent(student);
        ticket.setSubject(ticketDTO.getSubject());

        // **** Encrypt the message before saving ****
        String encryptedMessage = encryptionService.encrypt(ticketDTO.getMessage());
        ticket.setMessage(encryptedMessage);

        ticket.setCategory("Counseling_Support");
        ticket.setStatus("Assigned"); // Directly assign, no "Unassigned" state
        ticket.setAssignedTo(assignedCounselor);

        ticketRepository.save(ticket);
    }

    @Override
    public ViewTicketDTO getDecryptedTicketDetails(Integer ticketId) {
        ViewTicketDTO dto = getTicketDetails(ticketId); // Get normal details
        if ("Counseling_Support".equals(dto.getCategory())) {
            dto.setMessage(encryptionService.decrypt(dto.getMessage()));
        }
        return dto;
    }

    @Override
    @Transactional
    public void addEncryptedResponseByStaff(Integer ticketId, String responseMessage, User staffMember) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();
        if ("Counseling_Support".equals(ticket.getCategory())) {
            responseMessage = encryptionService.encrypt(responseMessage);
        }
        addResponseByStaff(ticketId, responseMessage, staffMember); // Call original method
    }

    @Override
    public List<TicketResponse> getDecryptedResponsesByOfficer(Integer officerId) {
        List<TicketResponse> responses = getResponsesByOfficer(officerId);
        responses.forEach(response -> {
            if ("Counseling_Support".equals(response.getTicket().getCategory())) {
                response.setResponseMessage(encryptionService.decrypt(response.getResponseMessage()));
            }
        });
        return responses;
    }
}
