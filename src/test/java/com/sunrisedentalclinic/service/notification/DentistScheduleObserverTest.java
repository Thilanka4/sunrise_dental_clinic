package com.sunrisedentalclinic.service.notification;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.sunrisedentalclinic.model.Appointment;
import com.sunrisedentalclinic.model.Patient;
import com.sunrisedentalclinic.model.Treatment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DentistScheduleObserverTest {

    private ListAppender<ILoggingEvent> logAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(DentistScheduleObserver.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(logAppender);
    }

    @Test
    void onAppointmentBooked_logsDentistName() {
        Treatment treatment = new Treatment("General Checkup", "desc", new BigDecimal("1500.00"));
        Patient patient = new Patient("Nimal Perera", "12 Lake Road", "0771234567");
        Appointment appointment = new Appointment("APT000001", patient, treatment, "Silva",
                LocalDateTime.now().plusDays(1));

        new DentistScheduleObserver().onAppointmentBooked(appointment);

        assertThat(logAppender.list).hasSize(1);
        assertThat(logAppender.list.get(0).getFormattedMessage()).contains("Silva");
    }
}
