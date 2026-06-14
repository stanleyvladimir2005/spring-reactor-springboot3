package com.mitocode.repo;

import com.mitocode.model.Bill;
import org.springframework.data.mongodb.repository.Query;
import reactor.core.publisher.Flux;
import java.time.LocalDate;

public interface IBillRepo extends IGenericRepo<Bill, String>{

	@Query("{'client' : { _id : ?0 }}")
	Flux<Bill> getInvoicesByCustomer(String client);
	
	@Query("{'createIn' : { $gte: ?0, $lt: ?1} }")
	Flux<Bill> getBillsByDate(LocalDate fechaInicio, LocalDate fechaFin);
}