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
import Gamestudio.game.minesweeper.core.Clue;
import Gamestudio.game.minesweeper.core.Field;
import Gamestudio.game.minesweeper.core.GameState;
import Gamestudio.game.minesweeper.core.Tile;
import Gamestudio.game.minesweeper.core.TileState;
import Gamestudio.service.CommentService;
import Gamestudio.service.FavoriteService;
import Gamestudio.service.RatingService;
import Gamestudio.service.ScoreService;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class MinesController extends AbstractGameController {
	private Field field;
	@Autowired
	private ScoreService scoreService;
	@Autowired
	private UserController userController;
	private boolean marking;
	protected String message;
	private int difficult;
	private int finalScore;

	public boolean isMarking() {
		return marking;
	}

	public String getMessage() {
		return message;
	}

	@RequestMapping("/mines_mark")
	public String mines(Model model) {
		marking = !marking;
		fillMethod(model);
		return getGameName();
	}

	@RequestMapping("/mines")
	public String mines(@RequestParam(value = "row", required = false) String row,
			@RequestParam(value = "column", required = false) String column, Model model) {
		processCommand(row, column);
		fillMethod(model);
		return getGameName();
	}

	@RequestMapping("/mines_easy")
	public String mines_easy(Model model) {
		field = new Field(5, 5, 5);
		fillMethod(model);
		difficult = 1;
		return getGameName();
	}

	@RequestMapping("/mines_hard")
	public String mines_hard(Model model) {
		field = new Field(15, 15, 20);
		fillMethod(model);
		difficult = 3;
		return getGameName();
	}
	public String render() {

		StringBuilder sb = new StringBuilder();
		sb.append("<table class='center'>\n");
		for (int row = 0; row < field.getRowCount(); row++) {
			sb.append("<tr>");
			for (int column = 0; column < field.getColumnCount(); column++) {
				Tile tile = field.getTile(row, column);
				String image = "closed";
				switch (tile.getState()) {
				case CLOSED:
					image = "closed";
					break;
				case MARKED:
					image = "marked";
					break;
				case OPEN:
					if (tile instanceof Clue) {
						image = "open" + ((Clue) tile).getValue();
					} else
						image = "mine";

				}

				sb.append("<td>\n");
				if (tile.getState().equals(TileState.OPEN) || !(field.getState() == GameState.PLAYING)) {
					sb.append("<img src='/images/mines/" + image + ".png' height=\"25\" width=\"25\">");

				} else {
					sb.append(String.format("<a href='/mines?row=%d&column=%d'>\n", row, column));
					sb.append("<img src='/images/mines/" + image + ".png' height=\"25\" width=\"25\">");
					sb.append("</a>\n");
				}
				sb.append("</td>\n");
			}
			sb.append("</tr>\n");
		}

		sb.append("</table>\n");
		return sb.toString();
	}

	private void createField() {
		field = new Field(9, 9, 9);
		difficult = 3;
		message = "";
	}

	private void processCommand(String row, String column) {
		try {
			if (marking) {
				field.markTile(Integer.parseInt(row), Integer.parseInt(column));
			} else {
				field.openTile(Integer.parseInt(row), Integer.parseInt(column));
			}
			if (field.getState() == GameState.FAILED) {
				message = "Failed";
			} else if (field.getState() == GameState.SOLVED) {
				if (userController.isLogged()) {
					saveScore();
				}
				message = "Solved";

			}
		} catch (NumberFormatException e) {
			createField();
		}
	}

	private void saveScore() {
		if (field.isSolved()) {
			int time = (int) (field.getEndTime() - field.getStartTime()) / 1000;
			if (difficult == 1) {
				finalScore = 300 - time;
			}
			if (difficult == 2) {
				finalScore = 500 - time;
			}
			if (difficult == 3) {
				finalScore = 1000 - time;
			}
			Score score = new Score();
			score.setGame(getGameName());
			score.setUsername(userController.getLoggedPlayer().getLogin());
			score.setValue(finalScore);
			scoreService.addScore(score);
		}
	}

	@Override
	protected String getGameName() {
		return "mines";
	}

}
