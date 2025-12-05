package com.group2.backend.service;

import com.group2.backend.security.response.StaffResponse;
import org.springframework.data.domain.Page;


import java.util.List;


public interface UserService {
    void deleteStaff(Long id);

    StaffResponse getStaff(Long id);

    Page<StaffResponse> getAllStaff(int page, int size, String sort, String direction);
}
