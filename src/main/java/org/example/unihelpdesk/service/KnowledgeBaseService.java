package org.example.unihelpdesk.service;

import org.example.unihelpdesk.dto.KnowledgeBaseArticleDTO;
import org.example.unihelpdesk.model.KnowledgeBaseArticle;
import org.example.unihelpdesk.model.User;
import java.util.List;

public interface KnowledgeBaseService {

    // --- Strategy Pattern  ---
    List<KnowledgeBaseArticle> getArticlesForPublic(); // Strategy 1: Public view
    List<KnowledgeBaseArticle> getArticlesForAuthor(User author); // Strategy 2: Staff view

    // --- Factory Pattern  ---
    void createArticle(KnowledgeBaseArticleDTO dto, User author);

    // ---  CRUD operations ---
    void updateArticle(KnowledgeBaseArticleDTO dto, User author) throws IllegalAccessException;
    void deleteArticle(Integer articleId, User author) throws IllegalAccessException;
    KnowledgeBaseArticle findArticleById(Integer articleId);
    KnowledgeBaseArticleDTO getArticleDTOById(Integer articleId);
}