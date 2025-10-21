package org.example.unihelpdesk.service;

import org.example.unihelpdesk.model.Notification;
import org.example.unihelpdesk.model.User;

import java.util.List;

public interface NotificationService {

    /**
     *
     * @param user
     * @param message
     * @param link
     */
    void createNotification(User user, String message, String link);


    List<Notification> getUnreadNotificationsForUser(Integer userId);


    long getUnreadNotificationCount(Integer userId);


    void markNotificationAsRead(Integer notificationId);

    Notification getNotificationByIdAndUser(Integer notificationId, Integer userId);

    List<Notification> getAllNotificationsForUser(Integer userId);
}