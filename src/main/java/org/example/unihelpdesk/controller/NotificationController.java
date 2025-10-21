package org.example.unihelpdesk.controller;

import jakarta.servlet.http.HttpSession;
import org.example.unihelpdesk.model.Notification;
import org.example.unihelpdesk.model.SupportStaff;
import org.example.unihelpdesk.model.User;
import org.example.unihelpdesk.repository.SupportStaffRepository;
import org.example.unihelpdesk.service.NotificationService;
import org.example.unihelpdesk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;


    @Autowired
    private UserService userService;
    @Autowired
    private SupportStaffRepository supportStaffRepository;
    // ---------------------------------------------

    @GetMapping("/notifications/unread")
    @ResponseBody
    public ResponseEntity<List<Notification>> getUnreadNotifications(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        List<Notification> notifications = notificationService.getUnreadNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }



    @GetMapping("/notifications/read/{id}")
    public RedirectView markAsReadAndRedirect(@PathVariable("id") Integer notificationId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) {
            return new RedirectView("/login");
        }


        String defaultDashboardUrl = getDefaultDashboardUrl(userId);

        try {

            Notification notification = notificationService.getNotificationByIdAndUser(notificationId, userId);

            if (notification != null) {

                notificationService.markNotificationAsRead(notificationId);


                return new RedirectView(notification.getLink());
            } else {

                return new RedirectView(defaultDashboardUrl);
            }
        } catch (Exception e) {

            return new RedirectView(defaultDashboardUrl);
        }
    }


    private String getDefaultDashboardUrl(Integer userId) {
        try {
            User user = userService.findUserById(userId);
            switch (user.getRole()) {
                case "Student":
                    return "/student/dashboard";
                case "Admin":
                    return "/admin/dashboard";
                case "Lecturer":
                    return "/lecturer/dashboard";
                case "Staff":
                    Optional<SupportStaff> staff = supportStaffRepository.findById(userId);
                    if (staff.isPresent()) {
                        switch (staff.get().getStaffType()) {
                            case "IT_Support":
                                return "/it-support/dashboard";
                            case "Help_Desk":
                                return "/help-desk/dashboard";
                            case "Counselor":
                                return "/counselor/dashboard";
                        }
                    }
                    return "/";
                default:
                    return "/"; // Home page
            }
        } catch (Exception e) {
            return "/login";
        }
    }

    @GetMapping("/notifications/history")
    public String showNotificationHistory(Model model, HttpSession session) {

        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) {
            return "redirect:/login";
        }

        List<Notification> allNotifications = notificationService.getAllNotificationsForUser(userId);


        model.addAttribute("notifications", allNotifications);


        return "notification-history";
    }
}