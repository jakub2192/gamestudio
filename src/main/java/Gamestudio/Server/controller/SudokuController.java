package Gamestudio.Server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import Gamestudio.entity.Score;
import Gamestudio.game.sudoku.model.Game;
import Gamestudio.game.sudoku.view.Field;
import Gamestudio.service.ScoreService;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class SudokuController extends AbstractGameController {

	@Autowired
	private ScoreService scoreService;
	@Autowired
	private UserController userController;

	private boolean marking;
	private String message;

	int numberToSet;
	private Game game;
	private Field fields;
	private long startTime;
	private long endTime;

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
		message = "";
		   numberToSet = 0;
		return  "game";
	}

	@RequestMapping("/sudoku_set")
	public String setValue(@RequestParam(value = "value", required = false) String value, Model model) {
		numberToSet = Integer.parseInt(value);
		game.setSelectedNumber(numberToSet);
 
		fillMethod(model);
		message="";
		return "game";
	}

	@RequestMapping("/sudoku_setBack")
	public String setValueBack(Model model) {
		game.setNumber(fields.getFieldX(), fields.getFieldY(), 0);
		fillMethod(model);
		return "game";
	}

	@RequestMapping("/sudoku_setNumber")
	public String setNumber(@RequestParam(value = "row", required = false) String row,
			@RequestParam(value = "column", required = false) String column, Model model) {
		game.setNumber(Integer.parseInt(row), Integer.parseInt(column), numberToSet);
		fields.setX(Integer.parseInt(row));
		fields.setY(Integer.parseInt(column));
		if (isSolved()) {
			message = "Solved";
			saveScore();
		}

		fillMethod(model);
		return "game";
	}

	public String render() {
		startTime = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder();

		sb.append("<div class='buttons'>");
		sb.append("<button onclick=\"window.location.href='/sudoku_setBack'\">Step Back</button>");
		sb.append("</div>");
		
		sb.append("<table class='centerSudoku'>\n");
		sb.append("<tr>\n");
		for (int number = 1; number < 10; number++) {
			sb.append("<td>\n");
			sb.append(String.format("<a href='/sudoku_set?value=%d'>" +number + "</a>",number));
			sb.append("</td>");
		}
		sb.append("</tr>\n");
		sb.append("</table>\n");
		
		
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
		
		sb.append("<div class ='buttons'>");
		sb.append("<button onclick=\"window.location.href='/sudoku'\">New Game</button>\n");	
		sb.append("</div>\n");
		sb.append("<br/>");

		return sb.toString();
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
		if (userController.isLogged()) {
			endTime = System.currentTimeMillis();
			int time = (int) (endTime - startTime) / 1000;
			int finalScore = 1000 - time;
			Score score = new Score();
			score.setGame(getGameName());
			score.setUsername(userController.getLoggedPlayer().getLogin());
			score.setValue(finalScore);
			scoreService.addScore(score);
		}
	}

	@Override
	protected String getGameName() {
		return "sudoku";
	}

}
