package com.ansimyoyang.controller;

import com.ansimyoyang.domain.Institution;
import com.ansimyoyang.repository.InstitutionJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/institutions")
@CrossOrigin(origins = "*")
public class InstitutionController {
    
    @Autowired
    private InstitutionJdbcRepository institutionRepository;
    
    // 모든 기관 조회
    @GetMapping
    public ResponseEntity<List<Institution>> getAllInstitutions() {
        try {
            List<Institution> institutions = institutionRepository.findAll();
            return ResponseEntity.ok(institutions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // ID로 기관 조회
    @GetMapping("/{institutionId}")
    public ResponseEntity<Institution> getInstitutionById(@PathVariable Long institutionId) {
        try {
            Optional<Institution> institution = institutionRepository.findById(institutionId);
            if (institution.isPresent()) {
                return ResponseEntity.ok(institution.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // 요양원명과 전화번호로 기관 조회
    @GetMapping("/search")
    public ResponseEntity<Institution> getInstitutionByNameAndPhone(
            @RequestParam String institutionName,
            @RequestParam String phoneNumber) {
        try {
            Optional<Institution> institution = institutionRepository
                .findByNameAndPhone(institutionName, phoneNumber);
            if (institution.isPresent()) {
                return ResponseEntity.ok(institution.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // 요양원명으로 기관 조회
    @GetMapping("/search/name")
    public ResponseEntity<List<Institution>> getInstitutionsByName(@RequestParam String institutionName) {
        try {
            List<Institution> institutions = institutionRepository.findByName(institutionName);
            return ResponseEntity.ok(institutions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}