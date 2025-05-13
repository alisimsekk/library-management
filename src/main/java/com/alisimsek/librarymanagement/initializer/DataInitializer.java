package com.alisimsek.librarymanagement.initializer;

import com.alisimsek.librarymanagement.user.entity.User;
import com.alisimsek.librarymanagement.user.entity.UserType;
import com.alisimsek.librarymanagement.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.findAll().isEmpty()) {
            return;
        }

        User adminUser = getAdminUser();
        userRepository.save(adminUser);

        User librarianUser = getLibrarianUser();
        userRepository.save(librarianUser);

        User patronUser = getPatronUser();
        userRepository.save(patronUser);
    }

    private User getAdminUser() {
        User user = new User();
        user.setUsername("admin@mail.com");
        user.setPassword(passwordEncoder.encode("Aa123456"));
        user.setFirstName("AdminFirstname");
        user.setLastName("AdminLastname");
        user.setUserType(UserType.ADMIN);
        return user;
    }

    private User getLibrarianUser() {
        User user = new User();
        user.setUsername("librarian@mail.com");
        user.setPassword(passwordEncoder.encode("Aa123456"));
        user.setFirstName("LibrarianFirstname");
        user.setLastName("LibrarianLastname");
        user.setUserType(UserType.LIBRARIAN);
        return user;
    }

    private User getPatronUser() {
        User user = new User();
        user.setUsername("patron@mail.com");
        user.setPassword(passwordEncoder.encode("Aa123456"));
        user.setFirstName("PatronFirstname");
        user.setLastName("PatronLastname");
        user.setUserType(UserType.PATRON);
        return user;
    }

}
