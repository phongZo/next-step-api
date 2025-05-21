package com.nextstep.api.model.criteria;


import com.nextstep.api.model.Employee;
import com.nextstep.api.model.Post;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostCriteria {
    private Long id;
    private String name;
    private Integer experience;
    private String tag;
    private String level;
    private Integer type;
    private Integer contractType;

    public Specification<Post> getSpecification() {
        return new Specification<Post>(){

            @Override
            public Predicate toPredicate(Root<Post> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (!StringUtils.isEmpty(getName())) {
                    predicates.add(cb.like(cb.lower(root.get("name")), "%" + getName().toLowerCase() + "%"));
                }
                if (getExperience() != null) {
                    predicates.add(cb.equal(root.get("experience"), getExperience()));
                }

                if (!StringUtils.isEmpty(getTag())) {
                    predicates.add(cb.like(cb.lower(root.get("tag")), "%" + getTag().toLowerCase() + "%"));
                }

                if (!StringUtils.isEmpty(getLevel())) {
                    predicates.add(cb.equal(cb.lower(root.get("level")), getLevel().toLowerCase()));
                }

                if (getType() != null) {
                    predicates.add(cb.equal(root.get("type"), getType()));
                }

                if (getContractType() != null) {
                    predicates.add(cb.equal(root.get("contractType"), getContractType()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
