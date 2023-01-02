package com.kbytes.safetynet.repository;

import java.util.List;

import com.kbytes.safetynet.model.FireStation;
import com.kbytes.safetynet.model.MedicalRecord;
import com.kbytes.safetynet.model.Person;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SafetynetDataSource {
	
	List<Person> persons;
	
	List<FireStation> firestations;
	
	List<MedicalRecord> medicalrecords;

}
