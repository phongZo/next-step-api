package com.nextstep.api.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "db_post")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Post extends Auditable<String>{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.nextstep.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description",columnDefinition = "text")
    private String description;
    @Column(name = "expire_date")
    private Date expireDate;
    @Column(name = "tag")
    private String tag;
    @Column(name = "experience")
    private Integer experience;
    @Column(name = "level")
    private String level;
    @Column(name = "type")
    private Integer type; // (0: home, 1: office, 2: remote, 3: hybrid)
    @Column(name = "contract_type")
    private Integer contractType; // (0: partTime, 1: fulltime, 2: collab)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

}
