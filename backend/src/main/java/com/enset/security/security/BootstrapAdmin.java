package com.enset.security.security;

import com.enset.security.model.User;
import com.enset.security.model.enums.Role;
import com.enset.security.repository.UserRepository;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BootstrapAdmin implements ApplicationRunner {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;

    public BootstrapAdmin(UserRepository users, PasswordEncoder passwordEncoder,
                          @Value("${app.bootstrap-admin.email:}") String email,
                          @Value("${app.bootstrap-admin.password:}") String password) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (email.isBlank() || password.isBlank()) return;
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        if (!users.existsByEmail(normalizedEmail)) {
            users.save(new User("Administrator", normalizedEmail, passwordEncoder.encode(password), Role.ADMIN));
        }
    }
}
