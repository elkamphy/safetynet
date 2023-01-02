package com.kbytes.safetynet.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kbytes.safetynet.FireStationDataStore;
import com.kbytes.safetynet.model.FireStation;
import com.kbytes.safetynet.model.MedicalRecord;
import com.kbytes.safetynet.model.Person;
import com.kbytes.safetynet.model.dto.ChildAlertDTO;
import com.kbytes.safetynet.model.dto.CoveredFamilyDTO;
import com.kbytes.safetynet.model.dto.PersonCoveredDTO;
import com.kbytes.safetynet.model.dto.PersonDTO;
import com.kbytes.safetynet.model.dto.PersonFireDTO;
import com.kbytes.safetynet.model.dto.PersonInfoDTO;
import com.kbytes.safetynet.utils.SafetynetHelper;

@Repository
public class PersonRepository {

	private FireStationDataStore dataStore;

	@Autowired
	ModelMapper mapper;

	@Autowired
	public PersonRepository(FireStationDataStore fireStationDataStore) {
		this.dataStore = fireStationDataStore;
	}

	/**
	 * 
	 * @param stationsList
	 * @return
	 */
	public List<CoveredFamilyDTO> getCoveredFamilies(List<Integer> stations) {
		// Add a converter to calculate the age
		Converter<String, Integer> calculateAge = getAgeConverter();
		if (mapper.getTypeMap(MedicalRecord.class, PersonInfoDTO.class) == null)
			mapper.createTypeMap(MedicalRecord.class, PersonInfoDTO.class)
					.addMappings(mapper -> mapper.using(calculateAge)
							// Now define the mapping birthdate to age
							.map(MedicalRecord::getBirthdate, PersonInfoDTO::setAge));

		List<CoveredFamilyDTO> returnValue = new ArrayList<>();

		for (int station : stations) {
			List<String> addresses = dataStore.getDataSource().getFirestations().stream()
					.filter(stat -> stat.getStation() == station).map(stat -> stat.getAddress())
					.collect(Collectors.toList());
			for (String address : addresses) {
				// Find medical record list for the given address
				List<MedicalRecord> medicalRecords = dataStore.getDataSource().getMedicalrecords().stream().filter(
						mr -> dataStore.getDataSource().getPersons().stream().anyMatch(isPersonAtAddress(address, mr)))
						.collect(Collectors.toList());

				// Populate fields
				List<PersonInfoDTO> personsDTO = medicalRecords.stream().map(mr -> mapper.map(mr, PersonInfoDTO.class))
						.collect(Collectors.toList());
				Function<Person, String> nameFunction = person -> person.getFirstName().concat(person.getLastName());
				Map<String, Person> mapPersons = dataStore.getDataSource().getPersons().stream()
						.collect(Collectors.toMap(nameFunction, Function.identity()));
				Consumer<PersonDTO> copyFields = personDTO -> copyFields(mapPersons, personDTO);
				personsDTO.forEach(copyFields);
				CoveredFamilyDTO returnDTO = new CoveredFamilyDTO();
				returnDTO.setAddress(address);
				returnDTO.setMembers(personsDTO);
				returnValue.add(returnDTO);
			}
		}
		return returnValue;
	}

