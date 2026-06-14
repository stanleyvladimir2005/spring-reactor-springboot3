package com.mitocode.security;

import lombok.AllArgsConstructor;
import lombok.Data;

//Clase S2
@Data
@AllArgsConstructor
public class AuthRequest {
	private String username;
	private String password;
}
