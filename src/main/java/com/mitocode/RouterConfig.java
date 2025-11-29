package com.mitocode;

/*
import com.mitocode.handler.ClientHandler;
import com.mitocode.handler.BillHandler;
import com.mitocode.handler.DishHandler;
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
	public RouterFunction<ServerResponse> rutasPlatos(DishHandler handler) {
		return route(GET("/v2/dishes"), handler::findAll) //req -> handler.listar(req)
				.andRoute(GET("/v2/dishes/{id}"), handler::findById)
				.andRoute(GET("/v2/dishes/hateoas/{id}"), handler::listByHateoas)
				.andRoute(POST("/v2/dishes"), handler::save)
				.andRoute(PUT("/v2/dishes/{id}"), handler::update)
				.andRoute(DELETE("/v2/dishes/{id}"), handler::delete);
	}
	
	@Bean
	public RouterFunction<ServerResponse> rutasClientes(ClientHandler handler) {
		return route(GET("/v2/clients"), handler::findAll) //req -> handler.listar(req)
				.andRoute(GET("/v2/clients/{id}"), handler::findById)
				.andRoute(POST("/v2/clients"), handler::save)
				.andRoute(PUT("/v2/clients/{id}"), handler::update)
				.andRoute(DELETE("/v2/clients/{id}"), handler::delete);
	}
	
	@Bean
	public RouterFunction<ServerResponse> rutasFacturas(BillHandler handler) {
		return route(GET("/v2/bills"), handler::findAll) //req -> handler.listar(req)
				.andRoute(GET("/v2/bills/{id}"), handler::findById)
				.andRoute(POST("/v2/bills"), handler::save)
				.andRoute(PUT("/v2/bills/{id}"), handler::update)
				.andRoute(DELETE("/v2/bills/{id}"), handler::delete);
	}
}*/