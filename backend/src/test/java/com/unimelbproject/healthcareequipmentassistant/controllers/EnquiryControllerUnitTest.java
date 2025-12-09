package com.unimelbproject.healthcareequipmentassistant.controllers;

import com.unimelbproject.healthcareequipmentassistant.dto.EnquiryCreateRequest;
import com.unimelbproject.healthcareequipmentassistant.dto.EnquiryReplyRequest;
import com.unimelbproject.healthcareequipmentassistant.interfaces.IResponse;
import com.unimelbproject.healthcareequipmentassistant.models.Enquiry;
import com.unimelbproject.healthcareequipmentassistant.models.User;
import com.unimelbproject.healthcareequipmentassistant.services.EmailService;
import com.unimelbproject.healthcareequipmentassistant.services.EnquiryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnquiryControllerUnitTest {

    @Mock
    private EnquiryService enquiryService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EnquiryController controller;

    private User adminUser;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId("123e4567-e89b-12d3-a456-426614174000");
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setRole(User.UserRole.admin);
    }

    private Enquiry sampleEnquiry(String question) {
        Enquiry e = new Enquiry();
        e.setAskerId(adminUser.getId());
        e.setQuestion(question);
        e.setStatus("pending");
        e.setTimestamp(LocalDateTime.now());
        return e;
    }

    @Test
    @DisplayName("GET /api/enquiries -> returns paged enquiries")
    void listAll_returnsPaged() {
        Page<Enquiry> page = new PageImpl<>(List.of(sampleEnquiry("Q1")), PageRequest.of(0, 10), 1);
        when(enquiryService.listAll(0, 10)).thenReturn(page);

        ResponseEntity<Page<Enquiry>> resp = controller.listAll(0, 10);

        assertEquals(200, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        assertEquals(1, resp.getBody().getContent().size());
        assertEquals("Q1", resp.getBody().getContent().get(0).getQuestion());
    }

    @Test
    @DisplayName("GET /api/enquiries/my unauthenticated -> failure")
    void listMine_unauthenticated() {
        IResponse<Page<Enquiry>> resp = controller.listMine(null, 0, 10);
        assertFalse(resp.isSuccess());
        assertEquals("User not authenticated.", resp.getMessage());
        assertNull(resp.getData());
    }

    @Test
    @DisplayName("GET /api/enquiries/my authenticated -> success")
    void listMine_authenticated() {
        Page<Enquiry> page = new PageImpl<>(List.of(sampleEnquiry("My Q")), PageRequest.of(0, 10), 1);
        when(enquiryService.listMine(adminUser.getId(), 0, 10)).thenReturn(page);

        IResponse<Page<Enquiry>> resp = controller.listMine(adminUser, 0, 10);
        assertTrue(resp.isSuccess());
        assertNotNull(resp.getData());
        assertEquals(1, resp.getData().getContent().size());
        assertEquals("My Q", resp.getData().getContent().get(0).getQuestion());
    }

    @Test
    @DisplayName("GET /api/enquiries/{id} found -> success")
    void getById_found() {
        Enquiry e = sampleEnquiry("Q?");
        when(enquiryService.getEnquiryById(1L)).thenReturn(Optional.of(e));

        IResponse<Enquiry> resp = controller.getEnquiryById(1L);
        assertTrue(resp.isSuccess());
        assertEquals("Q?", resp.getData().getQuestion());
    }

    @Test
    @DisplayName("GET /api/enquiries/{id} not found -> failure")
    void getById_notFound() {
        when(enquiryService.getEnquiryById(404L)).thenReturn(Optional.empty());

        IResponse<Enquiry> resp = controller.getEnquiryById(404L);
        assertFalse(resp.isSuccess());
        assertEquals("Enquiry not found", resp.getMessage());
        assertNull(resp.getData());
    }

    @Test
    @DisplayName("POST /api/enquiries/send unauthenticated -> failure")
    void create_unauthenticated() {
        EnquiryCreateRequest req = new EnquiryCreateRequest();
        req.setQuestion("Hello?");

        IResponse<Enquiry> resp = controller.create(null, req);
        assertFalse(resp.isSuccess());
        assertEquals("User not authenticated.", resp.getMessage());
    }

    @Test
    @DisplayName("POST /api/enquiries/send empty question -> failure")
    void create_emptyQuestion() {
        EnquiryCreateRequest req = new EnquiryCreateRequest();
        req.setQuestion(" ");

        IResponse<Enquiry> resp = controller.create(adminUser, req);
        assertFalse(resp.isSuccess());
        assertEquals("Question must not be empty.", resp.getMessage());
    }

    @Test
    @DisplayName("POST /api/enquiries/send success -> sends email and returns saved enquiry")
    void create_success() {
        EnquiryCreateRequest req = new EnquiryCreateRequest();
        req.setQuestion("New wheelchair?");

        Enquiry saved = sampleEnquiry("New wheelchair?");
        when(enquiryService.create(eq(adminUser.getId()), any(EnquiryCreateRequest.class))).thenReturn(saved);
        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);

        IResponse<Enquiry> resp = controller.create(adminUser, req);
        assertTrue(resp.isSuccess());
        assertEquals("New wheelchair?", resp.getData().getQuestion());
        assertEquals("pending", resp.getData().getStatus());

        verify(emailService).sendEmail(eq(adminUser.getEmail()), eq("[NOV] Enquiry sent successfully!"), Mockito.contains("New wheelchair?"));
        verify(enquiryService).create(eq(adminUser.getId()), any(EnquiryCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/enquiries/{id}/reply unauthenticated -> failure")
    void reply_unauthenticated() {
        EnquiryReplyRequest req = new EnquiryReplyRequest();
        req.setAnswer("Thanks!");

        IResponse<Enquiry> resp = controller.reply(1L, null, req);
        assertFalse(resp.isSuccess());
        assertEquals("User not authenticated.", resp.getMessage());
    }

    @Test
    @DisplayName("POST /api/enquiries/{id}/reply empty answer -> failure")
    void reply_emptyAnswer() {
        EnquiryReplyRequest req = new EnquiryReplyRequest();
        req.setAnswer(" ");

        IResponse<Enquiry> resp = controller.reply(1L, adminUser, req);
        assertFalse(resp.isSuccess());
        assertEquals("Answer must not be empty.", resp.getMessage());
    }

    @Test
    @DisplayName("POST /api/enquiries/{id}/reply success -> sends email and returns updated enquiry")
    void reply_success() {
        EnquiryReplyRequest req = new EnquiryReplyRequest();
        req.setAnswer("Here is the answer.");

        Enquiry updated = sampleEnquiry("Q?");
        updated.setResponderId(adminUser.getId());
        updated.setAnswer("Here is the answer.");
        updated.setStatus("answered");

        when(enquiryService.reply(eq(1L), eq(adminUser.getId()), any(EnquiryReplyRequest.class))).thenReturn(Optional.of(updated));
        when(emailService.sendEmail(anyString(), anyString(), anyString())).thenReturn(true);

        IResponse<Enquiry> resp = controller.reply(1L, adminUser, req);
        assertTrue(resp.isSuccess());
        assertEquals("Here is the answer.", resp.getData().getAnswer());
        assertEquals("answered", resp.getData().getStatus());

        verify(emailService).sendEmail(eq(adminUser.getEmail()), eq("[NOV] The reply to your enquiries."), eq("Here is the answer."));
        verify(enquiryService).reply(eq(1L), eq(adminUser.getId()), any(EnquiryReplyRequest.class));
    }
} 