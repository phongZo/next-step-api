package com.nextstep.api.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

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
}
