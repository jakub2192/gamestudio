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
import Gamestudio.game.puzzle.core.Field;
import Gamestudio.service.CommentService;
import Gamestudio.service.FavoriteService;
import Gamestudio.service.RatingService;
import Gamestudio.service.ScoreService;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class puzzleController extends AbstractGameController {
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
	Field field;
	private int difficult;
	private int finalScore;
	private String currentGame = "puzzle";
	private String message = "";

	public String getMessage() {
		return message;
	}

	@RequestMapping("/puzzle")
	public String puzzle(@RequestParam(value = "tile", required = false) String tile, Model model) {
		int tile2;
		try {
			tile2 = Integer.parseInt(tile);
			field.moveTile(tile2);

			if (field.isSolved()) {
				message = "Solved";
				if (userController.isLogged()) {
					saveScore();
				}

			}
		} catch (NumberFormatException e) {
			field = new Field(4, 4);
			difficult = 2;
			message = "";
		}

		fillMethod(model);
		return currentGame;
	}

	@RequestMapping("/addComment_puzzle")
	public String addComment(@RequestParam(value = "content", required = false) String content, Model model) {
		if (!"".equals(content)) {
			commentService.addComment(new Comment(userController.getLoggedPlayer().getLogin(), currentGame, content));
		}

		fillMethod(model);
		return currentGame;
	}

	@RequestMapping("/addRating_puzzle")
	public String addRating(@RequestParam(value = "value", required = false) String value, Model model) {
		int rating = Integer.parseInt(value);
		ratingService.setRating(new Rating(userController.getLoggedPlayer().getLogin(), currentGame, rating));
		fillMethod(model);
		return currentGame;
	}

	@RequestMapping("/addFavorite_puzzle")
	public String addFavorite(Model model) {
		favoriteService.setFavorite(new Favorite(userController.getLoggedPlayer().getLogin(), currentGame));
		fillMethod(model);
		return currentGame;
	}
	@RequestMapping("/puzzle_easy")
	public String mines_easy(Model model) {
		field = new Field(3, 3);
		fillMethod(model);
		difficult = 1;
		return currentGame;
	}

	@RequestMapping("/puzzle_hard")
	public String mines_hard(Model model) {
		field = new Field(5, 5);
		fillMethod(model);
		difficult = 3;
		return currentGame;
	}

	public String render() {
		StringBuilder sb = new StringBuilder();
		sb.append("<table class='centerPuzzle'>\n");
		for (int row = 0; row < field.getRowCount(); row++) {
			sb.append("<tr>\n");
			for (int column = 0; column < field.getColumnCount(); column++) {
				int tile = field.getTile(row, column);
				sb.append("<td>\n");
				if (!field.isSolved()) {
					sb.append(String.format("<a class='text' href='/puzzle?tile=%d'>\n", tile));
				}
				if (tile > 0) {
					sb.append("<font class='tile'>" + tile + "</font>");
				}
				if (!field.isSolved()) {
					sb.append("</a>\n");
				}

				sb.append("</td>\n");
			}
			sb.append("</tr>\n");
		}
		sb.append("</table>\n");

		return sb.toString();
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
			score.setGame(currentGame);
			score.setUsername(userController.getLoggedPlayer().getLogin());
			score.setValue(finalScore);
			scoreService.addScore(score);
		}
	}

	@Override
	protected String getGameName() {
		return "puzzle";
	}
}
