package com.kbytes.safetynet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.kbytes.safetynet.model.MedicalRecord;
import com.kbytes.safetynet.model.dto.MedicalRecordCreateDTO;
import com.kbytes.safetynet.model.dto.MedicalRecordUpdateDTO;

public class MedicalRecordControllerTest extends AbstractTest {

	@BeforeAll
	public void setUp() {
		super.setUp();
	}

	// Test CRUD
	@Test
	public void testCreate() throws Exception {
		String uri = "/medicalRecords";
		MedicalRecordCreateDTO medicalRecord = new MedicalRecordCreateDTO();
		medicalRecord.setFirstName("Noel");
		medicalRecord.setLastName("Kamphy");
		medicalRecord.setBirthdate("01/01/2018");
		medicalRecord.setAllergies(new String[] { "Nothing specific" });
		medicalRecord.setMedications(new String[] { "Para" });

		String inputJson = super.mapToJson(medicalRecord);

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
		String uri = "/medicalRecords/John,Boyd";
		MedicalRecordUpdateDTO medicalRecord = new MedicalRecordUpdateDTO();
		medicalRecord.setBirthdate("01/01/2019");
		medicalRecord.setAllergies(new String[] { "Nothing specific bla bla" });
		medicalRecord.setMedications(new String[] { "Paracetamol" });

		String inputJson = super.mapToJson(medicalRecord);

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
		String uri = "/medicalRecords/Jacob,Boyd";

		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)).andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();
		assertNotNull(content);
	}

	@Test
	public void testGetAll() throws Exception {
		String uri = "/medicalRecords";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		String content = mvcResult.getResponse().getContentAsString();

		MedicalRecord[] medicalRecordList = super.mapFromJson(content, MedicalRecord[].class);
		assertTrue(medicalRecordList.length > 0);
	}
}
