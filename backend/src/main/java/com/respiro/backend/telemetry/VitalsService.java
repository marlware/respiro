package com.respiro.backend.telemetry;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VitalsService {

    private final VitalsReadingRepository repository;

    public VitalsService(VitalsReadingRepository repository) {
        this.repository = repository;
    }

    public Optional<VitalsReading> latest() {
        return Optional.ofNullable(repository.findFirstByOrderByRecordedAtDesc())
                .map(VitalsReadingEntity::toReading);
    }

    public List<VitalsReading> recentHistory() {
        return repository.findTop50ByOrderByRecordedAtDesc().stream()
                .map(VitalsReadingEntity::toReading)
                .toList();
    }
}
