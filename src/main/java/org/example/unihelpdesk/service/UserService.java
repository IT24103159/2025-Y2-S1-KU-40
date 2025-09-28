package org.example.unihelpdesk.service;

import org.example.unihelpdesk.dto.UserDTO;
import org.example.unihelpdesk.dto.UserListDTO;
import org.example.unihelpdesk.model.User;
import java.util.Optional;
import java.util.List;
import java.util.Map;


public interface UserService {
    Optional<User> authenticate(String universityId, String password);
    void createUser(UserDTO userDTO) throws Exception;

    UserDTO findUserForManagement(String universityId) throws Exception;
    void updateUser(UserDTO userDTO) throws Exception;
    void deleteUser(Integer userId);
    List<UserListDTO> getAllUsersForDisplay();
    Map<String, List<UserListDTO>> getAllUsersGroupedByRole();
}