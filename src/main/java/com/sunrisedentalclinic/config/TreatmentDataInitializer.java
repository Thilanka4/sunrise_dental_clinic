package com.sunrisedentalclinic.config;

import com.sunrisedentalclinic.model.Treatment;
import com.sunrisedentalclinic.repository.TreatmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class TreatmentDataInitializer {

    @Bean
    CommandLineRunner seedTreatments(TreatmentRepository treatmentRepository) {
        return args -> {
            if (treatmentRepository.count() == 0) {
                treatmentRepository.saveAll(List.of(
                        new Treatment("General Checkup", "Routine dental examination", new BigDecimal("1500.00")),
                        new Treatment("Teeth Cleaning", "Scaling and polishing", new BigDecimal("3500.00")),
                        new Treatment("Dental Filling", "Composite filling for cavities", new BigDecimal("4500.00")),
                        new Treatment("Tooth Extraction", "Simple tooth extraction", new BigDecimal("5000.00")),
                        new Treatment("Root Canal Treatment", "Root canal therapy per tooth", new BigDecimal("15000.00")),
                        new Treatment("Teeth Whitening", "Cosmetic whitening treatment", new BigDecimal("12000.00"))));
            }
        };
    }
}
