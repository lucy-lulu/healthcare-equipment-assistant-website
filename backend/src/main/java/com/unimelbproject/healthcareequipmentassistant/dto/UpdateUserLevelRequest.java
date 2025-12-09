package com.unimelbproject.healthcareequipmentassistant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Request to update a user's level by email")
public class UpdateUserLevelRequest {

    @Schema(description = "User email", example = "partner@example.com", required = true)
    @NotBlank @Email
    private String email;

    @Schema(description = "New level. Partners: 1–4; Staff/Sales/OT/Admin: must be 0",
            example = "2", required = true)
    @NotNull
    private Integer level;

    public UpdateUserLevelRequest() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
}
