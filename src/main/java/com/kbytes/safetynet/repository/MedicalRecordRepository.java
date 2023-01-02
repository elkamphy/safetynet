package com.kbytes.safetynet.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kbytes.safetynet.FireStationDataStore;
import com.kbytes.safetynet.model.MedicalRecord;

@Repository
public class MedicalRecordRepository {

	private FireStationDataStore dataStore;

	@Autowired
	ModelMapper mapper;

	@Autowired
	public MedicalRecordRepository(FireStationDataStore fireStationDataStore) {
		this.dataStore = fireStationDataStore;
	}

	/**
	 * 
	 * @param medicalRecordReq
	 * @return
	 */
	public MedicalRecord save(MedicalRecord medicalRecordReq) {
		boolean state = dataStore.getDataSource().getMedicalrecords().add(medicalRecordReq);
		return state ? medicalRecordReq : null;
	}

	/**
	 * 
	 * @param firstName
	 * @param lastName
	 * @param medicalRecordReq
	 * @return
	 */
	public MedicalRecord update(String firstName, String lastName, MedicalRecord medicalRecordReq) {
		List<MedicalRecord> deleted = dataStore.getDataSource().getMedicalrecords().stream()
				.filter(v -> !(v.getFirstName().equals(firstName) && v.getLastName().equals(lastName)))
				.collect(Collectors.toList());
		medicalRecordReq.setFirstName(firstName);
		medicalRecordReq.setLastName(lastName);
		boolean state = deleted.add(medicalRecordReq);
		dataStore.getDataSource().setMedicalrecords(deleted);
		return state ? medicalRecordReq : null;
	}

	/**
	 * This implementation uses Stream API
	 * 
	 * @param firstName
	 * @param lastName
	 * @return the status of the operation
	 */
	public boolean delete(String firstName, String lastName) {
		int sizeBefore = dataStore.getDataSource().getMedicalrecords().size();
		List<MedicalRecord> deleted = dataStore.getDataSource().getMedicalrecords().stream()
				.filter(v -> !(v.getFirstName().equals(firstName) && v.getLastName().equals(lastName)))
				.collect(Collectors.toList());
		dataStore.getDataSource().setMedicalrecords(deleted);
		int sizeAfter = dataStore.getDataSource().getMedicalrecords().size();

		return sizeBefore > sizeAfter;
	}

	public List<MedicalRecord> findAll() {
		return dataStore.getDataSource().getMedicalrecords();
	}

	/**
	 * 
	 * @param firstName
	 * @param lastName
	 * @return
	 */
	public MedicalRecord findOne(String firstName, String lastName) {
		List<MedicalRecord> results = dataStore.getDataSource().getMedicalrecords().stream()
				.filter(mr -> mr.getFirstName().equals(firstName) && mr.getLastName().equals(lastName))
				.collect(Collectors.toList());
		if (results != null && results.size() > 0)
			return results.get(0);

		return null;
	}
}
