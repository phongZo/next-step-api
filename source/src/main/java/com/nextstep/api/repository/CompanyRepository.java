package com.nextstep.api.repository;

import com.nextstep.api.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {
    boolean existsByNameAndStatus(String name, int status);
    boolean existsByHotlineAndStatus(String name, int status);
}
