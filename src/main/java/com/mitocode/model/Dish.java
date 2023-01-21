package com.mitocode.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection= "dishes")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dish {
	
	@Id
	private String id;
	
	@NotEmpty
	@Size(min = 3, message = "{dish_name.size}")
	@Field(name="dishName")
	private String dishName;
	
	@Field(name="price")
	private Double price;
	
	@NotNull
	@Field(name="status")
	private boolean status;
}