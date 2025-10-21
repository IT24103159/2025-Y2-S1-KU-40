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

    // --- Role එක අනුව Dashboard හොයන්න මේ දෙක ඕන ---
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


    // =========================================================================
    //         ## මෙන්න දෝෂය නිවැරදි කරපු අලුත් Method එක ##
    // =========================================================================
    @GetMapping("/notifications/read/{id}")
    public RedirectView markAsReadAndRedirect(@PathVariable("id") Integer notificationId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) {
            return new RedirectView("/login");
        }

        // 1. User ගේ Role එකට අදාළ Default Dashboard URL එක හොයාගන්නවා
        String defaultDashboardUrl = getDefaultDashboardUrl(userId);

        try {
            // 2. අලුත්, ශක්තිමත් (robust) check එක පාවිච්චි කරනවා
            Notification notification = notificationService.getNotificationByIdAndUser(notificationId, userId);

            if (notification != null) {
                // 3. Notification එක "read" කරනවා (දැනටමත් read නම්, අවුලක් නැහැ)
                notificationService.markNotificationAsRead(notificationId);

                // 4. Notification එකේ තියෙන, නිවැරදි link එකට redirect කරනවා
                return new RedirectView(notification.getLink());
            } else {
                // 5. Notification එක හම්බවුණේ නැත්නම්, Default Dashboard එකට යවනවා
                return new RedirectView(defaultDashboardUrl);
            }
        } catch (Exception e) {
            // 6. වෙනත් Error එකක් ආවත්, Default Dashboard එකට යවනවා
            return new RedirectView(defaultDashboardUrl);
        }
    }

    /**
     * User ගේ ID එකෙන් එයාගේ Role එක හොයලා,
     * අදාළ Dashboard එකේ URL එක return කරන Helper Method එක
     */
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
                    return "/"; // Staff වුණත්, type එකක් නැත්නම් Home
                default:
                    return "/"; // Home page
            }
        } catch (Exception e) {
            return "/login"; // User ව හොයාගන්න බැරිවුණොත් login
        }
    }

    @GetMapping("/notifications/history")
    public String showNotificationHistory(Model model, HttpSession session) {
        // 1. User login වෙලාද කියලා බලනවා
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) {
            return "redirect:/login"; // Login වෙලා නැත්නම්, login page එකට යවනවා
        }

        // 2. Service එකෙන් අදාළ user ගේ සියලුම notifications ගන්නවා
        List<Notification> allNotifications = notificationService.getAllNotificationsForUser(userId);

        // 3. Model එකට notifications list එක එකතු කරනවා
        model.addAttribute("notifications", allNotifications);

        // 4. "notification-history.html" කියන page එක load කරනවා
        return "notification-history";
    }
}