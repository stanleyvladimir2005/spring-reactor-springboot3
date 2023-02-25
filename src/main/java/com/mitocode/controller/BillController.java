package com.mitocode.controller;

import com.mitocode.dto.FilterDTO;
import com.mitocode.model.Bill;
import com.mitocode.service.IBillService;
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
@RequestMapping("/v1/bills")
public class BillController {
	
	@Autowired
	private IBillService service;
	
	@GetMapping
	public Mono<ResponseEntity<Flux<Bill>>> findAll() {
		Flux<Bill> fxBills = service.findAll();
		return Mono.just(ResponseEntity
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(fxBills)
				);
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Bill>> findById(@PathVariable("id") String id){
		return service.findById(id) //Mono<Bill>
				.map(p -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(p)
						) //Mono<ResponseEntity<Bill>>
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	@PostMapping
	public Mono<ResponseEntity<Bill>> save (@Valid @RequestBody Bill p, final ServerHttpRequest req){
		return service.save(p)
				.map(pl -> ResponseEntity.created(URI.create(req.getURI().toString().concat("/").concat(pl.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(pl)
				);
	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Bill>> modificar(@Valid @RequestBody Bill p, @PathVariable("id") String id){
		Mono<Bill> monoBill = Mono.just(p);
		Mono<Bill> monoBD = service.findById(id);
		return monoBD
				.zipWith(monoBill, (bd, pl) -> {
					bd.setId(id);
					bd.setClient(p.getClient());
					bd.setDescription(p.getDescription());
					bd.setObservation(p.getObservation());
					bd.setItems(p.getItems());
					return bd;
				})
				.flatMap(service::update) //bd -> service.modificar(bd)
				.map(pl -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(pl))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable("id") String id){
		return service.findById(id)
				.flatMap(p -> service.delete(p.getId()) //Mono<Void>
						.then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@GetMapping("/hateoas/{id}")
	public Mono<EntityModel<Bill>> listByHateoas(@PathVariable("id") String id){
		//localhost:8080/platos/60779cc08e37a27164468033	
		Mono<Link> link1 =linkTo(methodOn(BillController.class).findById(id)).withSelfRel().toMono();
		Mono<Link> link2 =linkTo(methodOn(BillController.class).findById(id)).withSelfRel().toMono();
		return link1.zipWith(link2)
				.map(function((left, right) -> Links.of(left, right)))				
				.zipWith(service.findById(id), (lk, p) -> EntityModel.of(p, lk));
	}
	
	@GetMapping("/pageable")
	public Mono<ResponseEntity<PageSupport<Bill>>> listPagebale(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "10") int size){
		Pageable pageRequest = PageRequest.of(page, size);
		return service.listPage(pageRequest)
				.map(p -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(p)	
						)
				.defaultIfEmpty(ResponseEntity.noContent().build());
	}
	
	@PostMapping("/buscar") //Metodo para buscar
	public Mono<ResponseEntity<Flux<Bill>>> buscar(@RequestBody FilterDTO filtro){
		Flux<Bill> fxFacturas = service.getDishesByFilter(filtro);
		return Mono.just(ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(fxFacturas)
				);
	}

	@GetMapping("/generarReporte/{id}")
	public Mono<ResponseEntity<byte[]>> generarReporte(@PathVariable("id") String id){
		Mono<byte[]> monoReporte = service.generateReport(id);
		return monoReporte
				.map(bytes -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_OCTET_STREAM)
						.body(bytes)
				).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NO_CONTENT));
	}
}