package com.example.chocopage.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.chocopage.entity.Product;
import com.example.chocopage.entity.Review;
import com.example.chocopage.repository.ChocoRepository;

@Service
@Transactional
public class ChocoServiceImpl implements ChocoService {

	@Autowired
	ChocoRepository repository;

	//全件取得
	@Override
	public Iterable<Product> selectAll() {
		return repository.findAll();
	}

	//1件取得
	@Override
	public Optional<Product> selectOneById(Integer id) {
		return repository.findById(id);
	}

	//属性で複数取得→そもそもOptionalは複数取得不可
	@Override
	public Iterable<Product> selectTypeByMaker(Integer maker) {
		return repository.findByMaker(maker);
	}

	@Override
	public Iterable<Product> selectTypeByName(String name) {
		return repository.findByName(name);
	}

	@Override
	public Iterable<Product> selectTypeByTaste(Integer taste) {
		return repository.findByTaste(taste);
	}

	@Override
	public Iterable<Product> selectTypeByKind(Integer kind) {
		return repository.findByKind(kind);
	}
	@Override
	public Iterable<Product> selectAllSortByStar() {
		return repository.allSortByStar();//star
	}

	//TOP画面用（ランダム表示）
	@Override
	public Optional<Product> selectOneRandomProduct() {
		//ランダムでIDの値を取得する
		Integer randId = repository.getRandomId();
		//商品登録がない場合
		if (randId == null) {
			//空のOptionalインスタンスを返す
			return Optional.empty();
		}
		return repository.findById(randId);//crudリポジを継承しているおかげ;
	}

	@Override
	public void insertProduct(Product choco) {
		repository.save(choco);
	}

	@Override
	public void updateProduct(Product choco) {
		repository.save(choco);
	}

	@Override
	public void deleteProductById(Integer id) {
		repository.deleteById(id);
		//レビューテーブル内の個別商品レビュー削除指示も必要
//		repository.deleteReviewTableById(id);
	}
	
	@Override
	public String backKind(Integer kind_id) {
		return repository.backkind(kind_id);
	}
	@Override
	public String backMaker(Integer maker_id) {
		return repository.backmaker(maker_id);
	}
	@Override
	public String backTaste(Integer taste_id) {
		return repository.backtaste(taste_id);
	}
	@Override
	public Iterable<Review> selectAllReview(Integer id) {
		return repository.findReview(id);
	}

}
