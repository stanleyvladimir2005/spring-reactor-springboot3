package com.mitocode.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "menus")
public class Menu {

	@Id
	private String id;

	@Field(name = "icon")
	private String icon;

	@Field(name = "menuName")
	private String menuName;

	@Field(name = "url")
	private String url;

	@Field(name = "roles")
	private List<String> roles;
}