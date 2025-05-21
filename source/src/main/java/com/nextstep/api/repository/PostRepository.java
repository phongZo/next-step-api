package com.nextstep.api.repository;

import com.nextstep.api.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    @Modifying
    @Query("DELETE FROM Post p WHERE p.company.id = :companyId")
    void deleteAllByCompanyId(@Param("companyId") Long companyId);
    
    List<Post> findAllByCompanyId(Long companyId);
}
