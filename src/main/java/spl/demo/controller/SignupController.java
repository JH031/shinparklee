package spl.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spl.demo.dto.SignupDto;
import spl.demo.service.SignupService;

@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500"})  // 원격 브랜치의 설정 유지
@RestController
@RequestMapping("/api/signup") //회원가입 요청 처리 API
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
    @Operation(summary = "ID 중복 체크")
    @GetMapping("/check") // ID 중복 체크
    public ResponseEntity<Boolean> checkUsername(@RequestParam("userId") String userId) {
        return ResponseEntity.ok(signupService.isUsernameTaken(userId));
    }
}
