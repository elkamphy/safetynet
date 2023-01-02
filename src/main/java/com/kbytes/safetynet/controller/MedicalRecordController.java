package com.kbytes.safetynet.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
import org.springframework.web.bind.annotation.RestController;

import com.kbytes.safetynet.model.MedicalRecord;
import com.kbytes.safetynet.model.dto.MedicalRecordCreateDTO;
import com.kbytes.safetynet.model.dto.MedicalRecordUpdateDTO;
import com.kbytes.safetynet.service.MedicalRecordService;

@RestController
public class MedicalRecordController {

	@Autowired
	MedicalRecordService medicalRecordService;

	@Autowired
	ModelMapper mapper;

	@PostMapping("/medicalRecords")
	public ResponseEntity<MedicalRecordCreateDTO> createMedicalRecord(
			@RequestBody MedicalRecordCreateDTO medicalRecordCreateDTO) {

		MedicalRecord medicalRecordReq = mapper.map(medicalRecordCreateDTO, MedicalRecord.class);

		MedicalRecord medicalRecord = medicalRecordService.save(medicalRecordReq);

		MedicalRecordCreateDTO medicalRecordRes = mapper.map(medicalRecord, MedicalRecordCreateDTO.class);

		return new ResponseEntity<MedicalRecordCreateDTO>(medicalRecordRes, HttpStatus.CREATED);
	}

	@PutMapping("/medicalRecords/{name}")
	public ResponseEntity<MedicalRecordUpdateDTO> updateMedicalRecord(@PathVariable String name,
			@RequestBody MedicalRecordUpdateDTO medicalRecordUpdateDTO) {
		String[] names = name.split(",");
		if (names.length != 2)
			return new ResponseEntity<MedicalRecordUpdateDTO>(medicalRecordUpdateDTO, HttpStatus.BAD_REQUEST);

		MedicalRecord medicalRecordReq = mapper.map(medicalRecordUpdateDTO, MedicalRecord.class);

		MedicalRecord medicalRecord = medicalRecordService.update(names[0], names[1], medicalRecordReq);

		MedicalRecordUpdateDTO medicalRecordRes = mapper.map(medicalRecord, MedicalRecordUpdateDTO.class);

		return new ResponseEntity<MedicalRecordUpdateDTO>(medicalRecordRes, HttpStatus.OK);
	}

	@DeleteMapping("/medicalRecords/{name}")
	public ResponseEntity<String> deleteMedicalRecord(@PathVariable String name) {
		String[] names = name.split(",");
		if (names.length != 2)
			return new ResponseEntity<String>("Bad parameters", HttpStatus.BAD_REQUEST);

		boolean status = medicalRecordService.delete(names[0], names[1]);

		if (status)
			return new ResponseEntity<String>("Deletion complete !", HttpStatus.OK);

		return new ResponseEntity<String>("Something went wrong !", HttpStatus.OK);
	}

	@GetMapping("medicalRecords")
	public ResponseEntity<List<MedicalRecord>> findAll() {
		List<MedicalRecord> results = medicalRecordService.findAll();
		return new ResponseEntity<List<MedicalRecord>>(results, HttpStatus.OK);
	}

	@GetMapping("/medicalRecords/{name}")
	public ResponseEntity<EntityModel<MedicalRecord>> findOne(@PathVariable String name) throws Exception {
		String[] names = name.split(",");
		if (names.length != 2)
			throw new Exception("Bad parameters");

		MedicalRecord medicalRecord = medicalRecordService.findOne(names[0], names[1]);

		Link linkTo = WebMvcLinkBuilder.linkTo(methodOn(this.getClass()).findAll()).withRel("all-medicalRecords");

		EntityModel<MedicalRecord> resource = EntityModel.of(medicalRecord);

		resource.add(linkTo);

		return new ResponseEntity<EntityModel<MedicalRecord>>(resource, HttpStatus.OK);
	}
}
