package com.kbytes.safetynet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.kbytes.safetynet.model.Person;
import com.kbytes.safetynet.model.dto.ChildAlertDTO;
import com.kbytes.safetynet.model.dto.CoveredFamilyDTO;
import com.kbytes.safetynet.model.dto.PersonCoveredDTO;
import com.kbytes.safetynet.model.dto.PersonCreateDTO;
import com.kbytes.safetynet.model.dto.PersonFireDTO;
import com.kbytes.safetynet.model.dto.PersonInfoDTO;
import com.kbytes.safetynet.model.dto.PersonUpdateDTO;

class PersonControllerTest extends AbstractTest {

	@BeforeAll
	public void setUp() {
		super.setUp();
	}
	// Test CRUD

	@Test
	public void testCreate() throws Exception {
		String uri = "/persons";
		PersonCreateDTO person = new PersonCreateDTO();
		person.setAddress("Logpom");
		person.setCity("Douala");
		person.setEmail("elkamphy@gmail.com");
		person.setFirstName("Noel");
		person.setLastName("Kamphoa");
		person.setPhone("678989839");
		person.setZip("237");
		String inputJson = super.mapToJson(person);

		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		System.out.println(mvcResult.getResponse().getContentAsString());
		int status = mvcResult.getResponse().getStatus();
		assertEquals(201, status);
		String content = mvcResult.getResponse().getContentAsString();
		assertNotNull(content);
	}

	@Test
	public void testUpdate() throws Exception {
		String uri = "/persons/John,Boyd";
		PersonUpdateDTO person = new PersonUpdateDTO();
		person.setAddress("Logpom");
		person.setCity("Douala");
		person.setEmail("elkamphy@gmail.com");
		person.setPhone("678989839");
		person.setZip("237");
		String inputJson = super.mapToJson(person);

		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.put(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();
		assertNotNull(content);
	}

	@Test
	public void testDelete() throws Exception {
		String uri = "/persons/Jacob,Boyd";

		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();
		assertNotNull(content);
	}

	// Test GET URL
	@Test
	public void testGetAll() throws Exception {
		String uri = "/persons";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();

		Person[] personList = super.mapFromJson(content, Person[].class);
		assertTrue(personList.length > 0);
	}

	@Test
	public void testFireStation() throws Exception {
		String uri = "/firestation";
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.get(uri).param("stationNumber", "1").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();

		PersonCoveredDTO result = super.mapFromJson(content, PersonCoveredDTO.class);
		assertNotNull(result);
	}

	@Test
	public void testChildAlert() throws Exception {
		String uri = "/childAlert";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).param("address", "1509 Culver St")
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();

		ChildAlertDTO result = super.mapFromJson(content, ChildAlertDTO.class);
		assertNotNull(result);
	}

	@Test
	public void testPhoneAlert() throws Exception {
		String uri = "/phoneAlert";
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.get(uri).param("firestation", "1").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();

		String[] results = super.mapFromJson(content, String[].class);
		assertTrue(results.length > 0);
	}

	@Test
	public void testFire() throws Exception {
		String uri = "/fire";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).param("address", "1509 Culver St")
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();

		PersonFireDTO[] results = super.mapFromJson(content, PersonFireDTO[].class);
		assertTrue(results.length > 0);
	}

	@Test
	public void testFlood() throws Exception {
		String uri = "/flood/stations";
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.get(uri).param("stations", "1,2").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();

		CoveredFamilyDTO[] results = super.mapFromJson(content, CoveredFamilyDTO[].class);
		assertTrue(results.length > 0);
	}

	@Test
	public void testPersonInfo() throws Exception {
		String uri = "/personInfo";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).param("lastName", "Boyd")
				.param("firstName", "John").accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();

		PersonInfoDTO[] results = super.mapFromJson(content, PersonInfoDTO[].class);
		assertTrue(results.length > 0);
	}

	@Test
	public void testCommunityEmail() throws Exception {
		String uri = "/communityEmail";
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.get(uri).param("city", "Culver").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();

		String[] results = super.mapFromJson(content, String[].class);
		assertTrue(results.length > 0);
	}

}
