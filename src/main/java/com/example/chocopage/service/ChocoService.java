package com.example.chocopage.service;

import java.util.Optional;

import com.example.chocopage.entity.Product;
import com.example.chocopage.entity.Review;

public interface ChocoService {

	//商品情報を全件取得
	Iterable<Product> selectAll();

	//商品情報を、idをkeyに1件取得
	Optional<Product> selectOneById(Integer id);

	//属性で複数取得→Optionalは1件のみ
	public Iterable<Product> selectTypeByMaker(Integer maker);
	public Iterable<Product> selectTypeByName(String name);
	public Iterable<Product> selectTypeByTaste(Integer taste);
	public Iterable<Product> selectTypeByKind(Integer kind);
	public Iterable<Product> selectAllSortByStar();

	//商品情報を、ランダムで1件取得（今日のおすすめ・用）
	Optional<Product> selectOneRandomProduct();

	//商品情報を登録
	void insertProduct(Product choco);

	//商品情報を更新
	void updateProduct(Product choco);

	//商品情報を削除
	void deleteProductById(Integer id);
	
	String backKind(Integer kind_id);
	String backMaker(Integer maker_id);
	String backTaste(Integer taste_id);

	public Iterable<Review> selectAllReview(Integer id);
}
