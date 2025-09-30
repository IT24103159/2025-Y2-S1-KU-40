package org.example.unihelpdesk.repository;

import org.example.unihelpdesk.model.TicketResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.example.unihelpdesk.model.User;
import java.util.List;

@Repository
public interface TicketResponseRepository extends JpaRepository<TicketResponse, Integer> {

    List<TicketResponse> findByResponder(User responder);
}