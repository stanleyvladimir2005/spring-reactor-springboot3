package com.mitocode.controller;

import com.mitocode.security.AuthRequest;
import com.mitocode.security.AuthResponse;
import com.mitocode.security.ErrorLogin;
import com.mitocode.security.JWTUtil;
import com.mitocode.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.util.Date;

@RestController
@RequiredArgsConstructor
public class LoginController {

	private final JWTUtil jwtUtil;
    private final IUserService service;
	
	@PostMapping("/login")
	public Mono<ResponseEntity<?>> login(@RequestBody AuthRequest ar){
		return service.searchByUser(ar.getUsername())
				.map((userDetails) -> {
                    assert userDetails.getPassword() != null;
                    if(BCrypt.checkpw(ar.getPassword(), userDetails.getPassword())) {
						var token = jwtUtil.generateToken(userDetails);
						var expiration = jwtUtil.getExpirationDateFromToken(token);
						return ResponseEntity.ok(new AuthResponse(token, expiration));
					}else
						return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorLogin("Invalid Credentials", new Date()));
				}).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
	}
	
	@PostMapping("/v2/login")
	public Mono<ResponseEntity<?>> login(@RequestHeader("user") String user, @RequestHeader("password") String clave){
		return service.searchByUser(user)
				.map((userDetails) -> {
                    assert userDetails.getPassword() != null;
                    if(BCrypt.checkpw(clave, userDetails.getPassword())) {
						var token = jwtUtil.generateToken(userDetails);
						var expiracion = jwtUtil.getExpirationDateFromToken(token);
						return ResponseEntity.ok(new AuthResponse(token, expiracion));
					}else
						return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorLogin("Invalid Credentials", new Date()));
				}).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
	}
}