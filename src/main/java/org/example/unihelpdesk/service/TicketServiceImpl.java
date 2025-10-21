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


    @Autowired
    private NotificationService notificationService;
    @Autowired
    private SupportStaffRepository supportStaffRepository;
    // ================================================================


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

        List<User> helpDeskStaff = userService.findStaffByType("Help_Desk");
        String message = "A new " + savedTicket.getCategory() + " ticket (#" + savedTicket.getTicketId() + ") has been received.";
        String link = "/help-desk/ticket/view/" + savedTicket.getTicketId();


        for (User staff : helpDeskStaff) {
            notificationService.createNotification(staff, message, link);
        }
        // ================================================================


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
        ticket.setStatus("Answered");
        ticket.setAssignedTo(officer);
        ticketRepository.save(ticket);

        TicketResponse response = new TicketResponse();
        response.setTicket(ticket);
        response.setResponder(officer);
        response.setResponseMessage(responseMessage);
        responseRepository.save(response);

        // ================================================================
        //         ## OBSERVER PATTERN: NOTIFY OBSERVER (Student) ##
        // ================================================================
        User student = ticket.getStudent();
        String message = "Your ticket #" + ticket.getTicketId() + " has a new response from " + officer.getName() + ".";
        String link = "/student/ticket/view/" + ticket.getTicketId();
        notificationService.createNotification(student, message, link);
        // ================================================================
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

        // ================================================================
        //         ## OBSERVER PATTERN: NOTIFY OBSERVER (Assigned Staff) ##
        // ================================================================

        String message = "Ticket #" + ticket.getTicketId() + " has been assigned to you by " + officer.getName() + ".";
        String link = "";


        if (assignedUser.getRole().equals("Lecturer")) {
            link = "/lecturer/ticket/view/" + ticket.getTicketId();
        } else if (assignedUser.getRole().equals("Staff")) {
            SupportStaff staff = supportStaffRepository.findById(assignedUser.getUserId()).orElse(null);
            if (staff != null) {
                switch (staff.getStaffType()) {
                    case "IT_Support":
                        link = "/it-support/ticket/view/" + ticket.getTicketId();
                        break;
                    case "Counselor":
                        link = "/counselor/ticket/view/" + ticket.getTicketId();
                        break;
                }
            }
        }


        if (!link.isEmpty()) {
            notificationService.createNotification(assignedUser, message, link);
        }
        // ================================================================
    }

    @Override
    public List<TicketListDTO> getOpenTickets() {

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

        // --- DEBUGGING ---
        System.out.println("DEBUG: Fetching tickets assigned to user: " + assignedUser.getName());


        List<Ticket> tickets = ticketRepository.findByAssignedTo(assignedUser).stream()
                .filter(ticket -> "Assigned".equals(ticket.getStatus()))
                .collect(Collectors.toList());

        // --- DEBUGKING ---
        System.out.println("DEBUG: Found " + tickets.size() + " tickets with 'Assigned' status for this user.");

        return tickets.stream().map(ticket -> new TicketListDTO(
                ticket.getTicketId(),
                ticket.getStudent().getUniversityId(),
                ticket.getCategory(),
                ticket.getCreatedAt(),
                ticket.getStatus()
        )).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addResponseByStaff(Integer ticketId, String responseMessage, User staffMember) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));


        if (!ticket.getAssignedTo().getUserId().equals(staffMember.getUserId())) {
            throw new SecurityException("You are not authorized to respond to this ticket.");
        }

        ticket.setStatus("Answered");
        ticketRepository.save(ticket);

        TicketResponse response = new TicketResponse();
        response.setTicket(ticket);
        response.setResponder(staffMember);
        response.setResponseMessage(responseMessage);
        responseRepository.save(response);

        // ================================================================
        //         ## OBSERVER PATTERN: NOTIFY OBSERVER (Student) ##
        // ================================================================
        User student = ticket.getStudent();
        String message;


        if ("Counseling_Support".equals(ticket.getCategory())) {
            message = "You have received a new confidential response for ticket #" + ticket.getTicketId() + ".";
        } else {
            message = "Your ticket #" + ticket.getTicketId() + " has a new response from " + staffMember.getName() + ".";
        }

        String link = "/student/ticket/view/" + ticket.getTicketId();
        notificationService.createNotification(student, message, link);
        // ================================================================
    }

    @Transactional
    public void createCounselingTicket(TicketDTO ticketDTO, User student) throws Exception {

        List<User> counselors = userService.findStaffByType("Counselor");
        if (counselors.isEmpty()) {
            throw new Exception("No counsellors are available at the moment.");
        }
        User assignedCounselor = counselors.get(0);

        Ticket ticket = new Ticket();
        ticket.setStudent(student);
        ticket.setSubject(ticketDTO.getSubject());

        String encryptedMessage = encryptionService.encrypt(ticketDTO.getMessage());
        ticket.setMessage(encryptedMessage);

        ticket.setCategory("Counseling_Support");


        ticket.setStatus("Assigned");

        ticket.setAssignedTo(assignedCounselor);


        Ticket savedTicket = ticketRepository.save(ticket);

        // ================================================================
        //         ## OBSERVER PATTERN: NOTIFY OBSERVER (Counselor) ##
        // ================================================================
        String message = "A new confidential ticket (#" + savedTicket.getTicketId() + ") has been assigned to you.";
        String link = "/counselor/ticket/view/" + savedTicket.getTicketId();

        notificationService.createNotification(assignedCounselor, message, link);
        // ================================================================
    }

    @Override
    public ViewTicketDTO getDecryptedTicketDetails(Integer ticketId) {
        ViewTicketDTO dto = getTicketDetails(ticketId);
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

        addResponseByStaff(ticketId, responseMessage, staffMember);
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

    @Override
    public List<TicketListDTO> getTicketsByStudent(Integer studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        List<Ticket> tickets = ticketRepository.findByStudent(student);

        return tickets.stream().map(ticket -> new TicketListDTO(
                ticket.getTicketId(),
                ticket.getStudent().getUniversityId(),
                ticket.getCategory(),
                ticket.getCreatedAt(),
                ticket.getStatus() //
        )).collect(Collectors.toList());
    }

    @Override
    public ViewTicketDTO getStudentTicketDetailsWithResponses(Integer ticketId) {

        ViewTicketDTO dto = getDecryptedTicketDetails(ticketId);


        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));

        List<TicketResponse> responses = ticket.getResponses();


        if ("Counseling_Support".equals(ticket.getCategory())) {
            // Create a new list to hold decrypted responses
            List<TicketResponse> decryptedResponses = new ArrayList<>();
            for (TicketResponse response : responses) {
                // Decrypt each response and add to the new list
                String decryptedMessage = encryptionService.decrypt(response.getResponseMessage());
                response.setResponseMessage(decryptedMessage);
                decryptedResponses.add(response);
            }
            dto.setResponses(decryptedResponses);
        } else {

            dto.setResponses(responses);
        }

        return dto;
    }
}