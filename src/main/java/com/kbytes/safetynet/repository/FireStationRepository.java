package com.kbytes.safetynet.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kbytes.safetynet.FireStationDataStore;
import com.kbytes.safetynet.model.FireStation;

@Repository
public class FireStationRepository {

	private FireStationDataStore dataStore;

	@Autowired
	public FireStationRepository(FireStationDataStore fireStationDataStore) {
		this.dataStore = fireStationDataStore;
	}

	/**
	 * This implementation uses Stream API
	 * 
	 * @param fireStationReq
	 * @return null if couldn't save
	 */
	public FireStation save(FireStation fireStationReq) {
		boolean state = dataStore.getDataSource().getFirestations().add(fireStationReq);
		return state ? fireStationReq : null;
	}

	/**
	 * This implementation uses Stream API
	 * 
	 * @param address
	 * @param fireStationReq
	 * @return null if couldn't update
	 */
	public FireStation update(String address, FireStation fireStationReq) {
		List<FireStation> deleted = dataStore.getDataSource().getFirestations().stream()
				.filter(v -> !v.getAddress().equals(address)).collect(Collectors.toList());
		fireStationReq.setAddress(address);
		boolean state = deleted.add(fireStationReq);
		dataStore.getDataSource().setFirestations(deleted);
		return state ? fireStationReq : null;
	}

	/**
	 * This implementation uses Stream API
	 * 
	 * @param address
	 */
	public boolean delete(String address) {
		int sizeBefore = dataStore.getDataSource().getFirestations().size();
		List<FireStation> deleted = dataStore.getDataSource().getFirestations().stream()
				.filter(v -> !v.getAddress().equals(address)).collect(Collectors.toList());
		dataStore.getDataSource().setFirestations(deleted);
		int sizeAfter = dataStore.getDataSource().getFirestations().size();
		return sizeBefore > sizeAfter;
	}

	public List<FireStation> findAll() {
		return dataStore.getDataSource().getFirestations();
	}

	/**
	 * 
	 * @param address
	 * @return
	 */
	public FireStation findOne(String address) {

		List<FireStation> results = dataStore.getDataSource().getFirestations().stream()
				.filter(fs -> fs.getAddress().equals(address)).collect(Collectors.toList());

		if (results != null & results.size() > 0)
			return results.get(0);

		return null;
	}
}
