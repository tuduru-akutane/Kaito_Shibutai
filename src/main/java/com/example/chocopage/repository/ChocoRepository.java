package com.example.chocopage.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.chocopage.entity.Product;
import com.example.chocopage.entity.Review;

//Chocoテーブル：RepositoryImpl
public interface ChocoRepository extends CrudRepository<Product, Integer> {

	@Query("SELECT id FROM product ORDER BY RANDOM() limit 1")
	Integer getRandomId();

	@Query("SELECT * FROM product JOIN (SELECT * FROM maker_tb WHERE maker_id = :maker) AS maker_tb ON product.maker = maker_tb.maker_id")
	Iterable<Product> findByMaker(Integer maker);

	@Query("SELECT * FROM product WHERE name = :name")
	Iterable<Product> findByName(String name);

	@Query("SELECT * FROM product JOIN (SELECT * FROM taste_tb WHERE taste_id = :taste) AS taste_tb ON product.taste = taste_tb.taste_id")
	Iterable<Product> findByTaste(Integer taste);

	@Query("SELECT * FROM product JOIN (SELECT * FROM kind_tb WHERE kind_id = :kind ) AS kind_tb ON product.kind = kind_tb.kind_id")
	Iterable<Product> findByKind(Integer kind);

	@Query("SELECT product.name, product.img, AVG(COALESCE(review_tb.star,0.0))AS avg FROM product JOIN review_tb ON product.id = review_tb.product_id GROUP BY product.id , review_tb.product_id ORDER BY AVG(COALESCE(review_tb.star,0.0)) DESC")
	Iterable<Product> allSortByStar();
	
	@Query("SELECT name_kind FROM product JOIN (SELECT * FROM kind_tb) AS kind_tb ON product.kind = kind_tb.kind_id where id=:id")
	String backkind(Integer id);
	
	@Query("SELECT name_maker FROM product JOIN (SELECT * FROM maker_tb) AS maker_tb ON product.maker = maker_tb.maker_id where id=:id")
	String backmaker(Integer id);
	
	@Query("SELECT name_taste FROM product JOIN (SELECT * FROM taste_tb) AS taste_tb ON product.taste = taste_tb.taste_id where id=:id")
	String backtaste(Integer id);
	
	@Query("SELECT author,star,review_info FROM product JOIN (SELECT * FROM review_tb WHERE product_id = :id ) AS review_tb ON product.id = review_tb.product_id")
	Iterable<Review> findReview(Integer id);
	
}
