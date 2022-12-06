package com.mitocode.repo;

import com.mitocode.model.Factura;
import org.springframework.data.mongodb.repository.Query;
import reactor.core.publisher.Flux;
import java.time.LocalDate;

public interface IFacturaRepo extends IGenericRepo<Factura, String>{

	@Query("{'cliente' : { _id : ?0 }}")
	Flux<Factura> obtenerFacturasPorCliente(String cliente);
	
	@Query("{'creadoEn' : { $gte: ?0, $lt: ?1} }") 
	Flux<Factura> obtenerFacturasPorFecha(LocalDate fechaInicio, LocalDate fechaFin);
}