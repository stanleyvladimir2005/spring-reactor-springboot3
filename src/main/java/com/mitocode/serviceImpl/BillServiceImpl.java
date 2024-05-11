package com.mitocode.serviceImpl;

import com.mitocode.dto.FilterDTO;
import com.mitocode.model.Bill;
import com.mitocode.repo.IClientRepo;
import com.mitocode.repo.IBillRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.repo.IDishRepo;
import com.mitocode.service.IBillService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.HashMap;

@Service
public class BillServiceImpl extends CRUDImpl<Bill, String> implements IBillService {
	
	@Autowired
	private IBillRepo repo;
	
	@Autowired
	private IClientRepo clientRepo;
	
	@Autowired
	private IDishRepo dishRepo;

	@Override
	protected IGenericRepo<Bill, String> getRepo() {
		return repo;
	}

	//Metodo para buscar facturas
	@Override
	public Flux<Bill> getDishesByFilter(FilterDTO filter) {
		var criter = filter.getIdClient() != null ? "C" : "O";
        return criter.equalsIgnoreCase("C") ?
				repo.getInvoicesByCustomer(filter.getIdClient()) :
				repo.getBillsByDate(filter.getStartDate(), filter.getEndDate());
	}
	
	//Metodo para generar el reporte cargando el jr
	@Override
	public Mono<byte[]> generateReport(String idFactura) {
		return repo.findById(idFactura) //Mono<Bill>
					//Obteniendo Client
					.flatMap(f -> Mono.just(f)
							.zipWith(clientRepo.findById(f.getClient().getId()), (fa, cl) -> {
								fa.setClient(cl);
								return fa;
							}))
					//Obteniendo cada Dish
					.flatMap(f -> Flux.fromIterable(f.getItems()).flatMap(it -> dishRepo.findById(it.getDish().getId())
							.map(p -> {
								it.setDish(p);
								return it;
							})).collectList().flatMap(list -> {
								//Seteando la nueva lista a factura
						   		f.setItems(list);
								return Mono.just(f); //devolviendo factura para el siguiente operador (doOnNext)
							}))
					.map(f -> {
						try {
							var parametros = new HashMap<String, Object>();
							parametros.put("txt_cliente", f.getClient().getFirstName() + " " + f.getClient().getLastName());
							var stream = getClass().getResourceAsStream("/facturas.jrxml");
							var report = JasperCompileManager.compileReport(stream);
							var print = JasperFillManager.fillReport(report, parametros, new JRBeanCollectionDataSource(f.getItems()));
							return JasperExportManager.exportReportToPdf(print);
						} catch (Exception e) {
							System.out.printf(String.valueOf(e));
						}
						return new byte[0];					
					});
	}
}