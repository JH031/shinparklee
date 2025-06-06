package spl.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spl.demo.dto.SignupDto;
import spl.demo.entity.SignupEntity;
import spl.demo.repository.SignupRepository;

@Service
public class SignupService {

    private final SignupRepository signupRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupService(SignupRepository signupRepository,
                         PasswordEncoder passwordEncoder) {
        this.signupRepository = signupRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void signup(SignupDto dto) {
        // ❗ userId 기준으로 중복 체크
        if (signupRepository.existsByUserId(dto.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        SignupEntity user = new SignupEntity();
        user.setUsername(dto.getUsername()); // 유저 이름은 중복 허용
        user.setUserId(dto.getUserId());     // 로그인용 ID (중복 불가)
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setInterestCategories(dto.getInterestCategories());

        signupRepository.save(user);
    }

    public boolean isUsernameTaken(String userId) {
        // ❗ userId 기준 중복 체크
        return signupRepository.existsByUserId(userId);
    }
}
