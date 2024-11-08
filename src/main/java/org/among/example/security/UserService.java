package org.among.example.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.among.example.security.CustomUserDetails;
import org.among.example.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class UserService implements UserDetailsService {
    private static final String userFilePath = "data/user.json";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            List<User> users = readUsers(); // DB는 아니지만 일종의 DB처럼 사용 위해 CustomUserDetails와 분리
            User foundUser = users.stream()
                    .filter(user -> user.getId().equals(username))
                    .findFirst()
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            foundUser.setPassword(passwordEncoder.encode(foundUser.getPassword())); // 로그인 시 암호화된 PW 비교해서 임시로 이렇게 작성
            return createUserDetails(foundUser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<User> readUsers() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(userFilePath);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(classPathResource.getInputStream(), new TypeReference<Map<String, List<User>>>() {}).get("user");
    }

    private UserDetails createUserDetails(User user) {
        return new CustomUserDetails(user);
    }
}
