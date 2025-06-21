package com.nextstep.api.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "db_employee")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Employee extends Auditable<String>{
    @Id
    private Long id;
    private String name;
    private String code;
    @OneToOne
    @MapsId
    @JoinColumn(name = "id", referencedColumnName = "id", nullable = false)
    private Account account;
    private boolean isManager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinTable(name = "db_permission_employee",
            joinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id",
                    referencedColumnName = "id"))
    private List<Permission> permissions = new ArrayList<>();
}
