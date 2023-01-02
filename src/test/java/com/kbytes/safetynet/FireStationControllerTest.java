package com.kbytes.safetynet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.kbytes.safetynet.model.FireStation;
import com.kbytes.safetynet.model.dto.FireStationCreateDTO;
import com.kbytes.safetynet.model.dto.FireStationUpdateDTO;

public class FireStationControllerTest extends AbstractTest {
	@BeforeAll
	public void setUp() {
		super.setUp();
	}

	@Test
	public void testCreate() throws Exception {
		String uri = "/firestations";
		FireStationCreateDTO fireStation = new FireStationCreateDTO();
		fireStation.setAddress("Logpom");
		fireStation.setStation(10);

		String inputJson = super.mapToJson(fireStation);

		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(201, status);
		String content = mvcResult.getResponse().getContentAsString();
		assertNotNull(content);
	}

	@Test
	public void testUpdate() throws Exception {
		String uri = "/firestations/748 Townings Dr";
		FireStationUpdateDTO fireStation = new FireStationUpdateDTO();
		fireStation.setStation(11);

		String inputJson = super.mapToJson(fireStation);

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
		String uri = "/firestations/951 LoneTree Rd";

		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();
		assertNotNull(content);
	}

	@Test
	public void testGetAll() throws Exception {
		String uri = "/firestations";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();

		FireStation[] fireStationList = super.mapFromJson(content, FireStation[].class);
		assertTrue(fireStationList.length > 0);
	}
}
