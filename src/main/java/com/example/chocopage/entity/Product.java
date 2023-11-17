package com.example.chocopage.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//Quizに相当
public class Product {
	
	//識別ID
	@Id
	private Integer Id;
	private Integer Maker;
	private Integer Kind;
	
	private String Name;
	private Integer Taste;
	private String Img;
	//↓不要のはず（DBとの接点がないため）
//	private MultipartFile multipartFile;
	private String Link;
	
//	商品の評価値
	@Transient
	private Double Avg;
	
}