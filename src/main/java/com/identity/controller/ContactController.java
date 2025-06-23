package com.identity.controller;

import com.identity.dto.IdentifyRequest;
import com.identity.dto.IdentifyResponse;
import com.identity.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/identify")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<IdentifyResponse> identify(@RequestBody IdentifyRequest request) {
        IdentifyResponse response = contactService.identifyContact(request);
        return ResponseEntity.ok(response);
    }
}