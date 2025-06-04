package spl.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import spl.demo.dto.UserInfoDto;
import spl.demo.security.CustomUserDetails;
import spl.demo.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "로그인한 회원 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<UserInfoDto> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String userId = userDetails.getUsername(); // == user.getUserId()
        return ResponseEntity.ok(userService.getUserInfoByUserId(userId));
    }
}
