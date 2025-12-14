package com.ansimyoyang.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "caregiver")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Caregiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "caregiver_id")
    private Long caregiverId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "staff_code")
    private String staffCode;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private CaregiverRole role = CaregiverRole.STAFF;
    
    @Column(name = "account_id")
    private Long accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "patients", "caregivers"})
    private Institution institution;

    @OneToMany(mappedBy = "caregiver")
    @JsonIgnore
    private List<Activity> activities = new ArrayList<>();
}
