package com.mitocode.handler;

import com.mitocode.dto.HateoasModel;
import com.mitocode.model.Dish;
import com.mitocode.service.IDishService;
import com.mitocode.util.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import java.net.URI;
import java.util.List;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class DishHandler {
	
	@Autowired
	private IDishService service;
	
	@Autowired
	private RequestValidator validadorGeneral;
		
	public Mono<ServerResponse> findAll(ServerRequest req){
		return ServerResponse
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(service.findAll(), Dish.class);
	}
	
	public Mono<ServerResponse> findById(ServerRequest req){
		var id = req.pathVariable("id");
		return service.findById(id)
				.flatMap(p -> ServerResponse
						.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(p))
				)
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> save(ServerRequest req) {
		var monoPlato = req.bodyToMono(Dish.class);
		return monoPlato
				.flatMap(validadorGeneral::validate)//validacion
				.flatMap(service::save)//p -> service.registrar(p)
				.flatMap(p -> ServerResponse.created(URI.create(req.uri().toString().concat(p.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(p))
		);	
	}
	
	public Mono<ServerResponse> update(ServerRequest req) {
		var monoPlato = req.bodyToMono(Dish.class);
		var monoBD = service.findById(req.pathVariable("id"));
		return monoBD
				.zipWith(monoPlato, (bd, p) -> {				
					bd.setId(p.getId());
					bd.setDishName(p.getDishName());
					bd.setStatus(p.isStatus());
					return bd;
				})											
				.flatMap(service::update)
				.flatMap(p -> ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(p))
				)
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> delete(ServerRequest req){
		var id = req.pathVariable("id");
		return service.findById(id)
				.flatMap(p -> service.delete(p.getId())
						.then(ServerResponse.noContent().build())
				)
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	//HATEOAS EN HANDLER 
	public Mono<ServerResponse> listByHateoas(ServerRequest req){
		var id = req.pathVariable("id");
		return service.findById(id)
				.map(p -> {
					HateoasModel hm = new HateoasModel();
					hm.setModel(p);
					hm.setLinks(List.of(req.path()));
					return hm;
				})
				.flatMap(hm -> ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(hm))
				);
	}	
}