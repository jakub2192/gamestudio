package Gamestudio.Server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import Gamestudio.entity.Comment;
import Gamestudio.entity.Favorite;
import Gamestudio.entity.Rating;
import Gamestudio.entity.Score;
import Gamestudio.game.sudoku.model.Game;
import Gamestudio.game.sudoku.view.Field;
import Gamestudio.service.CommentService;
import Gamestudio.service.FavoriteService;
import Gamestudio.service.RatingService;
import Gamestudio.service.ScoreService;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class SudokuController {

	@Autowired
	private ScoreService scoreService;
	@Autowired
	private UserController userController;
	@Autowired
	private RatingService ratingService;
	@Autowired
	private CommentService commentService;
	@Autowired
	private FavoriteService favoriteService;

	private boolean marking;
	private String message;
	private String checkMessage;
	int numberToSet;
	private Game game;
	private Field fields;
	private long startTime;
	private long endTime;

	public String getCheckMessage() {
		return checkMessage;
	}

	public void setCheckMessage(String checkMessage) {
		this.checkMessage = checkMessage;
	}

	public boolean isMarking() {
		return marking;
	}

	public String getMessage() {
		return message;
	}

	public int getNumberToSet() {
		return numberToSet;
	}

	@RequestMapping("/sudoku")
	public String mines(Model model) {
		fields = new Field();
		game = new Game();
		fillMethod(model);
		checkMessage = "";
		return "sudoku";
	}

	@RequestMapping("/addComment_sudoku")
	public String addComment(@RequestParam(value = "content", required = false) String content, Model model) {
		if (!"".equals(content)) {
			commentService.addComment(new Comment(userController.getLoggedPlayer().getLogin(), "sudoku", content));
		}

		fillMethod(model);
		return "/sudoku";
	}

	@RequestMapping("/addRating_sudoku")
	public String addRating(@RequestParam(value = "value", required = false) String value, Model model) {
		int rating = Integer.parseInt(value);
		ratingService.setRating(new Rating(userController.getLoggedPlayer().getLogin(), "sudoku", rating));
		fillMethod(model);
		return "/sudoku";
	}

	@RequestMapping("/sudoku_set")
	public String setValue(@RequestParam(value = "value", required = false) String value, Model model) {
		numberToSet = Integer.parseInt(value);
		game.setSelectedNumber(numberToSet);

		fillMethod(model);
		setCheckMessage("");
		return "/sudoku";
	}

	@RequestMapping("/sudoku_setBack")
	public String setValueBack(Model model) {
		game.setNumber(fields.getFieldX(), fields.getFieldY(), 0);
		fillMethod(model);
		return "/sudoku";
	}

	@RequestMapping("/sudoku_setNumber")
	public String setNumber(@RequestParam(value = "row", required = false) String row,
			@RequestParam(value = "column", required = false) String column, Model model) {
		game.setNumber(Integer.parseInt(row), Integer.parseInt(column), numberToSet);
		fields.setX(Integer.parseInt(row));
		fields.setY(Integer.parseInt(column));
		if (isSolved()) {
			checkMessage = "Solved";
			saveScore();
		}

		fillMethod(model);
		return "/sudoku";
	}

	@RequestMapping("/addFavorite_sudoku")
	public String addFavorite(Model model) {
		favoriteService.setFavorite(new Favorite(userController.getLoggedPlayer().getLogin(), "sudoku"));
		fillMethod(model);

		return "/sudoku";
	}

	public String render() {
		startTime = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder();
		sb.append("<table class='centerSudoku'>\n");
		for (int row = 0; row < 9; row++) {
			sb.append("<tr>\n");
			for (int column = 0; column < 9; column++) {
				int tile = game.getNumber(row, column);

				sb.append("<td>\n");
				if (tile == 0) {
					sb.append(
							String.format("<a class='text' href='/sudoku_setNumber?row=%d&column=%d'>\n", row, column));
				}
				if (!(tile == 0)) {
					sb.append("<font class='tile'>" + tile + "</font>");
				}
				if (tile == 0) {
					sb.append("<font class='tile' style='visibility: hidden;'>" + tile + "</font>");
					sb.append("</a>\n");
				}

				sb.append("</td>\n");
			}
			sb.append("</tr>\n");
		}
		sb.append("</table>\n");

		return sb.toString();
	}

	private void fillMethod(Model model) {
		model.addAttribute("sudokuController", this);
		model.addAttribute("scores", scoreService.getTopScores("sudoku"));
		model.addAttribute("comment", commentService.getComments("sudoku"));
		model.addAttribute("rating", ratingService.getAverageRating("sudoku"));
		if (userController.isLogged()) {
			model.addAttribute("value", ratingService.getValue(userController.getLoggedPlayer().getLogin(), "sudoku"));
			model.addAttribute("favorite",
					favoriteService.isFavorite(userController.getLoggedPlayer().getLogin(), "sudoku"));
		}

	}

	private boolean isSolved() {
		int countNumbers = 0;
		for (int row = 0; row < 9; row++) {
			for (int column = 0; column < 9; column++) {
				if (game.getSolutionNumber(column, row) == game.getNumber(column, row)) {
					countNumbers++;
				}
			}
		}
		if (countNumbers == 81) {
			return true;
		}
		return false;
	}

	private void saveScore() {
		endTime = System.currentTimeMillis();
		int time = (int) (endTime - startTime) / 1000;
		int finalScore = 1000 - time;
		Score score = new Score();
		score.setGame("puzzle");
		score.setUsername(userController.getLoggedPlayer().getLogin());
		score.setValue(finalScore);
		scoreService.addScore(score);

	}

}
