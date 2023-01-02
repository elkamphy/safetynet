package com.kbytes.safetynet.exceptions;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class ExceptionResponse {
	private Date timestamp;
	private String message;
	private String details;
}
