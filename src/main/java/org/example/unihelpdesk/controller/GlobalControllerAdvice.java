package org.example.unihelpdesk.controller;

import jakarta.servlet.http.HttpSession;
import org.example.unihelpdesk.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private HttpSession session;


    @ModelAttribute
    public void addGlobalAttributes(Model model) {


        Integer userId = (Integer) session.getAttribute("loggedInUserId");

        if (userId != null) {

            long unreadCount = notificationService.getUnreadNotificationCount(userId);


            model.addAttribute("unreadNotificationCount", unreadCount);
        }
    }
}