	/**
	 * This implementation uses Stream API
	 * 
	 * @param stationNumber
	 * @return filtered List
	 */
	public PersonCoveredDTO getPersonByFireStation(int stationNumber) {
		// Returns a Map of firestation and list of addresses covered
		// The Map is filtered on the station number. So normally should return a single
		// map element
		Map<Integer, List<String>> covered = dataStore.getDataSource().getFirestations().stream()
				.filter(s -> s.getStation() == stationNumber)
				.collect(Collectors.toMap(FireStation::getStation, item -> {
					List<String> list = new ArrayList<>();
					list.add(item.getAddress());
					return list;
				}, (list1, list2) -> {
					list1.addAll(list2);
					return list1;
				}, HashMap::new));

		// The list has at most most one element
		List<String> coveredAddress = covered.get(stationNumber);

		// We use anyMath to look for people living at the given addresses
		List<Person> persons = dataStore.getDataSource().getPersons().stream()
				.filter(pers -> coveredAddress.stream().anyMatch(address -> address.equals(pers.getAddress())))
				.collect(Collectors.toList());

		Function<Person, String> nameFunction = person -> person.getFirstName().concat(person.getLastName());

		Map<String, Person> mapPersons = persons.stream().collect(Collectors.toMap(nameFunction, Function.identity()));
		// Add a converter to calculate the age
		Converter<String, Integer> calculateAge = getAgeConverter();
		if (mapper.getTypeMap(MedicalRecord.class, PersonDTO.class) == null)
			mapper.createTypeMap(MedicalRecord.class, PersonDTO.class).addMappings(mapper -> mapper.using(calculateAge)
					// Now define the mapping birthdate to age
					.map(MedicalRecord::getBirthdate, PersonDTO::setAge));
		// list of person informations with age
		Consumer<PersonDTO> copyFields = personDTO -> copyFields(mapPersons, personDTO);
		List<PersonDTO> personDTO = dataStore.getDataSource().getMedicalrecords().stream()
				.filter(mr -> persons.stream().anyMatch(isSamePerson(mr)))
				.map(medicalRecord -> mapper.map(medicalRecord, PersonDTO.class)).collect(Collectors.toList());
		personDTO.forEach(copyFields);
		// Partition the stream in two lists children and adults
		Predicate<PersonDTO> isChildPredicate = childPredicate();
		Map<Boolean, List<PersonDTO>> map = personDTO.stream().collect(Collectors.partitioningBy(isChildPredicate));
		Integer adultCount = map.getOrDefault(Boolean.FALSE, new ArrayList<>()).size();
		Integer childCount = map.getOrDefault(Boolean.TRUE, new ArrayList<>()).size();

		// Create the object to be returned
		PersonCoveredDTO returnDTO = new PersonCoveredDTO();
		returnDTO.setPersons(personDTO);
		returnDTO.setAdultCount(adultCount);
		returnDTO.setChildCount(childCount);

		return returnDTO;

	}

	private void copyFields(Map<String, Person> persons, PersonDTO personDTO) {
		String key = personDTO.getFirstName().concat(personDTO.getLastName());
		Person person = persons.get(key);
		personDTO.setAddress(person.getAddress());
		personDTO.setPhone(person.getPhone());
	}

	/**
	 * 
	 * @param address
	 * @return a list of child at the given address
	 */
	public ChildAlertDTO getChildByAddress(String address) {

		// Add a converter to calculate the age
		Converter<String, Integer> calculateAge = getAgeConverter();
		if (mapper.getTypeMap(MedicalRecord.class, PersonDTO.class) == null)
			mapper.createTypeMap(MedicalRecord.class, PersonDTO.class).addMappings(mapper -> mapper.using(calculateAge)
					// Now define the mapping birthdate to age
					.map(MedicalRecord::getBirthdate, PersonDTO::setAge));

		// Find medical record list for the given address
		List<MedicalRecord> medicalRecords = dataStore.getDataSource().getMedicalrecords().stream()
				.filter(mr -> dataStore.getDataSource().getPersons().stream().anyMatch(isPersonAtAddress(address, mr)))
				.collect(Collectors.toList());

		// Populate fields
		List<PersonDTO> personsDTO = medicalRecords.stream().map(mr -> mapper.map(mr, PersonDTO.class))
				.collect(Collectors.toList());
		Function<Person, String> nameFunction = person -> person.getFirstName().concat(person.getLastName());
		Map<String, Person> mapPersons = dataStore.getDataSource().getPersons().stream()
				.collect(Collectors.toMap(nameFunction, Function.identity()));
		Consumer<PersonDTO> copyFields = personDTO -> copyFields(mapPersons, personDTO);
		personsDTO.forEach(copyFields);
		// Partition the stream in two list children and adults
		Predicate<PersonDTO> isChildPredicate = childPredicate();
		Map<Boolean, List<PersonDTO>> map = personsDTO.stream().collect(Collectors.partitioningBy(isChildPredicate));

		// Create the response
		ChildAlertDTO dto = new ChildAlertDTO();
		dto.setChildren(map.get(Boolean.TRUE));
		dto.setFamilyMembers(map.get(Boolean.FALSE));

		return dto;

	}

	private Predicate<PersonDTO> childPredicate() {
		return p -> p.getAge() <= SafetynetHelper.CHILD_MAX_AGE;
	}

