package org.example.unihelpdesk.service;

import org.example.unihelpdesk.dto.KnowledgeBaseArticleDTO;
import org.example.unihelpdesk.model.KnowledgeBaseArticle;
import org.example.unihelpdesk.model.User;
import org.example.unihelpdesk.repository.KnowledgeBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    @Autowired
    private KnowledgeBaseRepository kbRepository;

    // =================================================================
//         ## Where the Strategy Pattern is implemented ##
// =================================================================
// The Context (Service) calls the relevant strategy based on the user's requirement.

    @Override
    public List<KnowledgeBaseArticle> getArticlesForPublic() {
        // Strategy 1 (Public View): Returns all articles.
        return kbRepository.findAll();
    }

    @Override
    public List<KnowledgeBaseArticle> getArticlesForAuthor(User author) {
        // Strategy 2 (Staff Management View): Returns only the articles belonging to the respective author.
        return kbRepository.findByAuthor(author);
    }

    // =================================================================
    //         ## Where the Factory Pattern is implemented ##
    // =================================================================
    // Takes the simple DTO coming from the Controller,
    // "Creates" (Factory) a complete Article object and saves it.

    @Override
    @Transactional
    public void createArticle(KnowledgeBaseArticleDTO dto, User author) {
        KnowledgeBaseArticle article = new KnowledgeBaseArticle();
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setCategory(dto.getCategory());

        // --- Factory Logic: Adding additional data ---
        article.setAuthor(author);
        // This is handled by @PrePersist

        kbRepository.save(article);
    }

    // =================================================================
    //         ## Other Management (CRUD) Methods ##
    // =================================================================

    @Override
    public KnowledgeBaseArticle findArticleById(Integer articleId) {
        return kbRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found with ID: " + articleId));
    }

    @Override
    public KnowledgeBaseArticleDTO getArticleDTOById(Integer articleId) {
        KnowledgeBaseArticle article = findArticleById(articleId);
        KnowledgeBaseArticleDTO dto = new KnowledgeBaseArticleDTO();
        dto.setArticleId(article.getArticleId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        dto.setCategory(article.getCategory());
        return dto;
    }

    @Override
    @Transactional
    public void updateArticle(KnowledgeBaseArticleDTO dto, User author) throws IllegalAccessException {
        KnowledgeBaseArticle article = findArticleById(dto.getArticleId());

        // Security Check: Checks if the article's author is the same as the user who is editing
        if (!article.getAuthor().getUserId().equals(author.getUserId())) {
            throw new IllegalAccessException("You are not authorized to edit this article.");
        }

        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setCategory(dto.getCategory());
        // article.setUpdatedAt(LocalDateTime.now()); // This is handled by @PreUpdate

        kbRepository.save(article);
    }

    @Override
    @Transactional
    public void deleteArticle(Integer articleId, User author) throws IllegalAccessException {
        KnowledgeBaseArticle article = findArticleById(articleId);

        // Security Check: Checks if the article's author is the same as the user who is deleting
        if (!article.getAuthor().getUserId().equals(author.getUserId())) {
            throw new IllegalAccessException("You are not authorized to delete this article.");
        }

        kbRepository.delete(article);
    }
}