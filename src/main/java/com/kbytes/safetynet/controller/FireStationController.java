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

import com.kbytes.safetynet.model.FireStation;
import com.kbytes.safetynet.model.dto.FireStationCreateDTO;
import com.kbytes.safetynet.model.dto.FireStationUpdateDTO;
import com.kbytes.safetynet.service.FireStationService;

@RestController
public class FireStationController {

	@Autowired
	FireStationService fireStationService;

	@Autowired
	ModelMapper mapper;

	@PostMapping("/firestations")
	public ResponseEntity<FireStationCreateDTO> createFireStation(@RequestBody FireStationCreateDTO fireStationDTO) {

		FireStation fireStationReq = mapper.map(fireStationDTO, FireStation.class);

		FireStation fireStation = fireStationService.save(fireStationReq);

		FireStationCreateDTO fireStationRes = mapper.map(fireStation, FireStationCreateDTO.class);

		return new ResponseEntity<FireStationCreateDTO>(fireStationRes, HttpStatus.CREATED);
	}

	@PutMapping("/firestations/{address}")
	public ResponseEntity<FireStationUpdateDTO> updateFireStation(@PathVariable String address,
			@RequestBody FireStationUpdateDTO fireStationDTO) {

		FireStation fireStationReq = mapper.map(fireStationDTO, FireStation.class);

		FireStation fireStation = fireStationService.update(address, fireStationReq);

		FireStationUpdateDTO fireStationRes = mapper.map(fireStation, FireStationUpdateDTO.class);

		return new ResponseEntity<FireStationUpdateDTO>(fireStationRes, HttpStatus.OK);
	}

	@DeleteMapping("/firestations/{address}")
	public ResponseEntity<String> deleteFireStation(@PathVariable String address) {

		boolean status = fireStationService.delete(address);
		if (status)
			return new ResponseEntity<String>("Deletion complete !", HttpStatus.OK);

		return new ResponseEntity<String>("Something went wrong !", HttpStatus.OK);
	}

	@GetMapping("firestations")
	public ResponseEntity<List<FireStation>> findAll() {
		List<FireStation> results = fireStationService.findAll();
		return new ResponseEntity<List<FireStation>>(results, HttpStatus.OK);
	}

	@GetMapping("firestations/{address}")
	public ResponseEntity<EntityModel<FireStation>> findOne(@PathVariable String address) {
		FireStation firestation = fireStationService.findOne(address);

		Link linkTo = WebMvcLinkBuilder.linkTo(methodOn(this.getClass()).findAll()).withRel("all-firestations");

		EntityModel<FireStation> resource = EntityModel.of(firestation);

		resource.add(linkTo);

		return new ResponseEntity<EntityModel<FireStation>>(resource, HttpStatus.OK);
	}
}
