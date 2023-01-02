package com.kbytes.safetynet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MedicalRecord {
	private String firstName;
	private String lastName;
	private String birthdate;
	private String[] medications;
	private String[] allergies;
	@JsonIgnore
	private String _links;
	@JsonIgnore
	private String links;

}
