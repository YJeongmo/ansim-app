package com.ansimyoyang.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "guardian")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Guardian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guardian_id")
    private Long guardianId;

    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "phone")
    private String phone;

    @Column(name = "relationship")
    private String relationship; // 배우자, 자녀, 형제 등

    @OneToOne(mappedBy = "guardian")
    @JsonIgnore
    private Patient patient;

    @Column(name = "email")
    private String email;

    @Column(name = "address")
    private String address;
}

