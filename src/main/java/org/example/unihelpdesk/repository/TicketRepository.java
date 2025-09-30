package org.example.unihelpdesk.repository;

import org.example.unihelpdesk.model.Ticket;
import org.example.unihelpdesk.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findByStatus(String status);
    List<Ticket> findByStatusIn(List<String> statuses);
    List<Ticket> findByAssignedTo(User assignedUser);
    List<Ticket> findByStudent(User student);

}