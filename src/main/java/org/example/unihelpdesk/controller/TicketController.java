package org.example.unihelpdesk.controller;

import jakarta.servlet.http.HttpSession;
import org.example.unihelpdesk.dto.TicketDTO;
import org.example.unihelpdesk.dto.ViewTicketDTO;
import org.example.unihelpdesk.model.Student;
import org.example.unihelpdesk.model.User;
import org.example.unihelpdesk.repository.ModuleRepository;
import org.example.unihelpdesk.repository.StudentRepository;
import org.example.unihelpdesk.repository.UserRepository;
import org.example.unihelpdesk.service.EncryptionService;
import org.example.unihelpdesk.service.TicketService;
import org.example.unihelpdesk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class TicketController {
    @Autowired private UserService userService;
    @Autowired private TicketService ticketService;
    @Autowired private ModuleRepository moduleRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EncryptionService encryptionService;



    @GetMapping("/student/support")
    public String showCreateTicketForm(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) return "redirect:/login";

        User user = userService.findUserById(userId);
        Student student = studentRepository.findById(userId).orElse(null);

        model.addAttribute("ticket", new TicketDTO());
        model.addAttribute("user", user);
        model.addAttribute("student", student);
        model.addAttribute("modules", moduleRepository.findAll());
        return "student-support";
    }


    @PostMapping("/student/support")
    public String createTicket(@ModelAttribute("ticket") TicketDTO ticketDTO,
                               @RequestParam("attachments") MultipartFile[] attachments,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) {
            return "redirect:/login";
        }

        try {
            User user = userService.findUserById(userId);


            ticketDTO.setAttachments(attachments);


            ticketService.createTicket(ticketDTO, user);

            redirectAttributes.addFlashAttribute("successMessage", "Ticket submitted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error submitting ticket: " + e.getMessage());
        }

        return "redirect:/student/dashboard";
    }


    @GetMapping("/help-desk/tickets")
    public String showUnassignedTickets(Model model) {
        model.addAttribute("tickets", ticketService.getUnassignedTickets());
        return "ticket-list";
    }

    @GetMapping("/help-desk/ticket/{ticketId}")
    public String viewTicket(@PathVariable Integer ticketId, Model model) {
        ViewTicketDTO ticketDetails = ticketService.getTicketDetails(ticketId);
        model.addAttribute("ticket", ticketDetails);


        if ("IT_Support".equals(ticketDetails.getCategory())) {
            model.addAttribute("assignableStaff", userService.findStaffByType("IT_Support"));
        } else if ("Academic_Support".equals(ticketDetails.getCategory())) {

            model.addAttribute("assignableStaff", userRepository.findByRole("Lecturer"));
        }

        return "view-ticket";
    }


    @PostMapping("/help-desk/ticket/respond")
    public String respondToTicket(@RequestParam Integer ticketId, @RequestParam String responseMessage, HttpSession session) {
        User officer = userService.findUserById((Integer) session.getAttribute("loggedInUserId"));
        ticketService.handleBySelf(ticketId, responseMessage, officer);
        return "redirect:/help-desk/tickets";
    }


    @PostMapping("/help-desk/ticket/assign")
    public String assignTicket(@RequestParam Integer ticketId, @RequestParam Integer assignedToUserId, HttpSession session) {
        User officer = userService.findUserById((Integer) session.getAttribute("loggedInUserId"));
        ticketService.assignTicket(ticketId, assignedToUserId, officer);
        return "redirect:/help-desk/tickets";
    }

    @GetMapping("/help-desk/tickets/open")
    public String showOpenTickets(Model model) {
        model.addAttribute("tickets", ticketService.getOpenTickets());
        return "open-ticket-list";
    }

    @GetMapping("/help-desk/responses")
    public String showMyResponses(Model model, HttpSession session) {
        Integer officerId = (Integer) session.getAttribute("loggedInUserId");
        if (officerId == null) {
            return "redirect:/login";
        }
        model.addAttribute("responses", ticketService.getResponsesByOfficer(officerId));
        return "my-responses-list";
    }

    @GetMapping("/student/counseling-support")
    public String showCounselingForm(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) {
            return "redirect:/login";
        }


        User user = userService.findUserById(userId);
        Student student = studentRepository.findById(userId).orElse(null);


        model.addAttribute("ticket", new TicketDTO());
        model.addAttribute("user", user);
        model.addAttribute("student", student);

        return "counseling-support";
    }


    @PostMapping("/student/counseling-support")
    public String createCounselingTicket(@ModelAttribute("ticket") TicketDTO ticketDTO,
                                         HttpSession session,
                                         RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) return "redirect:/login";

        try {
            User student = userService.findUserById(userId);
            ticketService.createCounselingTicket(ticketDTO, student);
            redirectAttributes.addFlashAttribute("successMessage", "Your request has been sent to a counsellor securely.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/student/dashboard";
    }

    @GetMapping("/student/my-tickets")
    public String showMyTickets(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) return "redirect:/login";

        model.addAttribute("tickets", ticketService.getTicketsByStudent(userId));
        return "student-my-tickets";
    }

    @GetMapping("/student/ticket/view/{ticketId}")
    public String viewMyTicketDetails(@PathVariable Integer ticketId, Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) return "redirect:/login";

        ViewTicketDTO ticketDetails = ticketService.getStudentTicketDetailsWithResponses(ticketId);


        if (!ticketDetails.getStudentUniversityId().equals(session.getAttribute("universityId"))) {
            return "redirect:/student/dashboard";
        }

        model.addAttribute("ticket", ticketDetails);
        return "student-view-ticket";
    }
}