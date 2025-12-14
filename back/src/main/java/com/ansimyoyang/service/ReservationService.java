package com.ansimyoyang.service;

import com.ansimyoyang.domain.dto.*;
import com.ansimyoyang.domain.*;
import com.ansimyoyang.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {
    
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final GuardianRepository guardianRepository;
    private final CaregiverRepository caregiverRepository;
    private final NotificationService notificationService;
    
    public ReservationResponseDto createReservation(ReservationRequestDto request) {
        
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("환자를 찾을 수 없습니다."));
        
        // guardianId가 있으면 보호자를 조회, 없으면 null로 설정 (요양사가 직접 일정 생성하는 경우)
        Guardian guardian = null;
        if (request.getGuardianId() != null) {
            guardian = guardianRepository.findById(request.getGuardianId())
                    .orElseThrow(() -> new RuntimeException("보호자를 찾을 수 없습니다."));
        }
        
        Appointment appointment = Appointment.builder()
                .patient(patient)
                .guardian(guardian)
                .appointmentType(Appointment.AppointmentType.valueOf(request.getAppointmentType()))
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .purpose(request.getPurpose())
                .reason(request.getReason())
                .guardianNotes(request.getGuardianNotes())
                .visitorRelationship(request.getVisitorRelationship())
                .visitorCount(request.getVisitorCount())
                .status(Appointment.AppointmentStatus.PENDING)
                .scheduledAt(request.getStartTime())
                .build();
        
        appointment = appointmentRepository.save(appointment);
        
        // 새 예약 요청 알림 생성 (기관 요양보호사들에게 알림)
        try {
            // 환자가 속한 기관의 요양보호사들에게 알림
            List<Caregiver> caregivers = caregiverRepository.findByInstitution_InstitutionId(patient.getInstitution().getInstitutionId());
            String patientName = patient.getName();
            String guardianName = guardian.getName();
            
            for (Caregiver caregiver : caregivers) {
                notificationService.createAppointmentNotification(
                    caregiver.getCaregiverId(),
                    com.ansimyoyang.domain.Notification.UserType.CAREGIVER,
                    patientName + "님의 새로운 예약 요청",
                    guardianName + "님이 " + patientName + "님의 " + request.getAppointmentType() + " 예약을 요청했습니다.",
                    appointment.getAppointmentId()
                );
            }
        } catch (Exception e) {
            // 알림 생성 실패해도 예약 생성은 성공으로 처리
        }
        
        return convertToResponseDto(appointment);
    }
    
    public AvailabilityCheckDto checkAvailability(
            Long institutionId, LocalDate reservationDate, 
            LocalTime startTime, LocalTime endTime, int requestedVisitors) {
        
        LocalDateTime startDateTime = LocalDateTime.of(reservationDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(reservationDate, endTime);
        
        List<Appointment> conflictingAppointments = appointmentRepository
                .findConflictingAppointments(startDateTime, endDateTime);
        
        int currentVisitors = conflictingAppointments.stream()
                .filter(apt -> "APPROVED".equals(apt.getStatus()))
                .mapToInt(Appointment::getVisitorCount)
                .sum();
        
        boolean isAvailable = currentVisitors + requestedVisitors <= 10;
        
        List<String> conflictReasons = new ArrayList<>();
        if (!isAvailable) {
            conflictReasons.add("동시간대 방문자 수 초과");
        }
        
        List<AvailabilityCheckDto.AlternativeTimeSlot> alternatives = new ArrayList<>();
        if (!isAvailable) {
            alternatives = generateAlternativeTimeSlots(reservationDate, requestedVisitors);
        }
        
        return AvailabilityCheckDto.builder()
                .institutionId(institutionId)
                .reservationDate(reservationDate)
                .startTime(startTime)
                .endTime(endTime)
                .requestedVisitors(requestedVisitors)
                .isAvailable(isAvailable)
                .reasonIfNotAvailable(isAvailable ? null : "시간대 충돌")
                .conflictReasons(conflictReasons)
                .institutionStartTime(LocalTime.of(9, 0))
                .institutionEndTime(LocalTime.of(20, 0))
                .maxConcurrentVisits(5)
                .maxVisitorsPerReservation(3)
                .currentReservations(conflictingAppointments.size())
                .currentVisitors(currentVisitors)
                .suggestedAlternatives(alternatives)
                .build();
    }
    
    public ReservationResponseDto processApproval(Long appointmentId, ReservationApprovalDto approval) {
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));
        
        appointment.setStatus(approval.getApprovalStatus());
        appointment.setStaffNotes(approval.getStaffNotes());
        appointment.setProcessedAt(LocalDateTime.now());
        
        if (approval.getApprovedBy() != null) {
            Caregiver approver = caregiverRepository.findById(approval.getApprovedBy())
                    .orElse(null);
            appointment.setApprovedBy(approver);
        }
        
        if (approval.getAdjustedStartTime() != null) {
            appointment.setStartTime(approval.getAdjustedStartTime());
        }
        
        if (approval.getAdjustedEndTime() != null) {
            appointment.setEndTime(approval.getAdjustedEndTime());
        }
        
        appointment = appointmentRepository.save(appointment);
        
        // 예약 승인/거부 알림 생성 (보호자에게 알림)
        try {
            if (appointment.getGuardian() != null) {
                Long guardianId = appointment.getGuardian().getGuardianId();
                String patientName = appointment.getPatient().getName();
                String statusText = "APPROVED".equals(approval.getApprovalStatus()) ? "승인" : "거부";
                String title = patientName + "님의 예약 " + statusText;
                String message = patientName + "님의 " + appointment.getAppointmentType() + " 예약이 " + statusText + "되었습니다.";
                
                notificationService.createAppointmentNotification(
                    guardianId,
                    com.ansimyoyang.domain.Notification.UserType.GUARDIAN,
                    title,
                    message,
                    appointment.getAppointmentId()
                );
            }
        } catch (Exception e) {
            // 알림 생성 실패해도 승인 처리는 성공으로 처리
        }
        
        return convertToResponseDto(appointment);
    }
    
    public List<ReservationResponseDto> getReservationsByGuardian(Long guardianId) {
        List<Appointment> appointments = appointmentRepository.findByGuardian_GuardianIdOrderByStartTimeDesc(guardianId);
        return appointments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<ReservationResponseDto> getReservationsByPatient(Long patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatient_PatientIdOrderByStartTimeDesc(patientId);
        return appointments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<ReservationResponseDto> getPendingReservations() {
        List<Appointment> appointments = appointmentRepository.findByStatusOrderByScheduledAtAsc(Appointment.AppointmentStatus.PENDING);
        return appointments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public List<ReservationResponseDto> getReservationsByInstitution(
            Long institutionId, String status, LocalDate date) {
        
        List<Appointment> appointments;
        Appointment.AppointmentStatus statusEnum = null;
        
        if (status != null) {
            try {
                statusEnum = Appointment.AppointmentStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status: " + status);
            }
        }
        
        if (statusEnum != null && date != null) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);
            appointments = appointmentRepository.findByInstitutionAndStatusAndDateRange(institutionId, statusEnum, startOfDay, endOfDay);
        } else if (statusEnum != null) {
            appointments = appointmentRepository.findByInstitutionAndStatus(institutionId, statusEnum);
        } else if (date != null) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);
            appointments = appointmentRepository.findByInstitutionAndDateRange(institutionId, startOfDay, endOfDay);
        } else {
            appointments = appointmentRepository.findByPatient_Institution_InstitutionIdOrderByScheduledAtDesc(institutionId);
        }
        
        return appointments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    public ReservationResponseDto cancelReservation(Long appointmentId, String reason) {
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));
        
        appointment.setStatus("CANCELLED");
        appointment.setStaffNotes(reason);
        appointment.setProcessedAt(LocalDateTime.now());
        
        appointment = appointmentRepository.save(appointment);
        
        return convertToResponseDto(appointment);
    }
    
    private List<AvailabilityCheckDto.AlternativeTimeSlot> generateAlternativeTimeSlots(
            LocalDate date, int requestedVisitors) {
        
        List<AvailabilityCheckDto.AlternativeTimeSlot> alternatives = new ArrayList<>();
        LocalTime currentTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(20, 0);
        
        while (currentTime.isBefore(endTime.minusHours(1))) {
            LocalDateTime slotStart = LocalDateTime.of(date, currentTime);
            LocalDateTime slotEnd = slotStart.plusHours(2);
            
            List<Appointment> conflicting = appointmentRepository
                    .findConflictingAppointments(slotStart, slotEnd);
            
            int currentVisitors = conflicting.stream()
                    .filter(apt -> "APPROVED".equals(apt.getStatus()))
                    .mapToInt(Appointment::getVisitorCount)
                    .sum();
            
            int availableSlots = 10 - currentVisitors;
            
            if (availableSlots >= requestedVisitors) {
                alternatives.add(AvailabilityCheckDto.AlternativeTimeSlot.builder()
                        .startTime(currentTime)
                        .endTime(currentTime.plusHours(2))
                        .availableVisitors(availableSlots)
                        .build());
            }
            
            currentTime = currentTime.plusMinutes(30);
        }
        
        return alternatives.stream().limit(3).collect(Collectors.toList());
    }
    
    private ReservationResponseDto convertToResponseDto(Appointment appointment) {
        return ReservationResponseDto.builder()
                .appointmentId(appointment.getAppointmentId())
                .patientId(appointment.getPatient().getPatientId())
                .patientName(appointment.getPatient().getName())
                .guardianId(appointment.getGuardian() != null ? 
                    appointment.getGuardian().getGuardianId() : null)
                .guardianName(appointment.getGuardian() != null ? 
                    appointment.getGuardian().getName() : null)
                .appointmentType(appointment.getAppointmentType())
                .status(appointment.getStatus())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .scheduledAt(appointment.getScheduledAt())
                .purpose(appointment.getPurpose())
                .reason(appointment.getReason())
                .guardianNotes(appointment.getGuardianNotes())
                .staffNotes(appointment.getStaffNotes())
                .visitorRelationship(appointment.getVisitorRelationship())
                .visitorCount(appointment.getVisitorCount())
                .approvedBy(appointment.getApprovedBy() != null ? 
                    appointment.getApprovedBy().getCaregiverId() : null)
                .approvedByName(appointment.getApprovedBy() != null ? 
                    appointment.getApprovedBy().getName() : null)
                .processedAt(appointment.getProcessedAt())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .companions(new ArrayList<>())
                .build();
    }
}