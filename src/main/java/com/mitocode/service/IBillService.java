package com.mitocode.service;

import com.mitocode.dto.FilterDTO;
import com.mitocode.model.Bill;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IBillService extends ICRUD<Bill, String> {
	
	Flux<Bill> getDishesByFilter(FilterDTO filtro);

	Mono<byte[]> generateReport(String idFactura);
}