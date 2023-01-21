package com.mitocode.serviceImpl;

import com.mitocode.dto.FiltroDTO;
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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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
	public Flux<Bill> getDishesByFilter(FiltroDTO filter) {
		String criter = filter.getIdClient() != null ? "C" : "O";
		
		if (criter.equalsIgnoreCase("C"))  //si la busqueda viene el cliente
			return repo.getInvoicesByCustomer(filter.getIdClient());
		 else  //si la busqueda viene por fecha de inicio y fin
			return repo.getBillsByDate(filter.getStartDate(), filter.getEndDate());
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
						InputStream stream;
						try {									
							Map<String, Object> parametros = new HashMap<>();		//obteniendo los parametros de entrada del reporte
							parametros.put("txt_cliente", f.getClient().getFirstName() + " " + f.getClient().getLastName()); //llenamos el parametro de cliente del reporte
							stream = getClass().getResourceAsStream("/facturas.jrxml"); //obteniendo reporte y compilando en memoria
							JasperReport report = JasperCompileManager.compileReport(stream); //compilamos reporte
							JasperPrint print = JasperFillManager.fillReport(report, parametros, new JRBeanCollectionDataSource(f.getItems())); //Pasando parametros al reporte
							return JasperExportManager.exportReportToPdf(print); //exportando reporte a pdf
						} catch (Exception e) {
							e.printStackTrace();
						}
						return new byte[0];					
					});
	}
}