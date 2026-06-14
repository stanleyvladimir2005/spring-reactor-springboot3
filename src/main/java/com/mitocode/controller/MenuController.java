package com.mitocode.controller;

import com.mitocode.model.Menu;
import com.mitocode.service.IMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/menus")
@RequiredArgsConstructor
public class MenuController {

    private final IMenuService service;
	
	@GetMapping//CONSULTAR DE ACUERDO AL ROL DEL USUARIO QUE INICIO, DEVOLVER SUS MENUS
	public Mono<ResponseEntity<Flux<Menu>>> listar(){
		return ReactiveSecurityContextHolder.getContext()
				.map(SecurityContext::getAuthentication)
				.map(Authentication::getAuthorities)
				.map(roles -> {
					var rolesString = roles.stream().map(Object::toString).collect(Collectors.joining(","));
					var strings = rolesString.split(",");
					return service.getMenus(strings);
				})
				.flatMap(fx -> Mono.just(ResponseEntity.ok()  //Mono.just(ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(fx)));
	}
}