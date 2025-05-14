package com.nextstep.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "db_company")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Company extends Auditable<String>{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.nextstep.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description",columnDefinition = "text")
    private String description;
    @Column(name = "short_description")
    private String shortDescription;
    @Column(name = "hotline", unique = true)
    private String hotline;
    @Column(name = "logo")
    private String logo;
    @Column(name = "banner")
    private String banner;
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employee> employees = new ArrayList<>();
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

}
