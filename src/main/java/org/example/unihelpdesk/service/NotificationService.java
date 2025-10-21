package org.example.unihelpdesk.service;

import org.example.unihelpdesk.model.Notification;
import org.example.unihelpdesk.model.User;

import java.util.List;

public interface NotificationService {

    /**
     * Observer Pattern එකේ "notify" method එක.
     * Subject (උදා: TicketService) එකෙන් මේක call කරාම, අදාළ user ට
     * database එකේ අලුත් notification එකක් නිර්මාණය කරනවා.
     *
     * @param user    Notification එක ලැබිය යුතු User (The Observer)
     * @param message පණිවිඩය
     * @param link    Click කළාම යා යුතු URL එක
     */
    void createNotification(User user, String message, String link);

    /**
     * User කෙනෙක්ට අදාළ, කියවා නැති (unread) notifications ටික ගන්නවා.
     * (Header එකේ bell icon එක click කරාම පෙන්වන්න)
     */
    List<Notification> getUnreadNotificationsForUser(Integer userId);

    /**
     * User කෙනෙක්ට අදාළ, කියවා නැති (unread) notifications ගණන ගන්නවා.
     * (Header එකේ unread count එකට)
     */
    long getUnreadNotificationCount(Integer userId);

    /**
     * Notification එකක් "කියවපු" (read) එකක් විදියට update කරනවා.
     */
    void markNotificationAsRead(Integer notificationId);

    Notification getNotificationByIdAndUser(Integer notificationId, Integer userId);

    List<Notification> getAllNotificationsForUser(Integer userId);
}