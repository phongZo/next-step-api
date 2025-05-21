package com.nextstep.api.model.criteria;


import com.nextstep.api.model.Category;
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
public class EmployeeCriteria implements Serializable {
    private Long id;
    private String name;
    private String code;

    public  Specification<Employee> getSpecification() {
        return new Specification<Employee>(){

            @Override
            public Predicate toPredicate(Root<Employee> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (!StringUtils.isEmpty(getName())) {
                    predicates.add(cb.like(cb.lower(root.get("account").get("username")), "%" + getName().toLowerCase() + "%"));
                }
                if (!StringUtils.isEmpty(getCode())) {
                    predicates.add(cb.like(cb.lower(root.get("account").get("phone")), "%" + getCode().toLowerCase() + "%"));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
