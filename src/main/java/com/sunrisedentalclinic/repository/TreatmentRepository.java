package com.sunrisedentalclinic.repository;

import com.sunrisedentalclinic.model.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreatmentRepository extends JpaRepository<Treatment, Long> {
}
