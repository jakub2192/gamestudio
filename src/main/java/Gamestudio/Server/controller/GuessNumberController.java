package Gamestudio.Server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import Gamestudio.entity.Comment;
import Gamestudio.entity.Score;
import Gamestudio.game.guessNumber.consoleUI.Field;
import Gamestudio.service.CommentService;
import Gamestudio.service.FavoriteService;
import Gamestudio.service.RatingService;
import Gamestudio.service.ScoreService;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class GuessNumberController extends AbstractGameController {
	private Field field;
	private String message;
	private String difficult;
	int inputInt;
	private int finalScore;


	@Autowired
	private ScoreService scoreService;
	@Autowired
	private UserController userController;

	public String getMessage() {
		return message = field.getHint();
	}

	public String getDifficult() {
		return difficult;
	}

	@RequestMapping("/guessNumber")
	public String guessNumber(Model model) {
		field = new Field(100);
		difficult = "1 - 100";
		message = "";
		fillMethod(model);
		return getGameName();
	}

	@RequestMapping("/guessNumber_easy")
	public String guessNumberEasy(Model model) {
		field = new Field(10);
		difficult = "1 - 10";
		fillMethod(model);
		return getGameName();
	}

	@RequestMapping("/guessNumber_hard")
	public String guessNumberHard(Model model) {
		field = new Field(1000);
		difficult = "1 - 1000";
		fillMethod(model);
		return getGameName();
	}


	@RequestMapping("/guessNumber_ask")
	public String guessNumber(@RequestParam(value = "guess", required = false) String guess, Model model) {

		if (!"".equals(guess)) {
			try {
				inputInt = Integer.parseInt(guess);
			} catch (NumberFormatException e) {
				message = "You have to enter number";

			}
			field.procces(inputInt);
			if (userController.isLogged() && field.isSolved()) {
				saveScore();
			}

		}
		fillMethod(model);
		return getGameName();
	}


	public void saveScore() {
		int time = (int) (field.getEndTime() - field.getStartTime()) / 1000;
		if ("1-10".equals(difficult)) {
			finalScore = 300 - time;
		}
		if ("1-100".equals(difficult)) {
			finalScore = 500 - time;
		}
		if ("1-1000".equals(difficult)) {
			finalScore = 1000 - time;
		}
		Score score = new Score();
		score.setGame(getGameName());
		score.setUsername(userController.getLoggedPlayer().getLogin());
		score.setValue(finalScore);
		scoreService.addScore(score);

	}

	@Override
	protected String getGameName() {
		return "guessNumber";
	}

}
