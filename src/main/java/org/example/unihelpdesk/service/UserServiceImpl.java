package org.example.unihelpdesk.service;

import org.example.unihelpdesk.dto.UserDTO;
import org.example.unihelpdesk.dto.UserListDTO;
import org.example.unihelpdesk.model.Lecturer;
import org.example.unihelpdesk.model.Student;
import org.example.unihelpdesk.model.SupportStaff;
import org.example.unihelpdesk.model.User;
import org.example.unihelpdesk.repository.LecturerRepository;
import org.example.unihelpdesk.repository.StudentRepository;
import org.example.unihelpdesk.repository.SupportStaffRepository;
import org.example.unihelpdesk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private LecturerRepository lecturerRepository;
    @Autowired
    private SupportStaffRepository supportStaffRepository;

    @Override
    public Optional<User> authenticate(String universityId, String password) {
        Optional<User> userOptional = userRepository.findByUniversityId(universityId);
        if (userOptional.isPresent() && userOptional.get().getPasswordHash().equals(password)) {
            return userOptional;
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void createUser(UserDTO userDTO) throws Exception {
        if (userRepository.findByUniversityId(userDTO.getUniversityId()).isPresent() ||
                userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new Exception("User with this University ID or Email already exists.");
        }

        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setUniversityId(userDTO.getUniversityId());
        user.setPasswordHash(userDTO.getPassword());

        String role = userDTO.getRole();

        if ("Student".equals(role)) {
            user.setRole("Student");
            User savedUser = userRepository.save(user);
            Student student = new Student();
            student.setUserId(savedUser.getUserId());
            studentRepository.save(student);
        } else if ("Lecturer".equals(role)) {
            user.setRole("Lecturer");
            User savedUser = userRepository.save(user);
            Lecturer lecturer = new Lecturer();
            lecturer.setUserId(savedUser.getUserId());
            lecturerRepository.save(lecturer);
        } else if ("IT_Support".equals(role) || "Help_Desk".equals(role) || "Counselor".equals(role)) {
            user.setRole("Staff");
            User savedUser = userRepository.save(user);
            SupportStaff staff = new SupportStaff();
            staff.setUserId(savedUser.getUserId());
            staff.setStaffType(role);
            supportStaffRepository.save(staff);
        } else {
            throw new Exception("Invalid role selected.");
        }
    }

    @Override
    public UserDTO findUserForManagement(String universityId) throws Exception {
        User user = userRepository.findByUniversityId(universityId)
                .orElseThrow(() -> new Exception("User not found with University ID: " + universityId));

        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUniversityId(user.getUniversityId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());

        String role = user.getRole();
        if ("Staff".equals(role)) {
            SupportStaff staff = supportStaffRepository.findById(user.getUserId()).orElse(new SupportStaff());
            dto.setRole(staff.getStaffType()); // "IT_Support", "Help_Desk", etc.
        } else {
            dto.setRole(role); // "Student", "Lecturer", etc.
        }
        return dto;
    }

    @Override
    @Transactional
    public void updateUser(UserDTO userDTO) throws Exception {
        User user = userRepository.findById(userDTO.getUserId())
                .orElseThrow(() -> new Exception("User not found for update."));

        // Update common fields
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());

        // Only update password if a new one is provided
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPasswordHash(userDTO.getPassword()); // Hash this in a real app!
        }

        String newRoleType = userDTO.getRole();
        String currentDbRole = user.getRole();
        String currentSpecificRole = findUserForManagement(user.getUniversityId()).getRole();

        // If role is not changed, just save and exit
        if (newRoleType.equals(currentSpecificRole)) {
            userRepository.save(user);
            return;
        }

        // Role has changed, so we need to delete from the old specialized table
        if ("Student".equals(currentSpecificRole)) {
            studentRepository.deleteById(user.getUserId());
        } else if ("Lecturer".equals(currentSpecificRole)) {
            lecturerRepository.deleteById(user.getUserId());
        } else { // It was a staff role
            supportStaffRepository.deleteById(user.getUserId());
        }

        // Now, add to the new specialized table
        if ("Student".equals(newRoleType)) {
            user.setRole("Student");
            Student student = new Student();
            student.setUserId(user.getUserId());
            studentRepository.save(student);
        } else if ("Lecturer".equals(newRoleType)) {
            user.setRole("Lecturer");
            Lecturer lecturer = new Lecturer();
            lecturer.setUserId(user.getUserId());
            lecturerRepository.save(lecturer);
        } else { // It's a new staff role
            user.setRole("Staff");
            SupportStaff staff = new SupportStaff();
            staff.setUserId(user.getUserId());
            staff.setStaffType(newRoleType);
            supportStaffRepository.save(staff);
        }

        userRepository.save(user);
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
        // ON DELETE CASCADE in the database will handle deleting from specialized tables.
    }

    @Override
    public List<UserListDTO> getAllUsersForDisplay() {
        List<User> users = userRepository.findAll();
        List<UserListDTO> userList = new ArrayList<>();

        for (User user : users) {
            String specificRole = user.getRole();
            if ("Staff".equals(specificRole)) {

                SupportStaff staff = supportStaffRepository.findById(user.getUserId()).orElse(null);
                if (staff != null) {
                    specificRole = staff.getStaffType(); // "IT_Support", "Help_Desk", etc.
                }
            }
            userList.add(new UserListDTO(
                    user.getUniversityId(),
                    user.getName(),
                    user.getEmail(),
                    specificRole
            ));
        }
        return userList;
    }
}