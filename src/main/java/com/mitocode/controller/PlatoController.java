package com.mitocode.controller;

import com.mitocode.dto.RestResponse;
import com.mitocode.model.Plato;
import com.mitocode.service.IPlatoService;
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
import java.util.ArrayList;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;
import static reactor.function.TupleUtils.function;

@RestController
@RequestMapping("/platos")
public class PlatoController {
	
	@Autowired
	private IPlatoService service;
		
	@GetMapping
	public Mono<ResponseEntity<Flux<Plato>>> listar() {
		Flux<Plato> fxPlatos = service.listar();
		return Mono.just(ResponseEntity
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(fxPlatos)
				);
	}
	
	//Forma especial para mostrar la lista en forma content y error
	@GetMapping("/RR")
	public Mono<ResponseEntity<RestResponse>> listarRR() {
		Flux<Plato> fxPlatos = service.listar();
		return fxPlatos
			.collectList()
			.map(lista -> {
				RestResponse rr = new RestResponse();
				rr.setContent(lista);
				rr.setErrors(new ArrayList<>());	
				return rr;
			})
			.map(rr -> ResponseEntity
					.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(rr));			
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Plato>> listarPorId(@PathVariable("id") String id){
		return service.listarPorId(id) //Mono<Plato>
				.map(p -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(p)
						) //Mono<ResponseEntity<Plato>>
				.defaultIfEmpty(ResponseEntity.notFound().build());				
	}
	
	@PostMapping
	public Mono<ResponseEntity<Plato>> registrar(@Valid @RequestBody Plato p, final ServerHttpRequest req){
		return service.registrar(p)
				.map(pl -> ResponseEntity.created(URI.create(req.getURI().toString().concat("/").concat(pl.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(pl)
				);
	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Plato>> modificar(@Valid @RequestBody Plato p, @PathVariable("id") String id){
		Mono<Plato> monoPlato = Mono.just(p);
		Mono<Plato> monoBD = service.listarPorId(id);
		return monoBD
				.zipWith(monoPlato, (bd, pl) -> {
					bd.setId(id);
					bd.setNombre(pl.getNombre());
					bd.setPrecio(pl.getPrecio());
					bd.setEstado(pl.isEstado());
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
				.flatMap(p -> service.eliminar(p.getId()) // Mono<Void>  return service.eliminar(p.getId())
						.then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@GetMapping("/hateoas/{id}")
	public Mono<EntityModel<Plato>> listarHateoasPorId(@PathVariable("id") String id){
		//localhost:8080/platos/60779cc08e37a27164468033	
		Mono<Link> link1 =linkTo(methodOn(PlatoController.class).listarPorId(id)).withSelfRel().toMono();
		Mono<Link> link2 =linkTo(methodOn(PlatoController.class).listarPorId(id)).withSelfRel().toMono();
		return link1.zipWith(link2)
				.map(function((left, right) -> Links.of(left, right)))				
				.zipWith(service.listarPorId(id), (lk, p) -> EntityModel.of(p, lk));
	}
	
	@GetMapping("/pageable")
	public Mono<ResponseEntity<PageSupport<Plato>>> listarPagebale(@RequestParam(name = "page", defaultValue = "0") int page,@RequestParam(name = "size", defaultValue = "10") int size	){
		Pageable pageRequest = PageRequest.of(page, size);
		return service.listarPage(pageRequest)
				.map(p -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(p)	
						)
				.defaultIfEmpty(ResponseEntity.noContent().build());
	}
}