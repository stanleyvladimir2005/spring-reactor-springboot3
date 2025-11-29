package com.mitocode.controller;

import com.mitocode.model.Dish;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import java.time.Duration;

@RestController
@RequestMapping("/v1/backpressure")
public class BackPressureController {
	
	@GetMapping(path = "/stream", produces = MediaType.APPLICATION_JSON_VALUE)	
	public Flux<Dish> stream(){
		return Flux.interval(Duration.ofMillis(100))
				.map(t -> new Dish("1", "ARROZ", 20.0, true));
	}
	
	@GetMapping(path = "/noStream", produces = "application/stream+json")	
	public Flux<Dish> noStream(){
		return Flux.interval(Duration.ofMillis(100))
				.map(t -> new Dish("1", "ARROZ", 20.0, true));
	}
	
	@GetMapping(path = "/noStreamFinito", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<Dish> fluxFinitonoStream() {
	    return Flux.range(0, 5000)
	             .map(t -> new Dish("1", "ARROZ", 20.0, true));
	}

    /*
	@GetMapping(path = "/streamFinito", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Dish> fluxFinitoStream() {
        return Flux.range(0, 5000)
                .map(t -> new Dish("1", "ARROZ", 20.0, true));
    }*/

	@GetMapping(value= "/buffer")
	public Flux<Integer> testContrapresion() {
		return Flux.range(1, 100)  
				.log()
				.limitRate(10, 2)
				.delayElements(Duration.ofMillis(1));
	}
}