// src/main/java/com/group2/backend/security/response/StaffResponse.java
package com.group2.backend.security.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String mobileNumber;
}