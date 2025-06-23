package com.nextstep.api.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "db_job_recommend")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class JobRecommend extends Auditable<String>{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.nextstep.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @Column(name = "matching_score")
    private Double matchingScore;

    @Column(name = "matching", columnDefinition = "LONGTEXT")
    private String matching;

    @Column(name = "summary_cv", columnDefinition = "LONGTEXT")
    private String summaryCv;
}
