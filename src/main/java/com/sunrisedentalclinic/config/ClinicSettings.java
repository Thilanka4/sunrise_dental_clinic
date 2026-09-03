package com.sunrisedentalclinic.config;

import java.math.BigDecimal;

/**
 * Clinic-wide constants (name, standard consultation fee) shared by every billing
 * calculation. Implemented as a classic double-checked-locking Singleton, deliberately
 * outside the Spring container, so a single canonical instance exists regardless of
 * how it is looked up.
 */
public final class ClinicSettings {

    private static volatile ClinicSettings instance;

    private final String clinicName;
    private final BigDecimal standardConsultationFee;

    private ClinicSettings() {
        this.clinicName = "Sunrise Dental Clinic";
        this.standardConsultationFee = new BigDecimal("1000.00");
    }

    public static ClinicSettings getInstance() {
        ClinicSettings result = instance;
        if (result == null) {
            synchronized (ClinicSettings.class) {
                result = instance;
                if (result == null) {
                    instance = result = new ClinicSettings();
                }
            }
        }
        return result;
    }

    public String getClinicName() {
        return clinicName;
    }

    public BigDecimal getStandardConsultationFee() {
        return standardConsultationFee;
    }
}
