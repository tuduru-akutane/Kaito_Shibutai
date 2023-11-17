package com.example.chocopage.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
	
	private Integer Id;
	private Double Star;
	private String Author;
	private String Review_info;
}