package com.mitocode.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "bills")
public class Bill {

	@Id
	private String id;

	@Size(min = 3)
	@Field(name = "description")
	private String description;

	@Field(name = "observation")
	private String observation;

	//@DBRef No funciona en webFlux
	@NotNull
	@Field(name = "client")
	private Client client;
	
	private List<BillItem> items;

	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime createIn = LocalDateTime.now();
}