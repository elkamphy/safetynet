package com.kbytes.safetynet.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kbytes.safetynet.model.MedicalRecord;
import com.kbytes.safetynet.repository.MedicalRecordRepository;

@Service
public class MedicalRecordService {
	@Autowired
	private MedicalRecordRepository medicalRecordRepository;

	public List<MedicalRecord> findAll() {
		return medicalRecordRepository.findAll();
	}

	/**
	 * This implementation uses Stream API
	 * 
	 * @param personReq
	 * @return null if couldn't save
	 */
	public MedicalRecord save(MedicalRecord medicalRecordReq) {
		return medicalRecordRepository.save(medicalRecordReq);
	}

	/**
	 * This implementation uses Stream API
	 * 
	 * @param id
	 * @param personReq
	 * @return null if couldn't update
	 */
	public MedicalRecord update(String firstName, String lastName, MedicalRecord medicalRecordReq) {
		return medicalRecordRepository.update(firstName, lastName, medicalRecordReq);
	}

	/**
	 * This implementation uses Stream API
	 * 
	 * @param id
	 */
	public boolean delete(String firstName, String lastName) {
		return medicalRecordRepository.delete(firstName, lastName);
	}

	public MedicalRecordRepository getMedicalRecordRepository() {
		return medicalRecordRepository;
	}

	public void setMedicalRecordRepository(MedicalRecordRepository medicalRecordRepository) {
		this.medicalRecordRepository = medicalRecordRepository;
	}

	public MedicalRecord findOne(String firstName, String lastName) {
		return medicalRecordRepository.findOne(firstName, lastName);
	}
}
