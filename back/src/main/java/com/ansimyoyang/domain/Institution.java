package com.ansimyoyang.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "institution")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Institution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "institution_id")
    private Long institutionId;

    @Column(name = "name", nullable = false)
    private String institutionName;

    private String address;

    @Column(name = "phone")
    private String phoneNumber;

    // DB에 rating이 DECIMAL 이므로 엔티티도 BigDecimal로 맞춘다.
    // (precision/scale을 굳이 지정하지 않아도 DECIMAL로 인식됨)
    @Column(name = "rating")
    private BigDecimal rating;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "institution_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private InstitutionSettings settings;
}
