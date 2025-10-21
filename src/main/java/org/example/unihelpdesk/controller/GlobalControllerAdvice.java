package org.example.unihelpdesk.controller;

import jakarta.servlet.http.HttpSession;
import org.example.unihelpdesk.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice // මේකෙන් කියන්නේ මේ class එක global controller එකක් කියලා
public class GlobalControllerAdvice {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private HttpSession session; // Session එක inject කරගන්නවා

    // මේ method එක හැම request එකකදීම run වෙනවා
    @ModelAttribute
    public void addGlobalAttributes(Model model) {

        // User login වෙලාද කියලා බලනවා
        Integer userId = (Integer) session.getAttribute("loggedInUserId");

        if (userId != null) {
            // Login වෙලා නම්, unread count එක හොයනවා
            long unreadCount = notificationService.getUnreadNotificationCount(userId);

            // "unreadNotificationCount" කියන නමින් ඒ ගණන හැම HTML එකකටම යවනවා
            model.addAttribute("unreadNotificationCount", unreadCount);
        }
    }
}