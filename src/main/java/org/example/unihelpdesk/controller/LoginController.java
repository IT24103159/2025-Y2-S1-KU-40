package org.example.unihelpdesk.controller;

import org.example.unihelpdesk.model.User;
import org.example.unihelpdesk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String showLoginPage() {
        return "index";
    }


    @PostMapping("/login")
    public String handleLogin(@RequestParam String universityId,
                              @RequestParam String password,
                              RedirectAttributes redirectAttributes) {

        Optional<User> userOptional = userRepository.findByUniversityId(universityId); 

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.getPasswordHash().equals(password)) {
                if ("Admin".equals(user.getRole())) {
                    return "redirect:/admin/dashboard";
                }
                return "redirect:/?error=true";
            }
        }
        redirectAttributes.addAttribute("error", "true");
        return "redirect:/";
    }

    @GetMapping("/admin/dashboard")
    public String showAdminDashboard() {
        return "admin-dashboard";
    }



    @GetMapping("/login")
    public String showSpecificLoginPage() {
        return "login";
    }
}