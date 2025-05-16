package spl.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spl.demo.entity.SignupEntity;

import java.util.Optional;

public interface SignupRepository extends JpaRepository<SignupEntity, Long> {
    boolean existsByUsername(String userId); // 아이디 중복 확인

    Optional<SignupEntity> findByUserId(String userId);
}
