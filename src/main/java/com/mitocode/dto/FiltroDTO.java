package com.mitocode.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class FiltroDTO {
	private String idClient;
	private LocalDate startDate;
	private LocalDate endDate;
}