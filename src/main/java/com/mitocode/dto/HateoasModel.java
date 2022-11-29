package com.mitocode.dto;

import lombok.Data;

import java.util.List;

@Data
public class HateoasModel {
	private Object model;
	private List<?> links;
}