package org.example.unihelpdesk.repository;

import org.example.unihelpdesk.model.KnowledgeBaseArticle;
import org.example.unihelpdesk.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBaseArticle, Integer> {


    List<KnowledgeBaseArticle> findByAuthor(User author);
}