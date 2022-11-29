package com.mitocode;

import com.mitocode.handler.ClienteHandler;
import com.mitocode.handler.FacturaHandler;
import com.mitocode.handler.PlatoHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

//Clase que contiene las rutas de los handler
@Configuration
public class RouterConfig {

	@Bean
	public RouterFunction<ServerResponse> rutasPlatos(PlatoHandler handler) {
		return route(GET("/v2/platos"), handler::listar) //req -> handler.listar(req)
				.andRoute(GET("/v2/platos/{id}"), handler::listarPorId)
				.andRoute(GET("/v2/platos/hateoas/{id}"), handler::listarPorIdHateoas)
				.andRoute(POST("/v2/platos"), handler::registrar)
				.andRoute(PUT("/v2/platos/{id}"), handler::modificar)
				.andRoute(DELETE("/v2/platos/{id}"), handler::eliminar);
	}
	
	@Bean
	public RouterFunction<ServerResponse> rutasClientes(ClienteHandler handler) {
		return route(GET("/v2/clientes"), handler::listar) //req -> handler.listar(req)
				.andRoute(GET("/v2/clientes/{id}"), handler::listarPorId)
				.andRoute(POST("/v2/clientes"), handler::registrar)
				.andRoute(PUT("/v2/clientes/{id}"), handler::modificar)
				.andRoute(DELETE("/v2/clientes/{id}"), handler::eliminar);
	}
	
	@Bean
	public RouterFunction<ServerResponse> rutasFacturas(FacturaHandler handler) {
		return route(GET("/v2/facturas"), handler::listar) //req -> handler.listar(req)
				.andRoute(GET("/v2/facturas/{id}"), handler::listarPorId)
				.andRoute(POST("/v2/facturas"), handler::registrar)
				.andRoute(PUT("/v2/facturas/{id}"), handler::modificar)
				.andRoute(DELETE("/v2/facturas/{id}"), handler::eliminar);
	}
}