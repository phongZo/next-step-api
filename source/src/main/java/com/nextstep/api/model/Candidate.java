package com.nextstep.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "db_candidate")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Candidate extends Auditable<String> {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id", referencedColumnName = "id", nullable = false)
    private Account account;
    
    @Column(name = "job_title")
    private String jobTitle;
    
    @Column(name = "is_auto_apply")
    private Boolean isAutoApply = false;
    
    @Column(name = "is_job_searching")
    private Boolean isJobSearching = false;

    @Column(name = "cover_letter", columnDefinition = "LONGTEXT")
    private String coverLetter;

    @Column(name = "code")
    private String code;

    @Column(name = "experience")
    private Integer experience;

    @Column(name = "speciality")
    private String speciality;

    @Column(name = "work_area")
    private String workArea;

    @Column(name = "allow_company_contact")
    private Boolean allowCompanyContact = false;

    @Column(name = "cv")
    private String cv;

    @Column(name = "cvState")
    private Integer cvState; //(0: pending, 1: done, 2: error)

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "db_favorite_post_candidate",
            joinColumns = @JoinColumn(name = "candidate_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"))
    private List<Post> favoritePosts;
} 