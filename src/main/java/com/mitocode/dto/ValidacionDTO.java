package com.mitocode.dto;

import lombok.Data;

@Data
public class ValidacionDTO {

	private String campo;
	private String mensaje;

	public ValidacionDTO() {
	}

	public ValidacionDTO(String campo, String mensaje) {
		this.campo = campo;
		this.mensaje = mensaje;
	}
}