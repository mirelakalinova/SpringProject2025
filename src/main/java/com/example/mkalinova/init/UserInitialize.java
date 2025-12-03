package com.example.mkalinova.init;

import com.example.mkalinova.app.user.data.entity.User;
import com.example.mkalinova.app.user.data.entity.UsersRole;
import com.example.mkalinova.app.user.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserInitialize implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserInitialize(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }


    @Override
    public void run(String... args) throws Exception {

        if (userRepository.count() == 0) {
            User user = new User();
            user.setEmail("admin@test.bg");
			user.setEnabled(true);
            String pass = encoder.encode("123");
            user.setPassword(pass);
            user.setUsername("dorela-auto");
            user.setFirstName("Dorela");
            user.setLastName("Auto");
            user.setRole(UsersRole.ADMIN);
            userRepository.saveAndFlush(user);
        }
    }
}
