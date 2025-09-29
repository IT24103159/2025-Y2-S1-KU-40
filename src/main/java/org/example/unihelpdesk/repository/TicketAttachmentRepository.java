package org.example.unihelpdesk.repository;

import org.example.unihelpdesk.model.TicketAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface TicketAttachmentRepository extends JpaRepository<TicketAttachment, Integer> {}