package org.example.unihelpdesk.controller;

import org.example.unihelpdesk.dto.UserDTO;
import org.example.unihelpdesk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.example.unihelpdesk.dto.UserListDTO;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class UserManagementController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String showUserManagementPage() {
        return "user-management";
    }

    @GetMapping("/users/new")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "add-new-user";
    }

    @PostMapping("/users")
    public String addUser(@ModelAttribute("user") UserDTO userDTO, RedirectAttributes redirectAttributes) {
        try {
            userService.createUser(userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "User added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // Show the initial search page
    @GetMapping("/users/manage")
    public String showManageUserPage() {
        return "search-update-remove-user";
    }

    // Handle the search request
    @GetMapping("/users/search")
    public String searchUser(@RequestParam String universityId, Model model, RedirectAttributes redirectAttributes) {
        try {
            UserDTO userDTO = userService.findUserForManagement(universityId);
            model.addAttribute("user", userDTO);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/users/manage";
        }
        return "search-update-remove-user";
    }

    // Handle the update request
    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute("user") UserDTO userDTO, RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users/manage";
    }

    // Handle the delete request
    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam Integer userId, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("successMessage", "User removed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error removing user.");
        }
        return "redirect:/admin/users/manage";
    }

    @GetMapping("/users/list")
    public String showUserListPage(Model model, @RequestParam(required = false) String sortBy) {
        List<UserListDTO> users = userService.getAllUsersForDisplay();

        if ("role".equals(sortBy)) {
            users.sort(Comparator.comparing(UserListDTO::getSpecificRole));
        }

        model.addAttribute("users", users);
        return "user-list";
    }
}