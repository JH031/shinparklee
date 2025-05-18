package spl.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import spl.demo.dto.LoginDto;
import spl.demo.dto.TokenResponse;
import spl.demo.security.JwtUtil;
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500"})
@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginController(AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUserId(), dto.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

            String token = jwtUtil.generateToken(dto.getUserId());
            return ResponseEntity.ok(new TokenResponse(token));

        } catch (UsernameNotFoundException ex) {
            // 가입된 회원이 없을 때
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("등록된 회원이 없습니다.");

        } catch (BadCredentialsException ex) {
            // 비밀번호가 틀렸을 때
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("비밀번호가 일치하지 않습니다.");
        }
    }
}
