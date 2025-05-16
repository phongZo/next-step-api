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
    private String name;

    public Specification<Candidate> getSpecification() {
        return new Specification<Candidate>(){

            @Override
            public Predicate toPredicate(Root<Candidate> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (!StringUtils.isEmpty(getName())) {
                    predicates.add(cb.like(cb.lower(root.get("account").get("username")), "%" + getName().toLowerCase() + "%"));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
