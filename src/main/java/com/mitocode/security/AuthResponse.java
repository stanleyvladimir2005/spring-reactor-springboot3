package com.mitocode.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Date;

//Clase S3
@Data
@AllArgsConstructor
public class AuthResponse {
	private String token;
	private Date expiration;
}