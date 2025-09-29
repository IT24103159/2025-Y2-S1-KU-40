package org.example.unihelpdesk.controller;

import jakarta.servlet.http.HttpSession;
import org.example.unihelpdesk.dto.TicketDTO;
import org.example.unihelpdesk.model.Student;
import org.example.unihelpdesk.model.User;
import org.example.unihelpdesk.repository.ModuleRepository;
import org.example.unihelpdesk.repository.StudentRepository;
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
}