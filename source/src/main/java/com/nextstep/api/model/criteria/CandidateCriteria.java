package com.nextstep.api.model.criteria;

import com.nextstep.api.model.Candidate;
import com.nextstep.api.model.Employee;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class CandidateCriteria implements Serializable {
    private Long id;
    private String jobTitle;
    private Boolean isAutoApply;
    private Boolean isJobSearching;
    private String fullName;
    private String phone;

    public Specification<Candidate> getSpecification() {
        return new Specification<Candidate>(){

            @Override
            public Predicate toPredicate(Root<Candidate> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                
                if (!StringUtils.isEmpty(getJobTitle())) {
                    predicates.add(cb.like(cb.lower(root.get("jobTitle")), "%" + getJobTitle().toLowerCase() + "%"));
                }
                
                if (getIsAutoApply() != null) {
                    predicates.add(cb.equal(root.get("isAutoApply"), getIsAutoApply()));
                }
                
                if (getIsJobSearching() != null) {
                    predicates.add(cb.equal(root.get("isJobSearching"), getIsJobSearching()));
                }
                if (!StringUtils.isEmpty(getFullName())) {
                    predicates.add(cb.like(cb.lower(root.get("account").get("fullName")), "%" + getFullName().toLowerCase() + "%"));
                }

                if (!StringUtils.isEmpty(getPhone())) {
                    predicates.add(cb.like(cb.lower(root.get("account").get("phone")), "%" + getPhone().toLowerCase() + "%"));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
