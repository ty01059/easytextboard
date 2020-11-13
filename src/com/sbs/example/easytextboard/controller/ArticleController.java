package com.sbs.example.easytextboard.controller;

import java.util.ArrayList;
import java.util.Scanner;

import com.sbs.example.easytextboard.container.Container;
import com.sbs.example.easytextboard.dto.Article;
import com.sbs.example.easytextboard.dto.Board;
import com.sbs.example.easytextboard.dto.Member;
import com.sbs.example.easytextboard.service.ArticleService;
import com.sbs.example.easytextboard.service.MemberService;

public class ArticleController extends Controller {
	private Scanner sc;
	private ArticleService articleService;
	private MemberService memberService;

	public ArticleController() {
		articleService = Container.articleService;
		memberService = Container.memberService;
		sc = Container.scanner;

	}

	public void doCommand(String command) {

		// article add
		if (command.equals("article add")) {
			doAdd();
		}
		// article list
		else if (command.startsWith("article list")) {
			showList(command);
		}

		// article detail
		else if (command.startsWith("article detail")) {
			showDetail(command);
		}
		// article delete
		else if (command.startsWith("article delete")) {
			doDelete(command);
		}
		// article modify
		else if (command.startsWith("article modify")) {
			doModify(command);
		}
		// article search
		else if (command.startsWith("article search")) {
			showSearchList(command);
		}
		// article makeBoard
		else if (command.equals("article makeBoard")) {
			doMakeBoard(sc);
		}
		// article selectBoard
		else if (command.startsWith("article selectBoard")) {
			doSelectBoard(command);
		}
		// article currentBoard
		else if (command.equals("article currentBoard")) {
			showCurrentBoard();
		} else {
			System.out.println("존재하지 않는 명령어");
		}

	}

	private void showCurrentBoard() {
		Board board = null;
		board = articleService.getBoardById(Container.session.getSelectBoardId());

		System.out.printf("현재 %s(%d번) 게시판입니다.\n", board.getBoardName(), board.getBoardId());

	}

	// doSelectBoard
	private void doSelectBoard(String command) {
		Board board = null;
		String[] commands = command.split(" ");
		int id = 0;
		try {
			id = Integer.parseInt(commands[2]);
		} catch (NumberFormatException e) {
			System.out.println("게시판 번호는 숫자로 입력해야 합니다.");
			return;
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("게시판 번호를 입력해야 합니다.");
			return;
		}

		board = articleService.getBoardById(id);
		Container.session.setSelectBoardId(board.getBoardId());

		System.out.printf("%s(%d번) 게시판이 선택되었습니다.\n", board.getBoardName(), board.getBoardId());

	}

	// showSearchList
	private void showSearchList(String command) {

		String[] commands = command.split(" ");
		int pageNumber = 0;
		ArrayList<Article> searchArticles = new ArrayList<>();

		searchArticles = articleService.getArticlesByKeyword(commands[2]);
		if (searchArticles.size() == 0) {
			System.out.println("검색 결과가 없습니다.");
			return;
		}
		try {
			pageNumber = Integer.parseInt(commands[3]);
		} catch (NumberFormatException e) {
			System.out.println("페이지는 숫자로 입력해 주세요.");
			return;
		} catch (ArrayIndexOutOfBoundsException e) {
			pageNumber = 1;
			int itemsInAPage = 10;
			int start = 0;
			start = (pageNumber - 1) * itemsInAPage;
			int end = start + itemsInAPage;
			if (end >= searchArticles.size()) {
				end = searchArticles.size() - 1;
			}
			System.out.println("== 게시물 리스트 ==");
			System.out.println("번호 / 작성날짜 / 작성자 / 제목 / 내용 / 조회수");

			for (int i = start; i <= end; i++) {
				Member member = null;
				member = memberService.getMemberById(searchArticles.get(i).getWriteMemberId());
				int id = searchArticles.get(i).getId();
				String regDate = searchArticles.get(i).getRegDate();
				String name = member.getName();
				String title = searchArticles.get(i).getTitle();
				String body = searchArticles.get(i).getBody();
				int hit = searchArticles.get(i).getHit();
				System.out.printf("%d / %s / %s / %s / %s / %d\n", id, regDate, name, title, body, hit);
			}

			return;
		}

		if (searchArticles.size() == 0) {
			System.out.println("검색 결과가 없습니다.");
			return;
		}
		if ((pageNumber - 1) * 10 >= searchArticles.size()) {
			System.out.println("존재하지 않는 페이지입니다.");
			return;
		}

		int itemsInAPage = 10;
		int start = 0;
		start = (pageNumber - 1) * itemsInAPage;
		int end = start + itemsInAPage;
		if (end >= searchArticles.size()) {
			end = searchArticles.size() - 1;
		}
		System.out.println("== 게시물 리스트 ==");
		System.out.println("번호 / 작성날짜 / 작성자 / 제목 / 내용 / 조회수");

		for (int i = start; i <= end; i++) {
			Member member = null;
			member = memberService.getMemberById(searchArticles.get(i).getWriteMemberId());
			int id = searchArticles.get(i).getId();
			String regDate = searchArticles.get(i).getRegDate();
			String name = member.getName();
			String title = searchArticles.get(i).getTitle();
			String body = searchArticles.get(i).getBody();
			int hit = searchArticles.get(i).getHit();
			System.out.printf("%d / %s / %s / %s / %s / %d\n", id, regDate, name, title, body, hit);
		}

	}

