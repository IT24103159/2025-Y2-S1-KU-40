package org.example.unihelpdesk.dto;

public class KnowledgeBaseArticleDTO {

    private Integer articleId;
    private String title;
    private String content;
    private String category;

    // --- Getters and Setters ---

    public Integer getArticleId() { return articleId; }
    public void setArticleId(Integer articleId) { this.articleId = articleId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}