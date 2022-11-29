package com.mitocode.dto;

import lombok.Data;

import java.util.List;

@Data
public class RestResponse {

	private List<?> content;
	private List<ErrorResponse> errors;
}