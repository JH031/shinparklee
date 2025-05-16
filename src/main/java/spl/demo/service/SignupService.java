package spl.demo.service;

import org.springframework.stereotype.Service;
import spl.demo.dto.SignupDto;
import spl.demo.entity.SignupEntity;
import spl.demo.repository.SignupRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class SignupService {

    private final SignupRepository signupRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public SignupService(SignupRepository signupRepository) {
        this.signupRepository = signupRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(); // 비밀번호 암호화 (꼭 필요한지는 모르겠음)
    }

    public void signup(SignupDto dto) {
        if (signupRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        SignupEntity user = new SignupEntity();
        user.setUsername(dto.getUsername());
        user.setUserId(dto.getUserId());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setInterestCategories(dto.getInterestCategories());

        signupRepository.save(user);
    }

    public boolean isUsernameTaken(String userId) {
        return signupRepository.existsByUsername(userId); //중복체크
    }
}