	/**
	 * 
	 * @param address
	 * @return
	 */
	public List<PersonFireDTO> getPersonByAddress(String address) {

		// Add a converter to calculate the age
		Converter<String, Integer> calculateAge = getAgeConverter();
		if (mapper.getTypeMap(MedicalRecord.class, PersonFireDTO.class) == null)
			mapper.createTypeMap(MedicalRecord.class, PersonFireDTO.class)
					.addMappings(mapper -> mapper.using(calculateAge)
							// Now define the mapping birthdate to age
							.map(MedicalRecord::getBirthdate, PersonFireDTO::setAge));
		// Find Firestation at given address
		// Should return a list of maximum 1 element
		List<FireStation> fireStationAtAddress = dataStore.getDataSource().getFirestations().stream()
				.filter(fireStation -> fireStation.getAddress().equals(address)).collect(Collectors.toList());

		// Find medical records list for the given address
		List<PersonFireDTO> personFireDTO = dataStore.getDataSource().getMedicalrecords().stream()
				.filter(mr -> dataStore.getDataSource().getPersons().stream().anyMatch(isPersonAtAddress(address, mr)))
				.map(medicalRecord -> mapper.map(medicalRecord, PersonFireDTO.class)).collect(Collectors.toList());

		if (personFireDTO == null || fireStationAtAddress == null)
			return null;

		// Populate the stationNumber in the previous list
		Consumer<PersonFireDTO> populateStationNumber = dto -> dto
				.setStationNumber(fireStationAtAddress.get(0).getStation());

		personFireDTO.forEach(populateStationNumber);

		Function<Person, String> nameFunction = person -> person.getFirstName().concat(person.getLastName());
		Map<String, Person> mapPersons = dataStore.getDataSource().getPersons().stream()
				.collect(Collectors.toMap(nameFunction, Function.identity()));
		Consumer<PersonDTO> copyFields = personDTO -> copyFields(mapPersons, personDTO);
		personFireDTO.forEach(copyFields);

		return personFireDTO;

	}

	/**
	 * 
	 * @param firstName
	 * @param lastName
	 * @return
	 */
	public List<PersonInfoDTO> getPersonByFirstNameAndLastName(String firstName, String lastName) {

		// Add a converter to calculate the age
		Converter<String, Integer> calculateAge = getAgeConverter();
		if (mapper.getTypeMap(MedicalRecord.class, PersonInfoDTO.class) == null)
			mapper.createTypeMap(MedicalRecord.class, PersonInfoDTO.class)
					.addMappings(mapper -> mapper.using(calculateAge)
							// Now define the mapping birthdate to age
							.map(MedicalRecord::getBirthdate, PersonInfoDTO::setAge));
		// Find Firestation at given address
		// Should return a list of maximum 1 element
		List<MedicalRecord> testMedicalRecord = dataStore.getDataSource().getMedicalrecords().stream()
				.filter(medicalRecord -> medicalRecord.getFirstName().equals(firstName)
						&& medicalRecord.getLastName().equals(lastName))
				.collect(Collectors.toList());

		for (MedicalRecord record : testMedicalRecord) {
			System.out.println("Medical Record : " + record);
		}

		List<PersonInfoDTO> personsInfo = dataStore.getDataSource().getMedicalrecords().stream()
				.filter(medicalRecord -> medicalRecord.getFirstName().equals(firstName)
						&& medicalRecord.getLastName().equals(lastName))
				.map(medicalRecord -> mapper.map(medicalRecord, PersonInfoDTO.class)).collect(Collectors.toList());

		for (PersonInfoDTO record : personsInfo) {
			System.out.println("Person Info : " + record);
		}

		if (personsInfo != null && personsInfo.size() == 0)
			return null;

		Function<Person, String> nameFunction = person -> person.getFirstName().concat(person.getLastName());
		Map<String, Person> mapPersons = dataStore.getDataSource().getPersons().stream()
				.collect(Collectors.toMap(nameFunction, Function.identity()));
		Consumer<PersonDTO> copyFields = personDTO -> copyFields(mapPersons, personDTO);

		personsInfo.forEach(copyFields);

		return personsInfo;

	}

