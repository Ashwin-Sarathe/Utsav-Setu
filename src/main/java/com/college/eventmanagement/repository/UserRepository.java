package com.college.eventmanagement.repository;

import com.college.eventmanagement.model.Role;
import com.college.eventmanagement.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    boolean existsByRole(Role role);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Long countByRole(Role role);
}