	// doModify
	private void doModify(String command) {
		String[] commands = command.split(" ");
		int articleId = 0;
		try {
			articleId = Integer.parseInt(commands[2]);
		} catch (NumberFormatException e) {
			System.out.println("게시물 번호는 숫자로 입력해 주세요.");
			return;
		} catch (Exception e) {
			System.out.println("게시물 번호를 입력해 주세요.");
			return;
		}
		Article article = articleService.getArticleById(articleId);

		if (article == null) {
			System.out.println("존재하지 않는 게시물입니다.");
			return;
		}

		System.out.printf("새 제목 : ");
		String title = sc.nextLine();

		System.out.printf("새 내용 : ");
		String body = sc.nextLine();

		articleService.doModify(articleId, title, body);
		System.out.printf("%d번 게시물이 수정되었습니다.\n", articleId);

	}

	// doDelete
	private void doDelete(String command) {

		String[] commands = command.split(" ");
		int articleId = 0;
		try {
			articleId = Integer.parseInt(commands[2]);
		} catch (NumberFormatException e) {
			System.out.println("게시물 번호는 숫자로 입력해 주세요.");
			return;
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("게시물 번호를 입력해 주세요.");
			return;
		}

		Article article = articleService.getArticleById(articleId);
		if (article == null) {
			System.out.println("존재하지 않는 게시물입니다.");
			return;
		}

		articleService.doDelete(articleId);
		System.out.printf("%d번 게시물이 삭제되었습니다.\n", articleId);

	}

	// showDetail
	private void showDetail(String command) {
		Article article = null;
		Member member = null;
		String[] commands = command.split(" ");
		int articleId = 0;

		try {
			articleId = Integer.parseInt(commands[2]);
		} catch (NumberFormatException e) {
			System.out.println("게시물 번호는 숫자로 입력해 주세요.");
			return;
		} catch (Exception e) {
			System.out.println("게시물 번호를 입력해 주세요.");
			return;
		}

		article = articleService.getArticleById(articleId);
		member = memberService.getMemberById(article.getWriteMemberId());

		System.out.println("== 게시물 상세 ==");
		System.out.printf("번호 : %d\n", article.getId());
		System.out.printf("작성일 : %s\n", article.getRegDate());
		System.out.printf("작성자 : %s\n", member.getName());
		System.out.printf("제목 : %s\n", article.getTitle());
		System.out.printf("내용 : %s\n", article.getBody());
		System.out.printf("조회수 : %d\n", article.getHit());

	}

	// showList
	private void showList(String cmd) {
		ArrayList<Article> articles = null;
		articles = articleService.getArticles();

		String[] commands = cmd.split(" ");
		int pageNumber = 0;
		try {
			pageNumber = Integer.parseInt(commands[2]);
		} catch (NumberFormatException e) {
			System.out.println("페이지는 숫자로 입력해야 합니다.");
			return;
		} catch (ArrayIndexOutOfBoundsException e) {
			pageNumber = 1;
		}
		int itemsInAPage = 10;
		int start = 0;
		start = (pageNumber - 1) * itemsInAPage;
		int end = start + itemsInAPage;
		if (end >= articles.size()) {
			end = articles.size() - 1;
		}

		System.out.println("== 게시물 리스트 ==");
		System.out.println("번호 / 작성날짜 / 작성자 / 제목 / 내용 / 조회수");

		for (int i = start; i <= end; i++) {
			Member member = null;
			member = memberService.getMemberById(articles.get(i).getWriteMemberId());
			int id = articles.get(i).getId();
			String regDate = articles.get(i).getRegDate();
			String name = member.getName();
			String title = articles.get(i).getTitle();
			String body = articles.get(i).getBody();
			int hit = articles.get(i).getHit();
			System.out.printf("%d / %s / %s / %s / %s / %d\n", id, regDate, name, title, body, hit);
		}

	}

	// doAdd
	private void doAdd() {
		if (!Container.session.isLogined()) {
			System.out.println("로그인 후 이용해 주세요.");
			return;
		}
		System.out.println("== 게시물 등록 ==");
		System.out.printf("제목 : ");
		String title = sc.nextLine();
		System.out.printf("내용 : ");
		String body = sc.nextLine();

		int number = articleService.doAdd(title, body, Container.session.getLoginedId(),
				Container.session.getSelectBoardId());

		System.out.printf("%d번 게시물이 등록되었습니다.\n", number);
	}

	// doMakeBoard
	private void doMakeBoard(Scanner sc) {
		Board board = new Board();

		System.out.println("== 게시판 생성 ==");
		System.out.printf("게시판 이름 : ");
		String name = sc.nextLine();

		board = articleService.getBoardByName(name);
		if (board != null) {
			System.out.println("이미 존재하는 게시판입니다.");
			return;
		}

		int number = articleService.doMakeBoard(name);
		System.out.printf("%s(%d번) 게시판이 생성되었습니다.\n", name, number);
	}

}
