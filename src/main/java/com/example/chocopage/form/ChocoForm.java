package com.example.chocopage.form;

import org.springframework.data.annotation.Transient;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChocoForm {
	
	
	private Integer id;
	private Integer maker;
	private Integer kind;
	@NotBlank
	private String name;
	private Integer taste;
//	@NotBlank//→必ずはじかれてしまうので避ける
	private String img;
//	↓img代わり、いったんファイル保存
//	@NotBlank→不可！
	@Transient
	private MultipartFile multipartFile;
	private String link;
	//↓あると登録不可のため避ける
//	@Transient
//	private Double star;
	
	
	//↓以下2つは使っていない！
	//multipartfile→img変換メソッド
	public void setImg(MultipartFile multipartFile) {
		String multiImg = "/listImg/" + multipartFile.getOriginalFilename();
		
		this.img = multiImg;
	}
	//追記
	public void setImg(String img) {
		this.img = img;
	}
	
//	public String getStringId() {
//		return ;
//	}


}