package com.mitocode.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FacturaItem {

	private Integer cantidad;
	private Plato plato;
}