package com.nextstep.api.repository;

import com.nextstep.api.model.JobRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JobRecommendRepository extends JpaRepository<JobRecommend, Long>, JpaSpecificationExecutor<JobRecommend> {
}
