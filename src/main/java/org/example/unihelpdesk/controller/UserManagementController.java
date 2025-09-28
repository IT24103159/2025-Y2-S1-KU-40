package org.example.unihelpdesk.controller;

import org.example.unihelpdesk.dto.UserDTO;
import org.example.unihelpdesk.model.Faculty;
import org.example.unihelpdesk.repository.FacultyRepository;
import org.example.unihelpdesk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import org.example.unihelpdesk.dto.UserListDTO;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class UserManagementController {

    @Autowired private UserService userService;
    @Autowired private FacultyRepository facultyRepository;

    @GetMapping("/users")
    public String showUserManagementPage() {
        return "user-management";
    }

    @GetMapping("/users/new")
    public String showAddUserForm(Model model) {
        List<Faculty> faculties = facultyRepository.findAll();
        model.addAttribute("user", new UserDTO());
        model.addAttribute("faculties", faculties);
        return "add-new-user";
    }

    // Handle the search request
    @GetMapping("/users/search")
    public String searchUser(@RequestParam String universityId, Model model, RedirectAttributes redirectAttributes) {
        try {
            UserDTO userDTO = userService.findUserForManagement(universityId);
            List<Faculty> faculties = facultyRepository.findAll();
            model.addAttribute("user", userDTO);
            model.addAttribute("faculties", faculties);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/users/manage";
        }
        return "search-update-remove-user";
    }

    // Show the initial search page
    @GetMapping("/users/manage")
    public String showManageUserPage() {
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
    public String showUserListPage(Model model) {
        Map<String, List<UserListDTO>> groupedUsers = userService.getAllUsersGroupedByRole();
        model.addAttribute("studentList", groupedUsers.get("students"));
        model.addAttribute("lecturerList", groupedUsers.get("lecturers"));
        model.addAttribute("staffList", groupedUsers.get("staffMembers"));
        return "user-list";
    }
}