package com.mitocode.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FiltroDTO {
	private String idCliente;
	private LocalDate fechaInicio;
	private LocalDate fechaFin;
}