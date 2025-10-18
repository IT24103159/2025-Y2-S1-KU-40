package org.example.unihelpdesk.controller;

import jakarta.servlet.http.HttpSession;
import org.example.unihelpdesk.dto.KnowledgeBaseArticleDTO;
import org.example.unihelpdesk.model.KnowledgeBaseArticle;
import org.example.unihelpdesk.model.User;
import org.example.unihelpdesk.service.KnowledgeBaseService;
import org.example.unihelpdesk.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/kb")
public class KnowledgeBaseController {

    private final KnowledgeBaseService kbService;
    private final UserService userService;

    public KnowledgeBaseController(KnowledgeBaseService kbService, UserService userService) {
        this.kbService = kbService;
        this.userService = userService;
    }


    @GetMapping("/manage")
    public String showManagementPage(Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) return "redirect:/login";
        User author = userService.findUserById(userId);

        // Strategy Pattern: Calling the Staff Management Strategy
        model.addAttribute("articles", kbService.getArticlesForAuthor(author));
        return "kb-manage";
    }

    // To show the form for adding a new article
    @GetMapping("/new")
    public String showNewArticleForm(Model model) {
        model.addAttribute("articleDTO", new KnowledgeBaseArticleDTO());
        model.addAttribute("pageTitle", "Add New Article");
        return "kb-form";
    }

    // To show the form for editing an article
    @GetMapping("/edit/{id}")
    public String showEditArticleForm(@PathVariable Integer id, Model model, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) return "redirect:/login";

        try {
            // Calling the Service as the Security check is implemented there
            KnowledgeBaseArticleDTO dto = kbService.getArticleDTOById(id);
            KnowledgeBaseArticle article = kbService.findArticleById(id);

            // Checking if the author of the article is the same as the user trying to edit
            if (!article.getAuthor().getUserId().equals(userId)) {
                return "redirect:/kb/manage";
            }

            model.addAttribute("articleDTO", dto);
            model.addAttribute("pageTitle", "Edit Article");
            return "kb-form";
        } catch (Exception e) {
            return "redirect:/kb/manage";
        }
    }

    // When saving a new Article
    @PostMapping("/save")
    public String saveArticle(@ModelAttribute KnowledgeBaseArticleDTO dto, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) return "redirect:/login";
        User author = userService.findUserById(userId);

        try {
            // Calling the Factory Pattern
            kbService.createArticle(dto, author);
            redirectAttributes.addFlashAttribute("successMessage", "Article saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving article: " + e.getMessage());
        }
        return "redirect:/kb/manage";
    }

    // When updating an edited Article
    @PostMapping("/update")
    public String updateArticle(@ModelAttribute KnowledgeBaseArticleDTO dto, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) return "redirect:/login";
        User author = userService.findUserById(userId);

        try {
            kbService.updateArticle(dto, author);
            redirectAttributes.addFlashAttribute("successMessage", "Article updated successfully!");
        } catch (IllegalAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: You are not authorized to edit this article.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating article: " + e.getMessage());
        }
        return "redirect:/kb/manage";
    }

    // When deleting an Article
    @GetMapping("/delete/{id}")
    public String deleteArticle(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("loggedInUserId");
        if (userId == null) return "redirect:/login";
        User author = userService.findUserById(userId);

        try {
            kbService.deleteArticle(id, author);
            redirectAttributes.addFlashAttribute("successMessage", "Article deleted successfully!");
        } catch (IllegalAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: You are not authorized to delete this article.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting article: " + e.getMessage());
        }
        return "redirect:/kb/manage";
    }
}