	private Converter<String, Integer> getAgeConverter() {
		// Create a converter to map the birthday field in medicalRecord to age field in
		// persondto
		Converter<String, Integer> calculateAge = ctx -> ctx.getSource() == null ? 0
				: SafetynetHelper.calcBirthdate((String) ctx.getSource());

		return calculateAge;
	}

	private Predicate<Person> isPersonAtAddress(String address, MedicalRecord medicalRecord) {
		Predicate<Person> sameAddress = p -> (p.getAddress().equals(address));
		Predicate<Person> sameName = isSamePerson(medicalRecord);
		return sameAddress.and(sameName);
	}

	private Predicate<Person> isSamePerson(MedicalRecord medicalRecord) {
		return p -> p.getFirstName().equals(medicalRecord.getFirstName())
				&& p.getLastName().equals(medicalRecord.getLastName());
	}

	/**
	 * 
	 * @param stationNumber
	 * @return all the phone numbers at the given address
	 */
	public List<String> getPhonesByAddress(int stationNumber) {
		// Returns a Map of firestation and list of addresses covered
		// The Map is filtered on the station number. So normally should return a single
		// map element
		Map<Integer, List<String>> covered = dataStore.getDataSource().getFirestations().stream()
				.filter(s -> s.getStation() == stationNumber)
				.collect(Collectors.toMap(FireStation::getStation, item -> {
					List<String> list = new ArrayList<>();
					list.add(item.getAddress());
					return list;
				}, (list1, list2) -> {
					list1.addAll(list2);
					return list1;
				}, HashMap::new));

		// The list has at most most one element
		List<String> coveredAddress = covered.get(stationNumber);

		List<String> phones = dataStore.getDataSource().getPersons().stream()
				.filter(pers -> coveredAddress.stream().anyMatch(address -> address.equals(pers.getAddress())))
				.map(p -> p.getPhone()).collect(Collectors.toList());

		return phones;
	}

	public List<Person> findAll() {
		return dataStore.getDataSource().getPersons();
	}

	/**
	 * 
	 * @param city
	 * @return all the emails at the given city
	 */
	public List<String> getCommunityEmail(String city) {

		List<String> emails = dataStore.getDataSource().getPersons().stream().filter(p -> p.getCity().equals(city))
				.map(p -> p.getEmail()).collect(Collectors.toList());

		return emails;
	}

	/**
	 * This implementation uses Stream API
	 * 
	 * @param personReq
	 * @return null if couldn't save
	 */
	public Person save(Person personReq) {
		boolean state = dataStore.getDataSource().getPersons().add(personReq);
		return state ? personReq : null;
	}

	/**
	 * This implementation uses Stream API
	 * 
	 * @param firstName
	 * @param lastName
	 * @param personReq
	 * @return
	 */
	public Person update(String firstName, String lastName, Person personReq) {
		List<Person> deleted = dataStore.getDataSource().getPersons().stream()
				.filter(v -> !(v.getFirstName().equals(firstName) && v.getLastName().equals(lastName)))
				.collect(Collectors.toList());
		personReq.setFirstName(firstName);
		personReq.setLastName(lastName);
		boolean state = deleted.add(personReq);
		dataStore.getDataSource().setPersons(deleted);
		return state ? personReq : null;
	}

	/**
	 * This implementation uses Stream API
	 * 
	 * @param firstName
	 * @param lastName
	 * @return
	 */
	public boolean delete(String firstName, String lastName) {
		int sizeBefore = dataStore.getDataSource().getPersons().size();
		List<Person> deleted = dataStore.getDataSource().getPersons().stream()
				.filter(v -> !(v.getFirstName().equals(firstName) && v.getLastName().equals(lastName)))
				.collect(Collectors.toList());
		dataStore.getDataSource().setPersons(deleted);
		int sizeAfter = dataStore.getDataSource().getPersons().size();
		return sizeBefore > sizeAfter;
	}

	/**
	 * 
	 * @param firstName
	 * @param lastName
	 * @return
	 */
	public Person findByFirstNameAndLastName(String firstName, String lastName) {
		Function<Person, String> nameFunction = person -> person.getFirstName().concat(person.getLastName());
		Map<String, Person> mapPersons = dataStore.getDataSource().getPersons().stream()
				.collect(Collectors.toMap(nameFunction, Function.identity()));

		return mapPersons.get(firstName + lastName);
	}

}
