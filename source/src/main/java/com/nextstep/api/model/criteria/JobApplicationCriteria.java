package com.nextstep.api.model.criteria;

import com.nextstep.api.model.JobApplication;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class JobApplicationCriteria {
    private Long id;
    private Long candidateId;
    private Long postId;
    private Integer state;

    public Specification<JobApplication> getSpecification() {
        return new Specification<JobApplication>(){

            @Override
            public Predicate toPredicate(Root<JobApplication> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                
                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                
                if (getCandidateId() != null) {
                    predicates.add(cb.equal(root.get("candidate").get("id"), getCandidateId()));
                }
                
                if (getPostId() != null) {
                    predicates.add(cb.equal(root.get("post").get("id"), getPostId()));
                }
                
                if (getState() != null) {
                    predicates.add(cb.equal(root.get("state"), getState()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
} 