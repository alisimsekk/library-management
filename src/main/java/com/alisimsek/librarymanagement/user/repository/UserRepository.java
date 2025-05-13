package com.alisimsek.librarymanagement.user.repository;

import com.alisimsek.librarymanagement.common.base.BaseRepository;
import com.alisimsek.librarymanagement.user.entity.User;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    default Optional<User> findByUsernameInNewTransaction(String username) {
        return findByUsername(username);
    }

}
