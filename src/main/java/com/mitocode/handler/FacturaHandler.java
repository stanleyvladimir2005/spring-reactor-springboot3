package com.mitocode.handler;

import com.mitocode.model.Factura;
import com.mitocode.service.IFacturaService;
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
public class FacturaHandler {
	
	@Autowired
	private IFacturaService service;
	
	@Autowired
	private RequestValidator validadorGeneral;
		
	public Mono<ServerResponse> listar(ServerRequest req){
		return ServerResponse
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(service.listar(), Factura.class);
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
		Mono<Factura> monoFactura = req.bodyToMono(Factura.class);
		return monoFactura		//VALIDACION CONSTRAINT DE LA CAPA MODELO. METODO 2
				.flatMap(validadorGeneral::validate)//validacion
				.flatMap(service::registrar)//p -> service.registrar(p)
				.flatMap(p -> ServerResponse.created(URI.create(req.uri().toString().concat(p.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(p))
		);	
	}
	
	public Mono<ServerResponse> modificar(ServerRequest req) {
		Mono<Factura> monoPlato = req.bodyToMono(Factura.class);		
		Mono<Factura> monoBD = service.listarPorId(req.pathVariable("id"));
		return monoBD
				.zipWith(monoPlato, (bd, p) -> {				
					bd.setId(req.pathVariable("id"));
					bd.setCliente(p.getCliente());
					bd.setDescripcion(p.getDescripcion());
					bd.setObservacion(p.getObservacion());
					bd.setItems(p.getItems());
					return bd;
				})							
				.flatMap(validadorGeneral::validate)
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
}