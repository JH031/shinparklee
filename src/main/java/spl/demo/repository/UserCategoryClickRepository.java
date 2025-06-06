package spl.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spl.demo.entity.*;

import java.util.List;
import java.util.Optional;

public interface UserCategoryClickRepository extends JpaRepository<UserCategoryClick, Long> {
    Optional<UserCategoryClick> findByUserAndCategory(SignupEntity user, InterestCategoryEntity category);
    List<UserCategoryClick> findAllByUser(SignupEntity user);
}
