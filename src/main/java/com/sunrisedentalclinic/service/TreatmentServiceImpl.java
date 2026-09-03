package com.sunrisedentalclinic.service;

import com.sunrisedentalclinic.dto.TreatmentSummary;
import com.sunrisedentalclinic.repository.TreatmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TreatmentServiceImpl implements TreatmentService {

    private final TreatmentRepository treatmentRepository;

    public TreatmentServiceImpl(TreatmentRepository treatmentRepository) {
        this.treatmentRepository = treatmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentSummary> listAll() {
        return treatmentRepository.findAll().stream()
                .map(t -> new TreatmentSummary(t.getId(), t.getName(), t.getDescription(), t.getBaseCost()))
                .toList();
    }
}
