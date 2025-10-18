package org.example.unihelpdesk.controller;

import org.example.unihelpdesk.model.KnowledgeBaseArticle;
import org.example.unihelpdesk.service.KnowledgeBaseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PublicController {

    private final KnowledgeBaseService kbService;

    public PublicController(KnowledgeBaseService kbService) {
        this.kbService = kbService;
    }


    @GetMapping("/")
    public String showIndexPage(Model model) {
        // Strategy Pattern: Calling the Public View Strategy
        model.addAttribute("articles", kbService.getArticlesForPublic());
        return "index";
    }

    @GetMapping("/kb/article/{id}")
    public String showArticlePage(@PathVariable("id") Integer id, Model model) {
        try {
            KnowledgeBaseArticle article = kbService.findArticleById(id);
            model.addAttribute("article", article);
            return "kb-public-view-article"; // The new HTML file we are creating
        } catch (Exception e) {
            // If the article cannot be found, redirect to the index page
            return "redirect:/";
        }
    }
}