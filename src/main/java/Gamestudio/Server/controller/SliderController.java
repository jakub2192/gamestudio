package Gamestudio.Server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import Gamestudio.entity.Score;
import Gamestudio.game.puzzle.core.Field;
import Gamestudio.service.CommentService;
import Gamestudio.service.FavoriteService;
import Gamestudio.service.RatingService;
import Gamestudio.service.ScoreService;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class SliderController extends AbstractGameController {
	@Autowired
	private ScoreService scoreService;
	@Autowired
	private UserController userController;

	Field field;
	private int difficult;
	private int finalScore;
	private String message = "";

	public String getMessage() {
		return message;
	}

	@RequestMapping("/slider")
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
		return "game";
	}

	public String render() {
		StringBuilder sb = new StringBuilder();
		sb.append("<table class='centerSlider'>\n");
		for (int row = 0; row < field.getRowCount(); row++) {
			sb.append("<tr>\n");
			for (int column = 0; column < field.getColumnCount(); column++) {
				int tile = field.getTile(row, column);
				sb.append("<td>\n");
				if (!field.isSolved()) {
					sb.append(String.format("<a class='text' href='/slider?tile=%d'>\n", tile));
					if (tile > 0) {
						sb.append("<img class='tile' src='/pictures.puzzle/image" + tile
								+ ".jpg' height=\"75\" width=\"75\"></img>");
					}
					if (tile == 0) {
						sb.append(
								"<img class='tile' src='/pictures.puzzle/empty.jpg' height=\"75\" width=\"75\"></img>");
					}

				} else {
					sb.append("<img class='tile' src='/pictures.puzzle/image" + tile
							+ ".jpg' height=\"75\" width=\"75\"></img>");
				}

				if (!field.isSolved()) {
					sb.append("</a>\n");
				}
				sb.append("</td>\n");
			}
			sb.append("</tr>\n");
		}
		sb.append("</table>\n");

		sb.append("<br/>");

		sb.append("<div class ='buttons'>");
		sb.append("<button onclick=\"window.location.href='/slider'\">New Game</button>\n");

		sb.append("</button>\n");
		sb.append("</div>\n");

		return sb.toString();
	}

	private void saveScore() {
		if (field.isSolved()) {
			int time = (int) (field.getEndTime() - field.getStartTime()) / 1000;

			finalScore = 1000 - time;

			Score score = new Score();
			score.setGame(getGameName());
			score.setUsername(userController.getLoggedPlayer().getLogin());
			score.setValue(finalScore);
			scoreService.addScore(score);
		}
	}

	@Override
	protected String getGameName() {
		return "slider";
	}
}
