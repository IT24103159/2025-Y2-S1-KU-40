package org.example.unihelpdesk.service;

import org.example.unihelpdesk.dto.UserDTO;
import org.example.unihelpdesk.dto.UserListDTO;
import org.example.unihelpdesk.model.*;
import org.example.unihelpdesk.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
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
    @Autowired
    private FacultyRepository facultyRepository;

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
            Faculty faculty = facultyRepository.findById(userDTO.getFacultyId())
                    .orElseThrow(() -> new Exception("Invalid Faculty ID"));
            Student student = new Student();
            student.setUserId(savedUser.getUserId());
            student.setFaculty(faculty);
            studentRepository.save(student);
        } else if ("Lecturer".equals(role)) {
            user.setRole("Lecturer");
            User savedUser = userRepository.save(user);
            Faculty faculty = facultyRepository.findById(userDTO.getFacultyId())
                    .orElseThrow(() -> new Exception("Invalid Faculty ID"));
            Lecturer lecturer = new Lecturer();
            lecturer.setUserId(savedUser.getUserId());
            lecturer.setFaculty(faculty);
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
            dto.setRole(staff.getStaffType());
        } else {
            dto.setRole(role);
            if ("Student".equals(role)) {
                Student student = studentRepository.findById(user.getUserId()).orElse(null);
                if (student != null && student.getFaculty() != null) {
                    dto.setFacultyId(student.getFaculty().getFacultyId());
                    dto.setFacultyName(student.getFaculty().getFacultyName());
                }
            } else if ("Lecturer".equals(role)) {
                Lecturer lecturer = lecturerRepository.findById(user.getUserId()).orElse(null);
                if (lecturer != null && lecturer.getFaculty() != null) {
                    dto.setFacultyId(lecturer.getFaculty().getFacultyId());
                    dto.setFacultyName(lecturer.getFaculty().getFacultyName());
                }
            }
        }
        return dto;
    }
    @Override
    @Transactional
    public void updateUser(UserDTO userDTO) throws Exception {
        User user = userRepository.findById(userDTO.getUserId())
                .orElseThrow(() -> new Exception("User not found for update."));

        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPasswordHash(userDTO.getPassword());
        }

        String newRoleType = userDTO.getRole();
        String currentSpecificRole = findUserForManagement(user.getUniversityId()).getRole();


        if (newRoleType.equals(currentSpecificRole)) {
            if("Student".equals(newRoleType)) {
                Student student = studentRepository.findById(user.getUserId()).orElseThrow(() -> new Exception("Student details not found."));
                Faculty faculty = facultyRepository.findById(userDTO.getFacultyId()).orElseThrow(() -> new Exception("Selected faculty not found."));
                student.setFaculty(faculty);
                studentRepository.save(student);
            } else if ("Lecturer".equals(newRoleType)) {
                Lecturer lecturer = lecturerRepository.findById(user.getUserId()).orElseThrow(() -> new Exception("Lecturer details not found."));
                Faculty faculty = facultyRepository.findById(userDTO.getFacultyId()).orElseThrow(() -> new Exception("Selected faculty not found."));
                lecturer.setFaculty(faculty);
                lecturerRepository.save(lecturer);
            }
            userRepository.save(user);
            return;
        }


        if ("Student".equals(currentSpecificRole)) studentRepository.deleteById(user.getUserId());
        else if ("Lecturer".equals(currentSpecificRole)) lecturerRepository.deleteById(user.getUserId());
        else supportStaffRepository.deleteById(user.getUserId());

        if ("Student".equals(newRoleType)) {
            user.setRole("Student");
            Faculty faculty = facultyRepository.findById(userDTO.getFacultyId()).orElseThrow(() -> new Exception("Selected faculty not found."));
            Student student = new Student();
            student.setUserId(user.getUserId());
            student.setFaculty(faculty);
            studentRepository.save(student);
        } else if ("Lecturer".equals(newRoleType)) {
            user.setRole("Lecturer");
            Faculty faculty = facultyRepository.findById(userDTO.getFacultyId()).orElseThrow(() -> new Exception("Selected faculty not found."));
            Lecturer lecturer = new Lecturer();
            lecturer.setUserId(user.getUserId());
            lecturer.setFaculty(faculty);
            lecturerRepository.save(lecturer);
        } else { // It's a staff role
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
                    specificRole = staff.getStaffType();
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
    @Override
    public Map<String, List<UserListDTO>> getAllUsersGroupedByRole() {
        // created_at එක අනුව users ලාව sort කරලා list එකක් ගන්නවා
        List<User> sortedUsers = userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getCreatedAt))
                .collect(Collectors.toList());

        List<UserListDTO> students = new ArrayList<>();
        List<UserListDTO> lecturers = new ArrayList<>();
        List<UserListDTO> staffMembers = new ArrayList<>();

        for (User user : sortedUsers) {
            String role = user.getRole();
            if ("Student".equals(role)) {
                Student student = studentRepository.findById(user.getUserId()).orElse(null);
                String facultyName = (student != null && student.getFaculty() != null) ? student.getFaculty().getFacultyName() : "N/A";
                students.add(new UserListDTO(user.getUniversityId(), user.getName(), user.getEmail(), "Student", facultyName));
            } else if ("Lecturer".equals(role)) {
                Lecturer lecturer = lecturerRepository.findById(user.getUserId()).orElse(null);
                String facultyName = (lecturer != null && lecturer.getFaculty() != null) ? lecturer.getFaculty().getFacultyName() : "N/A";
                lecturers.add(new UserListDTO(user.getUniversityId(), user.getName(), user.getEmail(), "Lecturer", facultyName));
            } else if ("Staff".equals(role)) {
                SupportStaff staff = supportStaffRepository.findById(user.getUserId()).orElse(null);
                String staffType = (staff != null) ? staff.getStaffType() : "Unknown Staff";
                staffMembers.add(new UserListDTO(user.getUniversityId(), user.getName(), user.getEmail(), staffType, null));
            }
        }

        Map<String, List<UserListDTO>> groupedUsers = new HashMap<>();
        groupedUsers.put("students", students);
        groupedUsers.put("lecturers", lecturers);
        groupedUsers.put("staffMembers", staffMembers);

        return groupedUsers;
    }

    @Override
    public User findUserById(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public List<User> findStaffByType(String staffType) {
        return supportStaffRepository.findByStaffType(staffType).stream()
                .map(staff -> userRepository.findById(staff.getUserId()).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }
}