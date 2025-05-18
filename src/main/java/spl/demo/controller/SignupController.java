package spl.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spl.demo.dto.SignupDto;
import spl.demo.service.SignupService;
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500"})
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

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkUsername(@RequestParam("userId") String userId) {
        return ResponseEntity.ok(signupService.isUsernameTaken(userId));
    }
}
