package com.mitocode.controller;

import com.mitocode.dto.FiltroDTO;
import com.mitocode.model.Factura;
import com.mitocode.service.IFacturaService;
import com.mitocode.util.PageSupport;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.net.URI;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;
import static reactor.function.TupleUtils.function;

@RestController
@RequestMapping("/facturas")
public class FacturaController {
	
	@Autowired
	private IFacturaService service;
	
	@GetMapping
	public Mono<ResponseEntity<Flux<Factura>>> listar() {
		Flux<Factura> fxPlatos = service.listar();
		return Mono.just(ResponseEntity
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(fxPlatos)
				);
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Factura>> listarPorId(@PathVariable("id") String id){
		return service.listarPorId(id) //Mono<Factura>
				.map(p -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(p)
						) //Mono<ResponseEntity<Factura>>
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	@PostMapping
	public Mono<ResponseEntity<Factura>> registrar(@Valid @RequestBody Factura p, final ServerHttpRequest req){
		return service.registrar(p)
				.map(pl -> ResponseEntity.created(URI.create(req.getURI().toString().concat("/").concat(pl.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(pl)
				);
	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Factura>> modificar(@Valid @RequestBody Factura p, @PathVariable("id") String id){
		Mono<Factura> monoPlato = Mono.just(p);
		Mono<Factura> monoBD = service.listarPorId(id);
		return monoBD
				.zipWith(monoPlato, (bd, pl) -> {
					bd.setId(id);
					bd.setCliente(p.getCliente());
					bd.setDescripcion(p.getDescripcion());
					bd.setObservacion(p.getObservacion());
					bd.setItems(p.getItems());
					return bd;
				})
				.flatMap(service::modificar) //bd -> service.modificar(bd)
				.map(pl -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(pl))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> eliminar(@PathVariable("id") String id){
		return service.listarPorId(id)
				.flatMap(p -> service.eliminar(p.getId()) //Mono<Void>
						.then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/hateoas/{id}")
	public Mono<EntityModel<Factura>> listarHateoasPorId(@PathVariable("id") String id){
		//localhost:8080/platos/60779cc08e37a27164468033	
		Mono<Link> link1 =linkTo(methodOn(FacturaController.class).listarPorId(id)).withSelfRel().toMono();
		Mono<Link> link2 =linkTo(methodOn(FacturaController.class).listarPorId(id)).withSelfRel().toMono();
		return link1.zipWith(link2)
				.map(function((left, right) -> Links.of(left, right)))				
				.zipWith(service.listarPorId(id), (lk, p) -> EntityModel.of(p, lk));
	}
	
	@GetMapping("/pageable")
	public Mono<ResponseEntity<PageSupport<Factura>>> listarPagebale(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "10") int size){
		Pageable pageRequest = PageRequest.of(page, size);
		return service.listarPage(pageRequest)
				.map(p -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(p)	
						)
				.defaultIfEmpty(ResponseEntity.noContent().build());
	}
	
	@PostMapping("/buscar") //Metodo para buscar
	public Mono<ResponseEntity<Flux<Factura>>> buscar(@RequestBody FiltroDTO filtro){		
		Flux<Factura> fxFacturas = service.obtenerFacturasPorFiltro(filtro);
		return Mono.just(ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(fxFacturas)
				);
	}

	@GetMapping("/generarReporte/{id}")
	public Mono<ResponseEntity<byte[]>> generarReporte(@PathVariable("id") String id){
		Mono<byte[]> monoReporte = service.generarReporte(id);
		return monoReporte
				.map(bytes -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_OCTET_STREAM)
						.body(bytes)
				).defaultIfEmpty(new ResponseEntity<byte[]>(HttpStatus.NO_CONTENT));
	}
}