package com.nextstep.api.model.criteria;

import com.nextstep.api.model.Group;
import com.nextstep.api.model.Nation;
import com.nextstep.api.model.Permission;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class PermissionCriteria {
    private Integer kind;

    public static Specification<Permission> getSpecification(final PermissionCriteria permissionCriteria) {
        return new Specification<Permission>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Permission> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (permissionCriteria.getKind() != null) {
                    Join<Permission, Group> groups = root.join("groups", JoinType.INNER);

                    predicates.add(cb.equal(groups.get("kind"), permissionCriteria.getKind()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}
