package com.nextstep.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

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
} 