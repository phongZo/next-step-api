package com.nextstep.api.model.criteria;


import com.nextstep.api.model.Category;
import com.nextstep.api.model.Company;
import com.nextstep.api.model.Employee;
import com.nextstep.api.model.Post;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
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
    private String companyName;
    private Long categoryId;
    private Integer categoryKind;

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
                
                if (!StringUtils.isEmpty(getCompanyName())) {
                    Join<Post, Company> companyJoin = root.join("company", JoinType.INNER);
                    predicates.add(cb.like(cb.lower(companyJoin.get("name")), "%" + getCompanyName().toLowerCase() + "%"));
                }
                if (getCategoryId() != null) {
                    predicates.add(cb.equal(root.get("category").get("id"), getCategoryId()));
                }
                if (getCategoryKind() != null) {
                    predicates.add(cb.equal(root.get("category").get("kind"), getCategoryKind()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
