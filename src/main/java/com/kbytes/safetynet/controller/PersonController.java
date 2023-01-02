package com.kbytes.safetynet.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.kbytes.safetynet.exceptions.PersonNotFoundException;
import com.kbytes.safetynet.model.Person;
import com.kbytes.safetynet.model.dto.ChildAlertDTO;
import com.kbytes.safetynet.model.dto.CoveredFamilyDTO;
import com.kbytes.safetynet.model.dto.PersonCoveredDTO;
import com.kbytes.safetynet.model.dto.PersonCreateDTO;
import com.kbytes.safetynet.model.dto.PersonFireDTO;
import com.kbytes.safetynet.model.dto.PersonInfoDTO;
import com.kbytes.safetynet.model.dto.PersonUpdateDTO;
import com.kbytes.safetynet.service.PersonService;

@RestController
public class PersonController {
	@Autowired
	PersonService personService;

	@Autowired
	ModelMapper mapper;

	// Endpoints
	@PostMapping("/persons")
	public ResponseEntity<PersonCreateDTO> createPerson(@RequestBody PersonCreateDTO personCreateDTO) throws Exception {

		List<PersonInfoDTO> list = personService.getPersonByFirstNameAndLastName(personCreateDTO.getFirstName(),
				personCreateDTO.getLastName());
		System.out.println("Returned list : " + list);
		if (list != null && list.size() > 0) {
			System.out.println("Returned list size : " + list.size());
			throw new Exception("That person already exists");
		}

		Person personReq = mapper.map(personCreateDTO, Person.class);

		Person person = personService.save(personReq);

		PersonCreateDTO personRes = mapper.map(person, PersonCreateDTO.class);

		String name = personRes.getFirstName() + "," + personReq.getLastName();
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{name}").buildAndExpand(name).toUri();

		return ResponseEntity.created(location).build();
	}

	@GetMapping("/persons/{name}")
	public ResponseEntity<EntityModel<Person>> getPerson(@PathVariable String name) throws Exception {

		String[] names = name.split(",");
		if (names.length != 2)
			throw new PersonNotFoundException("Bad parameters : " + name);

		Person person = personService.findByFirstNameAndLastName(names[0], names[1]);
		if (person == null)
			throw new PersonNotFoundException("Person not found : " + name);

		Link linkTo = WebMvcLinkBuilder.linkTo(methodOn(this.getClass()).findAll()).withRel("all-persons");
		EntityModel<Person> resource = EntityModel.of(person);

		resource.add(linkTo);

		return new ResponseEntity<>(resource, HttpStatus.OK);
	}

	/**
	 * 
	 * @param name           is lastName + firstName (no space)
	 * @param fireStationDTO
	 * @return
	 * @throws Exception
	 */
	@PutMapping("/persons/{name}")
	public ResponseEntity<PersonUpdateDTO> updatePerson(@PathVariable String name,
			@RequestBody PersonUpdateDTO personUpdateDTO) throws Exception {
		String[] names = name.split(",");
		if (names.length != 2)
			throw new PersonNotFoundException("Bad parameters : " + name);

		Person personReq = mapper.map(personUpdateDTO, Person.class);

		Person person = personService.update(names[0], names[1], personReq);

		PersonUpdateDTO personRes = mapper.map(person, PersonUpdateDTO.class);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{name}").buildAndExpand(name).toUri();

		return new ResponseEntity<PersonUpdateDTO>(personRes, HttpStatus.OK);
	}

	/**
	 * 
	 * @param name is lastName + firstName (no space)
	 * @return
	 */
	@DeleteMapping("/persons/{name}")
	public ResponseEntity<String> deletePerson(@PathVariable String name) {
		String[] names = name.split(",");
		if (names.length != 2)
			throw new PersonNotFoundException("Bad parameters : " + name);

		boolean status = personService.delete(names[0], names[1]);

		if (status) {
			return new ResponseEntity<String>("Deletion complete!", HttpStatus.OK);
		}

		return new ResponseEntity<String>("Something went wrong!", HttpStatus.OK);

	}

	// URL Part is below
	@GetMapping("/firestation")
	public PersonCoveredDTO getPersonByFireStation(@RequestParam int stationNumber) {

		return personService.getPersonByFireStation(stationNumber);
	}

	@GetMapping("/childAlert")
	public ChildAlertDTO getChildByAddress(@RequestParam String address) {

		return personService.getChildByAddress(address);
	}

	@GetMapping("/fire")
	public List<PersonFireDTO> getPersonByAddress(@RequestParam String address) {
		return personService.getPersonByAddress(address);
	}

	@GetMapping("/phoneAlert")
	public List<String> getPhonesByAddress(@RequestParam int firestation) {
		return personService.getPhonesByAddress(firestation);
	}

	@GetMapping("/communityEmail")
	public List<String> getCommunityEmail(@RequestParam String city) {
		return personService.getCommunityEmail(city);
	}

	@GetMapping("/personInfo")
	public List<PersonInfoDTO> getPersonByFirstNameAndLastName(@RequestParam String firstName,
			@RequestParam String lastName) {

		return personService.getPersonByFirstNameAndLastName(firstName, lastName);
	}

	@GetMapping("/flood/stations")
	public List<CoveredFamilyDTO> getPersonByFirstNameAndLastName(@RequestParam List<Integer> stations) {

		return personService.getCoveredFamilies(stations);
	}

	@GetMapping("/persons")
	public ResponseEntity<List<EntityModel<Person>>> findAll() throws Exception {
		List<Person> results = personService.findAll();
		List<EntityModel<Person>> resourcesList = new ArrayList<>();
		for (Person person : results) {
			String name = person.getFirstName() + "," + person.getLastName();
			Link linkTo = WebMvcLinkBuilder.linkTo(methodOn(this.getClass()).getPerson(name)).withSelfRel();
			EntityModel<Person> resource = EntityModel.of(person);
			resource.add(linkTo);
			resourcesList.add(resource);
		}

		return new ResponseEntity<>(resourcesList, HttpStatus.OK);
	}
}
