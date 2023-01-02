package com.kbytes.safetynet.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoveredFamilyDTO {
	private String address;	
	private List<PersonInfoDTO> members;
	
}
