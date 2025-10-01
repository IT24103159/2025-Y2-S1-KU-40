package org.example.unihelpdesk.controller;

import jakarta.servlet.http.HttpSession;
import org.example.unihelpdesk.model.User;
import org.example.unihelpdesk.service.TicketService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model  ;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/counselor")
public class CounselorController {
    private final TicketService ticketService;

    public CounselorController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/tickets")
    public String getAssignedTickets(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) return "redirect:/login";
        model.addAttribute("tickets", ticketService.getTicketsAssignedToUser(userId));
        return "counselor-assigned-ticket-list";
    }

    @GetMapping("/ticket/view/{ticketId}")
    public String viewTicket(@PathVariable Integer ticketId, Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) return "redirect:/login";
        model.addAttribute("ticket", ticketService.getDecryptedTicketDetails(ticketId)); // Decrypted details
        return "counselor-view-ticket";
    }

    @PostMapping("/ticket/respond")
    public String respondToTicket(@RequestParam Integer ticketId, @RequestParam String responseMessage, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) return "redirect:/login";
        User staffMember = new User();
        staffMember.setUserId(userId);
        try {
            ticketService.addEncryptedResponseByStaff(ticketId, responseMessage, staffMember); // Encrypted response
            redirectAttributes.addFlashAttribute("successMessage", "Response sent securely!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/counselor/dashboard";
    }

    @GetMapping("/responses")
    public String getMyResponses(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) return "redirect:/login";
        model.addAttribute("responses", ticketService.getDecryptedResponsesByOfficer(userId)); // Decrypted responses
        return "my-responses-list";
    }
}