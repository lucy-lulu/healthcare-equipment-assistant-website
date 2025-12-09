package com.unimelbproject.healthcareequipmentassistant.controllers;

import com.unimelbproject.healthcareequipmentassistant.dto.EnquiryCreateRequest;
import com.unimelbproject.healthcareequipmentassistant.dto.EnquiryReplyRequest;
import com.unimelbproject.healthcareequipmentassistant.interfaces.IResponse;
import com.unimelbproject.healthcareequipmentassistant.interfaces.Response;
import com.unimelbproject.healthcareequipmentassistant.models.Enquiry;
import com.unimelbproject.healthcareequipmentassistant.models.User;
import com.unimelbproject.healthcareequipmentassistant.services.EmailService;
import com.unimelbproject.healthcareequipmentassistant.services.EnquiryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/enquiries")
@Tag(name = "Enquiry API", description = "Create, list, and reply to enquiries.")
public class EnquiryController {

    private final EnquiryService service;
    private final EmailService emailService;

    public EnquiryController(EnquiryService service, EmailService emailService) {
        this.service = service;
        this.emailService = emailService;
    }

    // GET /api/enquiries?cursor=0&size=10
    @Operation(summary = "List all enquiries (paginated)")
    @GetMapping
    public ResponseEntity<Page<Enquiry>> listAll(@RequestParam(defaultValue = "0") int cursor,
                                                 @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.listAll(cursor, size));
    }

    // GET /api/enquiries/my?cursor=0&size=10
    @Operation(summary = "List my enquiries (paginated)")
    @GetMapping("/my")
    public IResponse<Page<Enquiry>> listMine(@AuthenticationPrincipal User user,
                                             @RequestParam(defaultValue = "0") int cursor,
                                             @RequestParam(defaultValue = "10") int size) {
        if (user == null || user.getId() == null) {
            return Response.failure("User not authenticated.");
        }
        return Response.success(service.listMine(user.getId(), cursor, size));
    }

    // GET /api/enquiries/{id}
    @Operation(
            summary = "Get enquiry details by ID",
            description = "Returns detailed information for a specific enquiry by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved enquiry details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IResponse.class),
                            examples = @ExampleObject(
                                    name = "Successful enquiry retrieval",
                                    value = """
                                    {
                                      "success": true,
                                      "message": "Success",
                                      "data": {
                                        "id": 1,
                                        "askerId": "123e4567-e89b-12d3-a456-426614174000",
                                        "responderId": "456e7890-e89b-12d3-a456-426614174001",
                                        "question": "What is the warranty period for wheelchairs?",
                                        "answer": "Our wheelchairs come with a 2-year warranty covering manufacturing defects.",
                                        "status": "answered",
                                        "timestamp": "2024-01-15T10:30:00"
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Enquiry not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IResponse.class),
                            examples = @ExampleObject(
                                    name = "Enquiry not found",
                                    value = """
                                    {
                                      "success": false,
                                      "message": "Enquiry not found",
                                      "data": null
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public IResponse<Enquiry> getEnquiryById(
            @Parameter(description = "Enquiry ID", required = true)
            @PathVariable Long id) {
        
        Optional<Enquiry> enquiry = service.getEnquiryById(id);
        if (enquiry.isEmpty()) {
            return Response.failure("Enquiry not found");
        }
        
        return Response.success(enquiry.get());
    }

    // POST /api/enquiries/send
    @Operation(summary = "Create a new enquiry")
    @PostMapping("/send")
    public IResponse<Enquiry> create(@AuthenticationPrincipal User user,
                                     @RequestBody EnquiryCreateRequest req) {
        if (user == null || user.getId() == null) {
            return Response.failure("User not authenticated.");
        }
        if (req.getQuestion() == null || req.getQuestion().isBlank()) {
            return Response.failure("Question must not be empty.");
        }

        //notification mail when enquiries sent
        emailService.sendEmail(user.getEmail(), "[NOV] Enquiry sent successfully!", "Your enquiry ("+ req.getQuestion() + ") has been successfully sent.");

        return Response.success(service.create(user.getId(), req));
    }

    // POST /api/enquiries/{id}/reply
    @Operation(summary = "Reply to an existing enquiry")
    @PostMapping("/{id}/reply")
    public IResponse<Enquiry> reply(@PathVariable Long id,
                                    @AuthenticationPrincipal User user,
                                    @RequestBody EnquiryReplyRequest req) {
        if (user == null || user.getId() == null) {
            return Response.failure("User not authenticated.");
        }
        if (req.getAnswer() == null || req.getAnswer().isBlank()) {
            return Response.failure("Answer must not be empty.");
        }
        Optional<Enquiry> updated = service.reply(id, user.getId(), req);

        //notification mail when reply to an enquiry
        emailService.sendEmail(user.getEmail(), "[NOV] The reply to your enquiries.", req.getAnswer());

        return updated.<IResponse<Enquiry>>map(Response::success)
                .orElseGet(() -> Response.failure("Enquiry not found."));
    }
}
