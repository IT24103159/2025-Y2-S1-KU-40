package org.example.unihelpdesk.service;

import org.example.unihelpdesk.model.Notification;
import org.example.unihelpdesk.model.User;
import org.example.unihelpdesk.repository.NotificationRepository;
import org.example.unihelpdesk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public void createNotification(User user, String message, String link) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setLink(link);
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotificationsForUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        return notificationRepository.findByUserAndIsReadOrderByCreatedAtDesc(user, false);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadNotificationCount(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        return notificationRepository.countByUserAndIsRead(user, false);
    }

    @Override
    @Transactional
    public void markNotificationAsRead(Integer notificationId) {
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setRead(true); // is_read = 1
            notificationRepository.save(notification);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Notification getNotificationByIdAndUser(Integer notificationId, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        return notificationRepository.findByNotificationIdAndUser(notificationId, user)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getAllNotificationsForUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));


        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }
}