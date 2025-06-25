package com.nextstep.api.repository;

import com.nextstep.api.model.JobApplication;
import com.nextstep.api.model.JobRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long>, JpaSpecificationExecutor<JobApplication> {
} 