package com.mitocode.controller;

import com.mitocode.dto.RestResponse;
import com.mitocode.model.Dish;
import com.mitocode.service.IDishService;
import com.mitocode.util.PageSupport;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
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
@RequestMapping("/v1/dishes")
@RequiredArgsConstructor
public class DishController {
	
	private final IDishService service;
		
	@GetMapping
	public Mono<ResponseEntity<Flux<Dish>>> findAll() {
		var fxDishes = service.findAll();
		return Mono.just(ResponseEntity
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(fxDishes)
				);
	}
	
	//Forma especial para mostrar la lista en forma content y error
	@GetMapping("/RR")
	public Mono<ResponseEntity<RestResponse>> listRR() {
		var fxDishes = service.findAll();
		return fxDishes
			.collectList()
			.map(list -> {
				var rr = new RestResponse();
				rr.setContent(list);
				rr.setErrors(new ArrayList<>());	
				return rr;
			})
			.map(rr -> ResponseEntity
					.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(rr));			
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Dish>> findById(@PathVariable String id){
		return service.findById(id) //Mono<Dish>
				.map(p -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(p)
						) //Mono<ResponseEntity<Dish>>
				.defaultIfEmpty(ResponseEntity.notFound().build());				
	}
	
	@PostMapping
	public Mono<ResponseEntity<Dish>> save(@Valid @RequestBody Dish p, final ServerHttpRequest req){
		return service.save(p)
				.map(pl -> ResponseEntity.created(URI.create(req.getURI().toString().concat("/").concat(pl.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(pl)
				);
	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Dish>> update(@Valid @RequestBody Dish p, @PathVariable String id){
		var monoDish = Mono.just(p);
		var monoBD = service.findById(id);
		return monoBD
				.zipWith(monoDish, (bd, pl) -> {
					bd.setId(id);
					bd.setDishName(pl.getDishName());
					bd.setPrice(pl.getPrice());
					bd.setStatus(pl.isStatus());
					return bd;
				})
				.flatMap(service::update) //bd -> service.modificar(bd)
				.map(pl -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(pl))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String id){
		return service.findById(id)
				.flatMap(p -> service.delete(p.getId()) // Mono<Void>  return service.eliminar(p.getId())
						.then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@GetMapping("/hateoas/{id}")
	public Mono<EntityModel<Dish>> listByHateoas(@PathVariable String id){
		//localhost:8080/Dishes/60779cc08e37a27164468033	
		var link1 =linkTo(methodOn(DishController.class).findById(id)).withSelfRel().toMono();
		var link2 =linkTo(methodOn(DishController.class).findById(id)).withSelfRel().toMono();
		return link1.zipWith(link2)
				.map(function((left, right) -> Links.of(left, right)))				
				.zipWith(service.findById(id), (lk, p) -> EntityModel.of(p, lk));
	}
	
	@GetMapping("/pageable")
	public Mono<ResponseEntity<PageSupport<Dish>>> listPagebale(@RequestParam(name = "page", defaultValue = "0") int page,
																@RequestParam(name = "size", defaultValue = "10") int size){
		var pageRequest = PageRequest.of(page, size);
		return service.listPage(pageRequest)
				.map(p -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(p)	
						)
				.defaultIfEmpty(ResponseEntity.noContent().build());
	}
}