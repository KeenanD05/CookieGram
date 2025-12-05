package com.group2.backend.service;

import com.group2.backend.exception.ResourceNotFoundException;
import com.group2.backend.model.User;
import com.group2.backend.repository.UserRepository;
import com.group2.backend.security.response.StaffResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public void deleteStaff(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    @Override
    public StaffResponse getStaff(Long id) {
        return userRepository.findById(id)
                .map(user -> new StaffResponse(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getMobileNumber(),
                        user.getUserName()

                ))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public Page<StaffResponse> getAllStaff(int page, int size, String sort, String direction) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(direction), sort)
        );
        return userRepository.findStaff(pageable)
                .map(user -> new StaffResponse(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getMobileNumber(),
                        user.getUserName()
                ));
    }
}
