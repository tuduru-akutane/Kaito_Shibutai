package com.example.chocopage.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.chocopage.entity.Product;
import com.example.chocopage.entity.Review;
import com.example.chocopage.form.ChocoForm;
import com.example.chocopage.form.ChocoSrchForm;
import com.example.chocopage.service.ChocoService;

@Controller
@RequestMapping("/choco")
public class ChocoPageController {
	//DI対象
	@Autowired
	ChocoService service;

	//「form-backing bean」の初期化???→必要
	@ModelAttribute
	public ChocoForm setUpForm() {
		ChocoForm chocoForm = new ChocoForm();
		return chocoForm;
	}

	//TOP(menu)を表示
	@GetMapping
	public String showTOP(Model model, ChocoForm chocoForm) {
		//Productを取得(Optionalでラップ)
		Optional<Product> chocoOpt = service.selectOneRandomProduct();
		//値が入っているか判定する
		if (chocoOpt.isPresent()) {
			//ChocoFormへのつめなおし
			Optional<ChocoForm> chocoFormOpt = chocoOpt.map(t -> makeChocoForm(t));
			chocoForm = chocoFormOpt.get();
		} else {
			model.addAttribute("msg", "商品登録がありません。");
			return "menu";
		}
		//表示用「Model」への格納
		model.addAttribute("chocoForm", chocoForm);
		return "menu";
	}

	//新規登録画面を表示
	@GetMapping("/new")
	public String showNew(Model model, ChocoForm chocoForm) {
		return "chocoNew";
	}

