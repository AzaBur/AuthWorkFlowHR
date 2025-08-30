package tranquillity.authworkflowhr.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Public endpoint - доступно всем";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint() {
        return "Admin endpoint - только для ADMIN";
    }

    @GetMapping("/hr")
    @PreAuthorize("hasRole('HR_MANAGER')")
    public String hrEndpoint() {
        return "HR endpoint - только для HR_MANAGER";
    }
}
