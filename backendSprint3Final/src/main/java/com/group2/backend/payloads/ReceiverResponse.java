package com.group2.backend.payloads;

import com.group2.backend.model.Receiver;
import lombok.Data;

@Data
public class ReceiverResponse {
    private Long id;
    private String name;
    private String phoneNumber;
    private String email;
    private String specialInstructions;



}
