package com.mitocode.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Date;

@Data
@AllArgsConstructor
public class ErrorLogin {
	private String mensaje;
	private Date timestamp;
}