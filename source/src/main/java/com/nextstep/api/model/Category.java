package com.nextstep.api.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "db_category")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Category extends Auditable<String>{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.nextstep.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;
    private String name;
    @Column(name = "description" ,  columnDefinition = "TEXT")
    private String description;
    private String image;
    private Integer ordering;
    private Integer kind; // (1:news, 2:skill, 3:job, 4:level, 5:education, 6: specialization)
    private String tag;
}
