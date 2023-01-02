package com.kbytes.safetynet.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChildAlertDTO{
	private List<PersonDTO> children;
	private List<PersonDTO> familyMembers;
}
