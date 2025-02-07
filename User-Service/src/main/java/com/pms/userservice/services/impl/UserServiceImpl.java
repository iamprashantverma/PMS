package com.pms.userservice.services.impl;

import com.pms.userservice.dto.ResponseDTO;
import com.pms.userservice.dto.UserDTO;
import com.pms.userservice.entities.User;
import com.pms.userservice.entities.enums.Roles;
import com.pms.userservice.entities.enums.Status;
import com.pms.userservice.exceptions.ResourceNotFound;
import com.pms.userservice.repositories.UserRepository;
import com.pms.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    private UserDTO convertToUserDTO(User user) {
        return modelMapper.map(user,UserDTO.class);
    }

    private User convertToUserEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO,User.class);
    }

    /* get user by userId*/
    @Override
    public User getUserById(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if ( userOptional.isEmpty() )
            throw new ResourceNotFound(" Invalid User id!");
        return userOptional.get();
    }

    @Override
    public User findUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if ( userOptional.isEmpty() )
            throw new ResourceNotFound("email not registered , please signup !");
        return userOptional.get();
    }

    @Override
    public List<Roles> getAllRoles() {
        return List.of(Roles.values());
    }

    @Override
    public ResponseDTO assignRoleToUser(String userId, Roles role) {
        /* get the user */
        User user = getUserById(userId);
        /* set the new Role */
        user.setRole(role);
        /* set the user into the db */
        userRepository.save(user);
        return ResponseDTO.builder().message("Role assigning successfully").build();
    }

    @Override
    public UserDTO getUserDetails(String userId) {
        User user = getUserById(userId);
        return  convertToUserDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> userList = userRepository.findAll() ;
        return userList.stream()
                .map(this::convertToUserDTO)
                .toList();
    }

    @Override
    public ResponseDTO updateUserDetails(UserDTO userDTO) {

       return ResponseDTO.builder().message("Details successfully updated").build();
    }

    @Override
    public ResponseDTO deactivateUser(String userId) {
        User user = getUserById(userId);
        user.setStatus(Status.INACTIVE);
        /* saved the user into the db */
        userRepository.save(user);
        return ResponseDTO.builder().message("Deactivated").build();

    }


}
