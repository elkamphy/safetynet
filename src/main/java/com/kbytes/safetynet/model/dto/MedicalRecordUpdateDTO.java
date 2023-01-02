package com.kbytes.safetynet.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MedicalRecordUpdateDTO {
	private String birthdate;
	private String[] medications;
	private String[] allergies;
}
