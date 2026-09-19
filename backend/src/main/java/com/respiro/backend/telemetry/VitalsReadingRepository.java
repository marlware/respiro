package com.respiro.backend.telemetry;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VitalsReadingRepository extends JpaRepository<VitalsReadingEntity, Long> {

    List<VitalsReadingEntity> findTop50ByOrderByRecordedAtDesc();

    VitalsReadingEntity findFirstByOrderByRecordedAtDesc();
}
