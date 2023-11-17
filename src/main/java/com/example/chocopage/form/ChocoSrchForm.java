package com.example.chocopage.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChocoSrchForm {
	
	private Integer maker;
	//↓部分一致検索は困難？
//	private String name;
	private Integer taste;
	private Integer kind;
	

}
