package com.kbytes.safetynet.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class PersonDTO {
	protected String firstName;
	protected String lastName;
	protected String phone;
	protected String address;
	protected int age;
}
