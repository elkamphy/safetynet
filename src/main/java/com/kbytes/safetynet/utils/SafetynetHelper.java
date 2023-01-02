package com.kbytes.safetynet.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class SafetynetHelper {
	public static int CHILD_MAX_AGE = 18;
    // This method should be used for mapping. In real, this could be a service call
    public static int calcBirthdate(String birthdate) {
        // create a formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    	LocalDate birth = LocalDate.parse(birthdate, formatter);
        return (int)ChronoUnit.YEARS.between(birth, LocalDate.now());
    }
}
