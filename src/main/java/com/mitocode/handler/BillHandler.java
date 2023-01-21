package com.mitocode.handler;

import com.mitocode.model.Bill;
import com.mitocode.service.IBillService;
import com.mitocode.util.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import java.net.URI;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class BillHandler {
	
	@Autowired
	private IBillService service;
	
	@Autowired
	private RequestValidator validadorGeneral;
		
	public Mono<ServerResponse> findAll(ServerRequest req){
		return ServerResponse
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(service.findAll(), Bill.class);
	}
	
	public Mono<ServerResponse> findById (ServerRequest req){
		String id = req.pathVariable("id");
		return service.findById(id)
				.flatMap(p -> ServerResponse
						.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(p))
				)
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> save(ServerRequest req) {
		Mono<Bill> monoFactura = req.bodyToMono(Bill.class);
		return monoFactura		//VALIDACION CONSTRAINT DE LA CAPA MODELO. METODO 2
				.flatMap(validadorGeneral::validate)//validacion
				.flatMap(service::save)//p -> service.registrar(p)
				.flatMap(p -> ServerResponse.created(URI.create(req.uri().toString().concat(p.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(p))
		);	
	}
	
	public Mono<ServerResponse> update(ServerRequest req) {
		Mono<Bill> monoPlato = req.bodyToMono(Bill.class);
		Mono<Bill> monoBD = service.findById(req.pathVariable("id"));
		return monoBD
				.zipWith(monoPlato, (bd, p) -> {				
					bd.setId(req.pathVariable("id"));
					bd.setClient(p.getClient());
					bd.setDescription(p.getDescription());
					bd.setObservation(p.getObservation());
					bd.setItems(p.getItems());
					return bd;
				})							
				.flatMap(validadorGeneral::validate)
				.flatMap(service::update)
				.flatMap(p -> ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(p))
				)
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> delete(ServerRequest req){
		String id = req.pathVariable("id");
		return service.findById(id)
				.flatMap(p -> service.delete(p.getId())
						.then(ServerResponse.noContent().build())
				)
				.switchIfEmpty(ServerResponse.notFound().build());
	}
}