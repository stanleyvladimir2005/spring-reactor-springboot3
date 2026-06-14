package com.mitocode.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class FilterDTO {
	private String idClient;
	private LocalDate startDate;
	private LocalDate endDate;
}