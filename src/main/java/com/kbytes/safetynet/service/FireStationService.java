package com.kbytes.safetynet.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kbytes.safetynet.model.FireStation;
import com.kbytes.safetynet.repository.FireStationRepository;

@Service
public class FireStationService {

	FireStationRepository fireStationRepository;

	@Autowired
	public FireStationService(FireStationRepository fireStationRepository) {
		this.fireStationRepository = fireStationRepository;
	}

	public FireStation save(FireStation fireStationReq) {
		return fireStationRepository.save(fireStationReq);
	}

	public FireStation update(String address, FireStation fireStationReq) {
		return fireStationRepository.update(address, fireStationReq);
	}

	public boolean delete(String address) {
		return fireStationRepository.delete(address);
	}

	public List<FireStation> findAll() {
		return fireStationRepository.findAll();
	}

	public FireStation findOne(String address) {
		return fireStationRepository.findOne(address);
	}
}
