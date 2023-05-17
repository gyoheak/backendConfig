package metaverse.backend.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestAdminController {
    @Secured("ROLE_ADMIN")
    @GetMapping("/test/admin")
    public String testAdmin() {
        return "Admin Test";
    }
}
