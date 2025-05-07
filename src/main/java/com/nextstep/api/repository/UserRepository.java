package com.nextstep.api.repository;

import com.nextstep.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {

//    User findUsersByPhone(String phone);
//    User findUsersByEmail(String email);
    Optional<User> findByAccountId(Long accountId);
    @Transactional
    @Modifying
    void deleteAllByAccountId(Long accountId);
}
