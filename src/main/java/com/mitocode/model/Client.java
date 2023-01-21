package com.mitocode.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection= "clients")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Client {
	
	@Id
	private String id;

	@Size(min = 3, message = "{first_name.size}")
	@Field(name = "firstName")
	private String firstName;

	@Size(min = 3, message = "{last_name.size}")
	@Field(name = "lastName")
	private String lastName;

	@Field(name = "birthday")
	private LocalDate birthday;

	@Field(name = "urlPhoto")
	private String urlPhoto;
}