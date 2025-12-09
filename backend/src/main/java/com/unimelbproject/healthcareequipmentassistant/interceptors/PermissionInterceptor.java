package com.unimelbproject.healthcareequipmentassistant.interceptors;

import com.unimelbproject.healthcareequipmentassistant.models.Response;
import com.unimelbproject.healthcareequipmentassistant.models.User;
import com.unimelbproject.healthcareequipmentassistant.security.Permission;
import com.unimelbproject.healthcareequipmentassistant.annotations.RequirePermission;
import com.unimelbproject.healthcareequipmentassistant.security.RolePermissions;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class PermissionInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(PermissionInterceptor.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequirePermission requirePermission = handlerMethod.getMethodAnnotation(RequirePermission.class);

        if (requirePermission == null) {
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //Is authentication missing or anonymous
        if (authentication == null) {
            log.warn("PermissionInterceptor: No authentication found for {}", request.getRequestURI());
            return unauthorized(response, "Authentication required");
        }

        if (!authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            log.warn("PermissionInterceptor: Unauthenticated or anonymous user for {}", request.getRequestURI());
            return unauthorized(response, "Authentication required");
        }
        //Validate principal type
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            log.error("PermissionInterceptor: Principal is not a User. Actual type: {}", principal.getClass().getName());
            return unauthorized(response, "Invalid authentication");
        }
        User user = (User) principal;

        if (user.getRole() == null) {
            log.error("PermissionInterceptor: User {} has null role", user.getUsername());
            return forbidden(response, "User role not assigned");
        }
        //Ensure requiredPermission isn’t null
        Permission requiredPermission = requirePermission.value();
        if (requiredPermission == null) {
            log.error("PermissionInterceptor: Required permission is null for {}", request.getRequestURI());
            return forbidden(response, "Permission misconfiguration");
        }

        // permission check
        if (!RolePermissions.hasPermission(user.getRole(), requiredPermission)) {
            log.warn("PermissionInterceptor: Access denied. User={}, Role={}, Required={}",
                    user.getUsername(), user.getRole(), requiredPermission);
            return forbidden(response, "Access denied");
        }

        log.debug("PermissionInterceptor: Access granted. User={}, Role={}, Permission={}",
                user.getUsername(), user.getRole(), requiredPermission);
        return true;
    }

    private boolean unauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(Response.failure(message)));
        return false;
    }

    private boolean forbidden(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(Response.failure(message)));
        return false;
    }
}
