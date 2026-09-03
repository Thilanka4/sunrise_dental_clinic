package com.sunrisedentalclinic.service;

import com.sunrisedentalclinic.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentNumberGeneratorTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Test
    void next_seedsFromCurrentRowCountAndIncrements() {
        when(appointmentRepository.count()).thenReturn(5L);

        AppointmentNumberGenerator generator = new AppointmentNumberGenerator(appointmentRepository);

        assertThat(generator.next()).isEqualTo("APT000006");
        assertThat(generator.next()).isEqualTo("APT000007");
    }

    @Test
    void next_emptyTable_startsAtOne() {
        when(appointmentRepository.count()).thenReturn(0L);

        AppointmentNumberGenerator generator = new AppointmentNumberGenerator(appointmentRepository);

        assertThat(generator.next()).isEqualTo("APT000001");
    }
}
