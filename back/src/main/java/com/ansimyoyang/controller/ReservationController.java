package com.ansimyoyang.controller;

import com.ansimyoyang.domain.dto.*;
import com.ansimyoyang.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    
    private final ReservationService reservationService;
    
    @PostMapping
    public ResponseEntity<ReservationResponseDto> createReservation(@RequestBody ReservationRequestDto request) {
        ReservationResponseDto response = reservationService.createReservation(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/availability")
    public ResponseEntity<AvailabilityCheckDto> checkAvailability(
            @RequestParam Long institutionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reservationDate,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime endTime,
            @RequestParam int requestedVisitors
    ) {
        AvailabilityCheckDto availability = reservationService.checkAvailability(
            institutionId, reservationDate, startTime, endTime, requestedVisitors
        );
        return ResponseEntity.ok(availability);
    }
    
    @PutMapping("/{appointmentId}/approval")
    public ResponseEntity<ReservationResponseDto> processApproval(
            @PathVariable Long appointmentId,
            @RequestBody ReservationApprovalDto approval
    ) {
        ReservationResponseDto response = reservationService.processApproval(appointmentId, approval);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/guardian/{guardianId}")
    public ResponseEntity<List<ReservationResponseDto>> getReservationsByGuardian(@PathVariable Long guardianId) {
        List<ReservationResponseDto> reservations = reservationService.getReservationsByGuardian(guardianId);
        return ResponseEntity.ok(reservations);
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ReservationResponseDto>> getReservationsByPatient(@PathVariable Long patientId) {
        List<ReservationResponseDto> reservations = reservationService.getReservationsByPatient(patientId);
        return ResponseEntity.ok(reservations);
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<ReservationResponseDto>> getPendingReservations() {
        List<ReservationResponseDto> reservations = reservationService.getPendingReservations();
        return ResponseEntity.ok(reservations);
    }
    
    @GetMapping("/institution/{institutionId}")
    public ResponseEntity<List<ReservationResponseDto>> getReservationsByInstitution(
            @PathVariable Long institutionId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<ReservationResponseDto> reservations = reservationService.getReservationsByInstitution(
            institutionId, status, date
        );
        return ResponseEntity.ok(reservations);
    }
    
    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<ReservationResponseDto> cancelReservation(
            @PathVariable Long appointmentId,
            @RequestParam String reason
    ) {
        ReservationResponseDto response = reservationService.cancelReservation(appointmentId, reason);
        return ResponseEntity.ok(response);
    }
}