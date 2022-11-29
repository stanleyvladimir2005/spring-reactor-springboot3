package com.mitocode.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDate;

@Data
@Document(collection= "clientes")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Cliente {
	
	@Id
	private String id;

	@Size(min = 3, message = "nombre minimo 3")
	@Field(name = "nombres")
	private String nombres;

	@Size(min = 3, message = "nombre minimo 3")
	@Field(name = "apellidos")
	private String apellidos;

	@Field(name = "fechaNacimiento")
	private LocalDate fechaNacimiento;

	@Field(name = "urlFoto")
	private String urlFoto;
}