package spl.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spl.demo.dto.SignupDto;
import spl.demo.service.SignupService;

@RestController
@RequestMapping("/api/signup")
public class SignupController {

    private final SignupService signupService;

    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping
    public ResponseEntity<String> signup(@RequestBody SignupDto dto) {
        signupService.signup(dto);
        return ResponseEntity.ok("회원가입 성공");
    }

    @GetMapping("/check") //ID 중복체크
    public ResponseEntity<Boolean> checkUsername(@RequestParam String userId) {
        return ResponseEntity.ok(signupService.isUsernameTaken(userId));
    }
}
