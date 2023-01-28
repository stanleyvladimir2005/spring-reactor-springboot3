package com.mitocode.exceptions;

import com.mitocode.dto.ErrorResponse;
import com.mitocode.dto.RestResponse;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;
import java.util.*;

@Component
@Order(-1)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

	public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties webproperties,
										  ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
		super(errorAttributes, webproperties.getResources(), applicationContext);
		this.setMessageWriters(configurer.getWriters());
	}
	
	@Override
	protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
		return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse); //req -> this.renderErrorResponse(req)
	}
	
	private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
		Map<String, Object> errorGeneral = getErrorAttributes(request, ErrorAttributeOptions.defaults());
		Map<String, Object> mapException = new HashMap<>();
		var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		String statusCode = String.valueOf(errorGeneral.get("status"));
		switch (statusCode) {
			case "500" -> {
				mapException.put("code", "500");
				mapException.put("excepcion", "Error general del backend");
			}
			case "400" -> {
				request.exchange().getAttributes();
				try {
					mapException.put("code", "400");
					mapException.put("excepcion", "Peticion incorrecta");
					httpStatus = HttpStatus.BAD_REQUEST;
				} catch (Exception e) {
					mapException.put("error", "500");
					mapException.put("excepcion", "Error general del backend");
				}
			}
			case "406" -> {
				mapException.put("code", "406");
				mapException.put("excepcion", "Archivo no subido correctamente");
				httpStatus = HttpStatus.NOT_ACCEPTABLE;
			}
			default -> {
				mapException.put("code", "900");
				mapException.put("excepcion", errorGeneral.get("error"));
				httpStatus = HttpStatus.CONFLICT;
			}
		}
		RestResponse rr = new RestResponse();
		rr.setContent(new ArrayList<>());
		rr.setErrors(List.of(new ErrorResponse(mapException)));
		
		return ServerResponse.status(httpStatus)
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(rr));
	}
}