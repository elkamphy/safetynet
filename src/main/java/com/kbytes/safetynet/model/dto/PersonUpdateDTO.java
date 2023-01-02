package com.kbytes.safetynet.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PersonUpdateDTO {
	private String address;
	private String city;
	private String phone;
	private String zip;
	private String email;
}
