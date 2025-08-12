package spl.demo.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import spl.demo.entity.SignupEntity;
import spl.demo.repository.SignupRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final SignupRepository signupRepository;

    public CustomUserDetailsService(SignupRepository signupRepository) {
        this.signupRepository = signupRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        SignupEntity user = signupRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));
        return new CustomUserDetails(user);
    }
}
