package com.kbytes.safetynet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FireStation {
	private String address;
	private int station;
	@JsonIgnore
	private String _links;
	@JsonIgnore
	private String links;

}
