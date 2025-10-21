package org.example.unihelpdesk.repository;

import org.example.unihelpdesk.model.Notification;
import org.example.unihelpdesk.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {


    List<Notification> findByUserAndIsReadOrderByCreatedAtDesc(User user, boolean isRead);


    long countByUserAndIsRead(User user, boolean isRead);

    Optional<Notification> findByNotificationIdAndUser(Integer notificationId, User user);

    List<Notification> findByUserOrderByCreatedAtDesc(User user);
}