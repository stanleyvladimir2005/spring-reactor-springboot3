package com.mitocode.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mitocode.exceptions.FileException;
import com.mitocode.model.Client;
import com.mitocode.service.IClientService;
import com.mitocode.util.PageSupport;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cloudinary.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Links;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;
import static reactor.function.TupleUtils.function;

@RestController
@RequestMapping("/v1/clients")
@RequiredArgsConstructor
public class ClientController {
	
	private final IClientService service;
	
	@GetMapping
	public Mono<ResponseEntity<Flux<Client>>> findAll() {
		var fxClients = service.findAll();
		return Mono.just(ResponseEntity
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(fxClients)
				);
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Client>> findById(@PathVariable("id") String id){
		return service.findById(id) //Mono<Client>
				.map(p -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(p)
						) //Mono<ResponseEntity<Client>>
				.defaultIfEmpty(ResponseEntity.notFound().build());				
	}
	
	@PostMapping
	public Mono<ResponseEntity<Client>> save(@Valid @RequestBody Client p, final ServerHttpRequest req){
		return service.save(p)
				.map(pl -> ResponseEntity.created(URI.create(req.getURI().toString().concat("/").concat(pl.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(pl)
				);
	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Client>> update(@Valid @RequestBody Client p, @PathVariable("id") String id){
		var monoClient = Mono.just(p);
		var monoBD = service.findById(id);
		return monoBD
				.zipWith(monoClient, (bd, pl) -> {
					bd.setId(id);
					bd.setFirstName(p.getFirstName());
					bd.setLastName(p.getLastName());
					bd.setBirthday(p.getBirthday());
					bd.setUrlPhoto(p.getUrlPhoto());
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
	public Mono<EntityModel<Client>> listByHateoas(@PathVariable("id") String id){
		//localhost:8080/clients/60779cc08e37a27164468033
		var link1 =linkTo(methodOn(ClientController.class).findById(id)).withSelfRel().toMono();
		var link2 =linkTo(methodOn(ClientController.class).findById(id)).withSelfRel().toMono();
		return link1.zipWith(link2)
				.map(function((left, right) -> Links.of(left, right)))				
				.zipWith(service.findById(id), (lk, p) -> EntityModel.of(p, lk));
	}
	
	@GetMapping("/pageable")
	public Mono<ResponseEntity<PageSupport<Client>>> listPagebale(@RequestParam(name = "page", defaultValue = "0") int page,
																  @RequestParam(name = "size", defaultValue = "10") int size){
		var pageRequest = PageRequest.of(page, size);
		return service.listPage(pageRequest)
				.map(p -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(p)	
						)
				.defaultIfEmpty(ResponseEntity.noContent().build());
	}

	/*Se sube el archivo a cloudinary usando metodo bloqueante, ya que espera recuperar la informacion del cliente
	 y luego transfiere a cloudinare */
	@PostMapping("/v1/upload/{id}")
	public Mono<ResponseEntity<Client>> upload(@PathVariable String id, @RequestPart FilePart file){
		var cloudinary = new Cloudinary(ObjectUtils.asMap(
				//"cloud_name", "ds6pdw45e",
				"api_key", "513196324494765",
				"api_secret", "PUvNv61a0Ohd4DadfBIillVjuHI"));
		return service.findById(id)
				.publishOn(Schedulers.boundedElastic()) //boundedElastic se usa para transferir procesos bloqueantes
				.flatMap(c -> {
					try {
						var f = Files.createTempFile("temp", file.filename()).toFile();
						file.transferTo(f).block(); //para tener la transferencia lista
						var response= cloudinary.uploader().upload(f, ObjectUtils.asMap("resource_type", "auto"));
						var json = new JSONObject(response);
						var url = json.getString("url");
						c.setUrlPhoto(url);
						return service.update(c).thenReturn(ResponseEntity.ok().body(c));
					}catch(Exception e) {
						return Mono.error(new FileException("error al subir el archivo"));
					}	
					//return Mono.just(ResponseEntity.ok().body(c));
				})
				.defaultIfEmpty(ResponseEntity.notFound().build());				
	}

	//Este metodo sube el archivo a cloudinary sin usar metodo bloqueante,
	// ya que primero transfiere y luego busca el id del cliente y luego transfiere
	@PostMapping("/v2/upload/{id}")
	public Mono<ResponseEntity<Client>> uploadV2(@PathVariable String id, @RequestPart FilePart file) throws IOException{
		Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
				"cloud_name", "ds6pdw45e",	
				"api_key", "513196324494765",
				"api_secret", "PUvNv61a0Ohd4DadfBIillVjuHI"));
		var f = Files.createTempFile("temp", file.filename()).toFile();
		return file.transferTo(f)
				.then(service.findById(id)
						.publishOn(Schedulers.boundedElastic())
						.flatMap(c -> {
							try {
								var response = cloudinary.uploader().upload(f , ObjectUtils.asMap("resource_type", "auto"));
								var json = new JSONObject(response);
								var url = json.getString("url");
						        c.setUrlPhoto(url);
							} catch (IOException e) {
								return Mono.error(new FileException("error al subir el archivo"));
							}
							return service.update(c).then(Mono.just(ResponseEntity.ok().body(c)));
						})
						.defaultIfEmpty(ResponseEntity.notFound().build())
					);	
	}
}