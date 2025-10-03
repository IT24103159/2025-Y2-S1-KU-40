package org.example.unihelpdesk.controller;

import org.example.unihelpdesk.model.SupportStaff;
import org.example.unihelpdesk.model.User;
import org.example.unihelpdesk.repository.SupportStaffRepository;
import org.example.unihelpdesk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SupportStaffRepository supportStaffRepository;

    @GetMapping("/")
    public String showLoginPage() {
        return "index";
    }


    @PostMapping("/login")
    public String handleLogin(@RequestParam String universityId,
                              @RequestParam String password,
                              RedirectAttributes redirectAttributes, HttpSession session) {

        Optional<User> userOptional = userRepository.findByUniversityId(universityId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.getPasswordHash().equals(password)) {

                session.setAttribute("loggedInUserId", user.getUserId());
                session.setAttribute("universityId", user.getUniversityId());


                String role = user.getRole();
                if ("Admin".equals(role)) {
                    return "redirect:/admin/dashboard";
                }
                if ("Student".equals(role)) {
                    return "redirect:/student/dashboard";
                }
                if ("Lecturer".equals(role)) {
                    return "redirect:/lecturer/dashboard";
                }
                if ("Staff".equals(role)) {
                    Optional<SupportStaff> staffOptional = supportStaffRepository.findById(user.getUserId());
                    if (staffOptional.isPresent()) {
                        SupportStaff staff = staffOptional.get();
                        switch (staff.getStaffType()) {
                            case "IT_Support":
                                return "redirect:/it-support/dashboard";
                            case "Help_Desk":
                                return "redirect:/help-desk/dashboard";
                            case "Counselor":
                                return "redirect:/counselor/dashboard";
                        }
                    }
                }
            }
        }
        redirectAttributes.addFlashAttribute("error", "Invalid University ID or Password");
        return "redirect:/login";
    }

    @GetMapping("/admin/dashboard")
    public String showAdminDashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/student/dashboard")
    public String showStudentDashboard() {
        return "student-dashboard";
    }

    @GetMapping("/lecturer/dashboard")
    public String showLecturerDashboard() {
        return "lecturer-dashboard";
    }

    @GetMapping("/it-support/dashboard")
    public String showItSupportDashboard() {
        return "it-support-dashboard";
    }

    @GetMapping("/help-desk/dashboard")
    public String showHelpDeskDashboard() {
        return "help-desk-dashboard";
    }

    @GetMapping("/counselor/dashboard")
    public String showCounselorDashboard() {
        return "counselor-dashboard";
    }

    @GetMapping("/login")
    public String showSpecificLoginPage() {
        return "login";
    }
}