package com.mitocode.dto;

import lombok.Data;
import java.util.Map;

@Data
public class ErrorResponse {

	Map<String, Object> error;

	public ErrorResponse(Map<String, Object> error) {
		this.error = error;
	}
}