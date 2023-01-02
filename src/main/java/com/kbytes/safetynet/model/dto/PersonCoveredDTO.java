package com.kbytes.safetynet.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PersonCoveredDTO {
	protected List<PersonDTO> persons;
	private int adultCount;
	private int childCount;
}
