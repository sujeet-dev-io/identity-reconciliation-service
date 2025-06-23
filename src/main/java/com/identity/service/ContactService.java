package com.identity.service;

import com.identity.dto.IdentifyRequest;
import com.identity.dto.IdentifyResponse;

public interface ContactService {
    IdentifyResponse identifyContact(IdentifyRequest request);
}