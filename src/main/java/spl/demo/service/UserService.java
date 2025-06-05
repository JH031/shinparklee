package spl.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spl.demo.dto.UserInfoDto;
import spl.demo.dto.UserUpdateRequestDto;
import spl.demo.entity.SignupEntity;
import spl.demo.repository.SignupRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SignupRepository signupRepository;

    public UserInfoDto getUserInfoByUserId(String userId) {
        SignupEntity user = signupRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        return new UserInfoDto(
                user.getUsername(),
                user.getUserId(),
                user.getEmail(),
                user.getInterestCategories(),
                user.getStyle()
        );
    }
    @Transactional
    public void updateUserInfo(String currentUserId, UserUpdateRequestDto dto) {
        SignupEntity user = signupRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        if (dto.getInterestCategories() != null) {
            user.setInterestCategories(dto.getInterestCategories());
        }

        if (dto.getStyle() != null) {
            user.setStyle(dto.getStyle());
        }
    }


}
