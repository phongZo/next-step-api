package com.nextstep.api.repository;

import com.nextstep.api.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    public Account findAccountByUsername(String username);
    public Account findAccountByEmail(String email);
    public Account findAccountByPhone(String phone);
    public Account findAccountByResetPwdCode(String resetPwdCode);
    public Account findAccountByEmailOrUsername(String email, String username);
    public Page<Account> findAllByKind(int kind, Pageable pageable);
    boolean existsByUsername(String username);
    boolean existsByPhone(String phone);
    public Account findAccountByEmailAndKind(String phone,int kind);
    Optional<Account> findByIdAndStatus(Long id, Integer status);
    Optional<Account> findByEmailAndPlatform(String email, Integer platform);
    Optional<Account> findByIdAndKind(Long id, Integer kind);
}
