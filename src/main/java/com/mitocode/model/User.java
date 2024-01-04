package com.mitocode.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "users")
public class User {

	@Id
	private String id;

	@Field(name = "user")
	private String user;

	@Field(name = "password")
	private String password;

	@Field(name = "status")
	private Boolean status;

	private List<Rol> roles;
}