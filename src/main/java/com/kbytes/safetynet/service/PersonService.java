package com.kbytes.safetynet.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kbytes.safetynet.model.Person;
import com.kbytes.safetynet.model.dto.ChildAlertDTO;
import com.kbytes.safetynet.model.dto.CoveredFamilyDTO;
import com.kbytes.safetynet.model.dto.PersonCoveredDTO;
import com.kbytes.safetynet.model.dto.PersonFireDTO;
import com.kbytes.safetynet.model.dto.PersonInfoDTO;
import com.kbytes.safetynet.repository.PersonRepository;

@Service
public class PersonService {

	private PersonRepository personRepository;

	@Autowired
	public PersonService(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	/**
	 * 
	 * @param stationNumber
	 * @return
	 */
	public PersonCoveredDTO getPersonByFireStation(int stationNumber) {
		return personRepository.getPersonByFireStation(stationNumber);
	}

	/**
	 * 
	 * @param address
	 * @return a list of child at the given address
	 */
	public ChildAlertDTO getChildByAddress(String address) {
		return personRepository.getChildByAddress(address);
	}

	/**
	 * 
	 * @param address
	 * @return
	 */
	public List<PersonFireDTO> getPersonByAddress(String address) {
		return personRepository.getPersonByAddress(address);
	}

	/**
	 * 
	 * @param stationNumber
	 * @return
	 */
	public List<String> getPhonesByAddress(int stationNumber) {
		return personRepository.getPhonesByAddress(stationNumber);
	}

	/**
	 * 
	 * @param city
	 * @return all the emails at the given city
	 */
	public List<String> getCommunityEmail(String city) {
		return personRepository.getCommunityEmail(city);
	}

	public List<PersonInfoDTO> getPersonByFirstNameAndLastName(String firstName, String lastName) {
		return personRepository.getPersonByFirstNameAndLastName(firstName, lastName);
	}

	public List<CoveredFamilyDTO> getCoveredFamilies(List<Integer> stationsList) {
		return personRepository.getCoveredFamilies(stationsList);
	}

	public List<Person> findAll() {
		return personRepository.findAll();
	}

	/**
	 * This implementation uses Stream API
	 * 
	 * @param personReq
	 * @return null if couldn't save
	 */
	public Person save(Person personReq) {
		return personRepository.save(personReq);
	}

	/**
	 * This implementation uses Stream API
	 * 
	 * @param id
	 * @param personReq
	 * @return null if couldn't update
	 */
	public Person update(String firstName, String lastName, Person personReq) {
		return personRepository.update(firstName, lastName, personReq);
	}

	/**
	 * This implementation uses Stream API
	 * 
	 * @param id
	 */
	public boolean delete(String firstName, String lastName) {
		return personRepository.delete(firstName, lastName);
	}

	public Person findByFirstNameAndLastName(String firstName, String lastName) {
		return personRepository.findByFirstNameAndLastName(firstName, lastName);
	}
}
