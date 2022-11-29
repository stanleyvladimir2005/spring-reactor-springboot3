package com.mitocode.handler;

import com.mitocode.dto.HateoasModel;
import com.mitocode.model.Plato;
import com.mitocode.service.IPlatoService;
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
public class PlatoHandler {
	
	@Autowired
	private IPlatoService service;
	
	@Autowired
	private RequestValidator validadorGeneral;
		
	public Mono<ServerResponse> listar(ServerRequest req){
		return ServerResponse
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(service.listar(), Plato.class);
	}
	
	public Mono<ServerResponse> listarPorId(ServerRequest req){
		String id = req.pathVariable("id");
		return service.listarPorId(id)
				.flatMap(p -> ServerResponse
						.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(p))
				)
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> registrar(ServerRequest req) {
		Mono<Plato> monoPlato = req.bodyToMono(Plato.class);
		return monoPlato
				.flatMap(validadorGeneral::validate)//validacion
				.flatMap(service::registrar)//p -> service.registrar(p)
				.flatMap(p -> ServerResponse.created(URI.create(req.uri().toString().concat(p.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(p))
		);	
	}
	
	public Mono<ServerResponse> modificar(ServerRequest req) {
		Mono<Plato> monoPlato = req.bodyToMono(Plato.class);		
		Mono<Plato> monoBD = service.listarPorId(req.pathVariable("id"));
		return monoBD
				.zipWith(monoPlato, (bd, p) -> {				
					bd.setId(p.getId());
					bd.setNombre(p.getNombre());
					bd.setEstado(p.isEstado());
					return bd;
				})											
				.flatMap(service::modificar)
				.flatMap(p -> ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(p))
				)
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> eliminar(ServerRequest req){
		String id = req.pathVariable("id");
		return service.listarPorId(id)
				.flatMap(p -> service.eliminar(p.getId())
						.then(ServerResponse.noContent().build())
				)
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	//HATEOAS EN HANDLER 
	public Mono<ServerResponse> listarPorIdHateoas(ServerRequest req){
		String id = req.pathVariable("id");
		return service.listarPorId(id)
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