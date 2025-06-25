package com.nextstep.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "db_job_application")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class JobApplication extends Auditable<String>{
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "com.nextstep.api.service.id.IdGenerator")
    @GeneratedValue(generator = "idGenerator")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private String cv;

    @Column(columnDefinition = "LONGTEXT")
    private String candidateInfo;

    @Column(columnDefinition = "LONGTEXT")
    private String coverLetter;

    private Integer state;//(0: pending, 1: approved, 2: cancelled)
} 