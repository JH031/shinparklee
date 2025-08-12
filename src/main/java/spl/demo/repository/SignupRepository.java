package spl.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spl.demo.entity.SignupEntity;

import java.util.Optional;

public interface SignupRepository extends JpaRepository<SignupEntity, Long> {

    boolean existsByUserId(String userId);

    Optional<SignupEntity> findByUserId(String userId);
}
