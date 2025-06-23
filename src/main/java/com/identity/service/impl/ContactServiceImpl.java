package com.identity.service.impl;

import com.identity.dto.IdentifyRequest;
import com.identity.dto.IdentifyResponse;
import com.identity.entity.Contact;
import com.identity.repository.ContactRepository;
import com.identity.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.identity.enums.LinkPrecedence;


import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    @Override
    public IdentifyResponse identifyContact(IdentifyRequest request) {
        String email = request.getEmail();
        String phone = request.getPhoneNumber();

        // 1. Fetch all contacts matching email or phone
        List<Contact> matchedContacts = contactRepository.findByEmailOrPhoneNumber(email, phone);

        // 2. If no matches, create new primary contact
        if (matchedContacts.isEmpty()) {
            Contact newContact = contactRepository.save(Contact.builder()
                    .email(email)
                    .phoneNumber(phone)
                    .linkPrecedence(LinkPrecedence.PRIMARY)
                    .build());

            return buildResponse(newContact, List.of(), List.of());
        }

        // 3. Find the oldest primary contact
        Contact primaryContact = matchedContacts.stream()
                .filter(c -> c.getLinkPrecedence() == LinkPrecedence.PRIMARY)
                .min(Comparator.comparing(Contact::getCreatedAt))
                .orElseGet(() -> matchedContacts.get(0));

        Integer primaryId = primaryContact.getId();

        // 4. Ensure all other contacts are linked to the primary
        for (Contact contact : matchedContacts) {
            if (!contact.getId().equals(primaryId) && contact.getLinkedId() == null) {
                contact.setLinkedId(primaryId);
                contact.setLinkPrecedence(LinkPrecedence.SECONDARY);
                contactRepository.save(contact);
            }
        }

        // 5. Check if new info needs to be stored as secondary
        boolean alreadyPresent = matchedContacts.stream()
                .anyMatch(c -> Objects.equals(c.getEmail(), email) || Objects.equals(c.getPhoneNumber(), phone));

        if (!alreadyPresent) {
            Contact newSecondary = contactRepository.save(Contact.builder()
                    .email(email)
                    .phoneNumber(phone)
                    .linkPrecedence(LinkPrecedence.SECONDARY)
                    .linkedId(primaryId)
                    .build());
            matchedContacts.add(newSecondary);
        }

        // 6. Gather all related contacts including secondaries
        Set<Contact> allRelated = matchedContacts.stream()
                .flatMap(c -> contactRepository.findByEmailOrPhoneNumber(c.getEmail(), c.getPhoneNumber()).stream())
                .collect(Collectors.toSet());

        Set<String> emails = allRelated.stream().map(Contact::getEmail).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<String> phones = allRelated.stream().map(Contact::getPhoneNumber).filter(Objects::nonNull).collect(Collectors.toSet());
        List<Integer> secondaryIds = allRelated.stream()
                .filter(c -> c.getLinkPrecedence() == LinkPrecedence.SECONDARY)
                .map(Contact::getId)
                .toList();

        return IdentifyResponse.builder()
                .primaryContactId(primaryId)
                .emails(new ArrayList<>(emails))
                .phoneNumbers(new ArrayList<>(phones))
                .secondaryContactIds(secondaryIds)
                .build();
    }

    private IdentifyResponse buildResponse(Contact primary, List<String> emails, List<String> phones) {
        return IdentifyResponse.builder()
                .primaryContactId(primary.getId())
                .emails(emails.isEmpty() ? List.of(primary.getEmail()) : emails)
                .phoneNumbers(phones.isEmpty() ? List.of(primary.getPhoneNumber()) : phones)
                .secondaryContactIds(List.of())
                .build();
    }
}
