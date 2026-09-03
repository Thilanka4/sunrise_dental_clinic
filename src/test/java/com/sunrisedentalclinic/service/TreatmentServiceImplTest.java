package com.sunrisedentalclinic.service;

import com.sunrisedentalclinic.dto.TreatmentSummary;
import com.sunrisedentalclinic.model.Treatment;
import com.sunrisedentalclinic.repository.TreatmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TreatmentServiceImplTest {

    @Mock
    private TreatmentRepository treatmentRepository;

    @Test
    void listAll_mapsEntitiesToSummaries() {
        Treatment treatment = new Treatment("General Checkup", "Routine exam", new BigDecimal("1500.00"));
        when(treatmentRepository.findAll()).thenReturn(List.of(treatment));

        List<TreatmentSummary> result = new TreatmentServiceImpl(treatmentRepository).listAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("General Checkup");
        assertThat(result.get(0).baseCost()).isEqualByComparingTo("1500.00");
    }

    @Test
    void listAll_noTreatments_returnsEmptyList() {
        when(treatmentRepository.findAll()).thenReturn(List.of());

        assertThat(new TreatmentServiceImpl(treatmentRepository).listAll()).isEmpty();
    }
}