	//新規登録画面にてProductデータを一件追加
	@PostMapping("/new/insert")
	public String insert(@Validated ChocoForm chocoForm, BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {
		//FormからEntityへの詰め替え
		Product choco = new Product();
		choco.setId(chocoForm.getId());
		choco.setMaker(chocoForm.getMaker());
		choco.setKind(chocoForm.getKind());
		choco.setName(chocoForm.getName());
		choco.setTaste(chocoForm.getTaste());
		//ここで画像を保存する処理 & ファイルパスのString=img確保が必須
		String img = uploadAction(chocoForm.getMultipartFile());
		//ここで画像のファイルパスを保存する処理
		//＝Entityの保持＝String、フォームの保持＝MultipartFile
		choco.setImg(img);
		choco.setLink(chocoForm.getLink());
		//		//入力チェック
		if ( (!bindingResult.hasErrors()) && !img.equals("\\listImg")) {
			service.insertProduct(choco);
			//			model.addAttribute("complete","登録が完了しました");
			return showTOP(model, chocoForm);//TOPに戻りたい
		} else {
			//入力チェックされない場合は表示更新してリダイレクト
			redirectAttributes.addFlashAttribute("fault", "名前または画像欄が空白です。");
			return "redirect:/choco/new";
		}
	}

	/**
	 * アップロード実行処理
	 * @param multipartFile
	 */
	private String uploadAction(MultipartFile multipartFile) {
		//ファイル名取得
		String fileName = multipartFile.getOriginalFilename();
		//格納先のフルパス
		Path filePath = Paths.get(
				"C:/pleiades/2023-03/workspace/ChocoPage/src/main/resources/static/listImg/" + fileName);
		try {
			//アップロードファイルをバイト値に変換
			byte[] bytes = multipartFile.getBytes();
			//バイト値を書き込む為のファイルを作成して指定したパスに格納
			OutputStream stream = Files.newOutputStream(filePath);
			//ファイルに書き込み
			stream.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//↓超重要！これを送信　およびDBに保持
		Path filePathShort = Paths.get("/listImg/" + fileName);
		return filePathShort.toString();
	}

	//Productの一覧を表示
	@GetMapping("/choco-list")
	public String showList(Model model) {
		//検索欄表示
		ChocoSrchForm chocoSrchForm = new ChocoSrchForm();
		model.addAttribute("chocoSrchForm", chocoSrchForm);

		//商品の一覧を取得する
		Iterable<Product> list = service.selectAll();
		//表示用「Model」への格納
		model.addAttribute("list", list);
		return "chocoList";
	}

	//Productの検索結果を表示
	@PostMapping("/choco-list/search")
	public String showSrchList(Model model, ChocoSrchForm chocoSrchForm) {
		
		//検索結果表示、選択がある場合は
		//!=nullの検索条件に対して商品の一覧を取得する（単属性検索のみ）
		Iterable<Product> slist = null;
		if (null != chocoSrchForm.getMaker()) {
			slist = service.selectTypeByMaker(chocoSrchForm.getMaker());
			//		} else if (null != chocoSrchForm.getName()) {
			//			list = service.selectTypeByName(chocoSrchForm.getName());
		} else if (null != chocoSrchForm.getTaste()) {
			slist = service.selectTypeByTaste(chocoSrchForm.getTaste());
		} else if (null != chocoSrchForm.getKind()) {
			slist = service.selectTypeByKind(chocoSrchForm.getKind());
		}
		//表示用「Model」への格納
		model.addAttribute("slist", slist);

		return "chocoList";
	}

	//画像押した段階でProductデータを1件取得し、モデル内に表示する
	@GetMapping("/choco-list/{id}") //DBへのアクセス指示
	public String showInfo(ChocoForm chocoForm, @PathVariable Integer id, Model model) {
		//Productを取得（Optionalでラップ）
		Optional<Product> chocoOpt = service.selectOneById(id);
		//ChocoFormへの詰めなおし準備(詳細不詳！)
		Optional<ChocoForm> chocoFormOpt = chocoOpt.map(choco -> makeChocoForm(choco));//２つ下↓
		//ChocoFormがnullでなければ(値が存在すれば)値を取り出す
		if (chocoFormOpt.isPresent()) {
			chocoForm = chocoFormOpt.get();
		}
		model.addAttribute("kindSt", service.backKind( id ));//商品idからkind列Integerを検索＆入力
		model.addAttribute("makerSt", service.backMaker( id ));
		model.addAttribute("tasteSt", service.backTaste( id ));

		Iterable<Review>  review = service.selectAllReview(id);
		model.addAttribute("review",review);
		
		//更新用のModelを作成する
		makeUpdateModel(chocoForm, model);//すぐ↓のメソッド
		return "chocoInfo";
	}

	//更新用のModelを作成する
	private void makeUpdateModel(ChocoForm chocoForm, Model model) {
		model.addAttribute("id", chocoForm.getId());
		model.addAttribute("chocoForm", chocoForm);
	}

	//編集画面→update か deleteに分岐
	@GetMapping("/choco-list/{id}/edit")
	public String showEdit(ChocoForm chocoForm, @PathVariable Integer id, Model model) {
		//更新用のModelを作成する
		makeUpdateModel(chocoForm, model);//すぐ↑のメソッド
		return "chocoEdit";
	}

	//idをkeyにしてデータを更新する
	@PostMapping("/choco-list/{id}/update")
	public String update(
			@Validated ChocoForm chocoForm,
			BindingResult result,
			Model model,
			RedirectAttributes redirectAttributes,
			@PathVariable Integer id) {
		//ChocoFormからProductにつめなおす
		Product choco = makeChoco(chocoForm, id);

		//入力チェック
		if (!result.hasErrors()) {
			//更新処理、フラッシュスコープの利用、リダイレクト（個々の編集ページ）
			service.updateProduct(choco);
			//			redirectAttributes.addFlashAttribute("complete", "更新が完了しました");
			//更新画面を表示する
			return "redirect:/choco/choco-list/" + choco.getId() + "/edit";
		} else {
			//更新用のModelを作成する
			//			makeUpdateModel(chocoForm, model);
			return "chocoList";
		}
	}

	/**
	 * !更新!実行処理
	 * @param multipartFile
	 * ★以前の画像(内部データ)上書き処理が必要
	 */
	private String uploadReAction(MultipartFile multipartFile, String shortpath) {
		//ファイル名取得
		//		String fileName = multipartFile.getOriginalFilename();
		String fileName = shortpath;//.substring(9)
		//格納先のフルパス ※事前に格納先フォルダ「UploadTest」をCドライブ直下に作成しておく
		Path filePath = Paths.get(
				"C:/pleiades/2023-03/workspace/ChocoPage/src/main/resources/static/listImg/" + fileName);
		try {
			//アップロードファイルをバイト値に変換
			byte[] bytes = multipartFile.getBytes();
			//バイト値を書き込む為のファイルを作成して指定したパスに格納
			OutputStream stream = Files.newOutputStream(filePath);
			//ファイルに書き込み
			stream.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//↓超重要！これを送信
		Path filePathShort = Paths.get("/listImg/" + fileName);
		return filePathShort.toString();
	}

	//製品登録//form→entity
	private Product makeChoco(ChocoForm chocoForm, Integer id) {
		Product choco = new Product();
		choco.setId(chocoForm.getId());
		choco.setName(chocoForm.getName());
		choco.setMaker(chocoForm.getMaker());
		choco.setKind(chocoForm.getKind());
		choco.setTaste(chocoForm.getTaste());

		//この書き位置ではchocoFormを上書きしてしまうので BAD
//		Optional<Product> chocoOpt = service.selectOneById(id);
//		//ChocoFormへの詰めなおし準備(詳細不詳！)
//		Optional<ChocoForm> chocoFormOpt = chocoOpt.map(c -> makeChocoForm(c));//２つ下↓
//		//ChocoFormがnullでなければ(値が存在すれば)値を取り出す
//		if (chocoFormOpt.isPresent()) {
//			chocoForm = chocoFormOpt.get();
//		}

		//Form内にMultipartFile以外にも変数imgが必要では？
		String s = uploadReAction(chocoForm.getMultipartFile(), id+".jpg");
		choco.setImg( s );

		choco.setLink(chocoForm.getLink());
		return choco;
	}

	//製品登録フォーム//entity→form
	private ChocoForm makeChocoForm(Product choco) {
		ChocoForm chocoForm = new ChocoForm();
		chocoForm.setId(choco.getId());
		chocoForm.setName(choco.getName());
		chocoForm.setMaker(choco.getMaker());
		chocoForm.setKind(choco.getKind());
		chocoForm.setTaste(choco.getTaste());
		//		↓こっちで妥協
		chocoForm.setImg(choco.getImg());
		//		↓ファイル形式は正しい？→エンティティ側はMultipartFileでなくimgしか不可能
		//		chocoForm.setMultipartFile(choco.getImg());
		chocoForm.setLink(choco.getLink());

		return chocoForm;
	}

	//idをkeyにしてデータを削除する
	@PostMapping("/choco-list/{id}/delete")
	public String delete(
			@PathVariable Integer id,
			Model model,
			RedirectAttributes redirectAttributes) {
		//プロダクトを1件削除して一覧画面にリダイレクト
		service.deleteProductById(id);
		//chocoListのトップに削除completeを記述する
		//		redirectAttributes.addFlashAttribute("complete", "削除が完了しました");
		return showList(model);
	}

	//Productの一覧をレビュー順ソートして表示
	@GetMapping("/ranking")
	public String showRank(Model model, ChocoForm chocoForm) {
		//商品のレビューソート一覧を取得する
		Iterable<Product> list = service.selectAllSortByStar();
		//表示用「Model」への格納→この段階ではサムネイル表示されないはず
		model.addAttribute("list", list);
		return "chocoRank";
	}

}
