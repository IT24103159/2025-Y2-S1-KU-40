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
                              RedirectAttributes redirectAttributes) {

        Optional<User> userOptional = userRepository.findByUniversityId(universityId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.getPasswordHash().equals(password)) {

                String role = user.getRole();

                if ("Admin".equals(user.getRole())) {
                    return "redirect:/admin/dashboard";
                }
                if ("Student".equals(user.getRole())) {
                    return "redirect:/student/dashboard";
                }
                if ("Lecturer".equals(user.getRole())) {
                    return "redirect:/lecturer/dashboard";
                }
                if ("Staff".equals(role)) {
                    Optional<SupportStaff> staffOptional = supportStaffRepository.findById(user.getUserId());
                    if (staffOptional.isPresent()) {
                        SupportStaff staff = staffOptional.get();
                        if ("IT_Support".equals(staff.getStaffType())) {
                            return "redirect:/it-support/dashboard";
                        } else if ("Help_Desk".equals(staff.getStaffType())) {
                            return "redirect:/help-desk/dashboard";
                        }else if ("Counselor".equals(staff.getStaffType())) {
                            return "redirect:/counselor/dashboard";
                        }
                    }
                }
            }
        }
        redirectAttributes.addAttribute("error", "true");
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