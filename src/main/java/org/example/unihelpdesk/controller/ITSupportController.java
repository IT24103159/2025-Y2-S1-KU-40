package org.example.unihelpdesk.controller;

import jakarta.servlet.http.HttpSession;
import org.example.unihelpdesk.model.User;
import org.example.unihelpdesk.service.TicketService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/it-support") // මේ controller එකේ எல்லா URLs /it-support වලින් පටන් ගන්නේ
public class ITSupportController {

    private final TicketService ticketService;

    public ITSupportController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // Assign කරපු tickets බලන page එකට
    @GetMapping("/tickets")
    public String getAssignedTickets(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) return "redirect:/login";

        model.addAttribute("tickets", ticketService.getTicketsAssignedToUser(userId));
        return "it-assigned-ticket-list"; // අලුතින් හදන HTML file එක
    }

    // Ticket එකක details බලන page එකට
    @GetMapping("/ticket/view/{ticketId}")
    public String viewTicket(@PathVariable Integer ticketId, Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) return "redirect:/login";

        model.addAttribute("ticket", ticketService.getTicketDetails(ticketId));
        return "it-view-ticket"; // අලුතින් හදන HTML file එක
    }

    // Response එක submit කරනකොට
    @PostMapping("/ticket/respond")
    public String respondToTicket(@RequestParam Integer ticketId,
                                  @RequestParam String responseMessage,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) return "redirect:/login";

        User staffMember = new User(); // We only need the ID for the service method
        staffMember.setUserId(userId);

        try {
            ticketService.addResponseByStaff(ticketId, responseMessage, staffMember);
            redirectAttributes.addFlashAttribute("successMessage", "Response sent successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error sending response: " + e.getMessage());
        }

        return "redirect:/it-support/dashboard";
    }

    // තමන් යවපු responses බලන page එකට
    @GetMapping("/responses")
    public String getMyResponses(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) return "redirect:/login";

        // Help Desk Officer ට හදපු service method එකම මෙතනත් පාවිච්චි කරනවා
        model.addAttribute("responses", ticketService.getResponsesByOfficer(userId));
        return "my-responses-list"; // පරණ HTML file එකම reuse කරනවා
    }
}