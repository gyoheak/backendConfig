package metaverse.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestMyPageController {
    @GetMapping("/test/mypage")
    public String testMyPage() {
        System.out.println("mypage");
        return "TEST MYPAGE";
    }
}
