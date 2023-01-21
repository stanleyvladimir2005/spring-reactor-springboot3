package com.mitocode.service;

import com.mitocode.dto.FiltroDTO;
import com.mitocode.model.Bill;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IBillService extends ICRUD<Bill, String> {
	
	Flux<Bill> getDishesByFilter(FiltroDTO filtro);

	Mono<byte[]> generateReport(String idFactura);
}