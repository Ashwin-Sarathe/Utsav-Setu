package com.college.eventmanagement.service;

import com.college.eventmanagement.dto.ChangePasswordDTO;
import com.college.eventmanagement.dto.RegisterRequestDTO;
import com.college.eventmanagement.dto.UserProfileDTO;
import com.college.eventmanagement.dto.UserResponseDTO;
import com.college.eventmanagement.exception.ConflictException;
import com.college.eventmanagement.exception.ResourceNotFoundException;
import com.college.eventmanagement.model.Role;
import com.college.eventmanagement.model.User;
import com.college.eventmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public void saveNewUser(RegisterRequestDTO request) {

        //validation
        //created GlobalException class to handle runtime exception
        String username = request.getUsername();
        if(userRepository.existsByUsername(username)){
            throw new ConflictException("Username Already Exists!!");
        }
        String email = request.getEmail();
        if(userRepository.existsByEmail(email)){
            throw new ConflictException("Email Already Exists!!");
        }

        //convert request dto to user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setBranch(request.getBranch());
        user.setSem(request.getSem());
        user.setYear(request.getYear());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());


        userRepository.save(user);

    }

    public UserResponseDTO promoteToAdmin(String userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if(user.getRole()==Role.ADMIN){
            throw new ConflictException("User is already admin");
        }
        user.setRole(Role.ADMIN);
        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    private UserResponseDTO mapToResponse(User user){
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(user.getId());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setRole(user.getRole());
        userResponseDTO.setUsername(user.getUsername());
        userResponseDTO.setName(user.getName());
        userResponseDTO.setBranch(user.getBranch());
        userResponseDTO.setSem(user.getSem());
        return userResponseDTO;
    }

    public Page<UserResponseDTO> getAllUsers(int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(this::mapToResponse);
    }

    public UserResponseDTO demoteToUser(String userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if(user.getRole()==Role.USER)
            throw new ConflictException("Role is already user");
        long adminCount = userRepository.countByRole(Role.ADMIN);
        if(adminCount<=1)
            throw new ConflictException("Cannot demote the last admin");

        user.setRole(Role.USER);
        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    public void updateUserProfile(String username, UserProfileDTO userProfileDTO){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (userProfileDTO.getEmail() != null) user.setEmail(userProfileDTO.getEmail());
        if (userProfileDTO.getName() != null) user.setName(userProfileDTO.getName());
        if (userProfileDTO.getBranch() != null) user.setBranch(userProfileDTO.getBranch());
        if (userProfileDTO.getYear() != null) user.setYear(userProfileDTO.getYear());
        if (userProfileDTO.getSem() != null) user.setSem(userProfileDTO.getSem());

        userRepository.save(user);
    }

    public void changePassword(String username, ChangePasswordDTO changePasswordDTO){

        User user  = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())){
            throw new RuntimeException("Incorrect current password");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);
    }
}
