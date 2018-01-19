package Gamestudio.Server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import Gamestudio.entity.Score;
import Gamestudio.game.guessNumber.consoleUI.Field;
import Gamestudio.service.ScoreService;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class GuessNumberController extends AbstractGameController {
	private Field field;
	private String message;
	private String difficult;
	private int difficultLevel;
	int inputInt;

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
		difficultLevel= 2;
		message = "";
		fillMethod(model);
		return "game";
	}

	@RequestMapping("/guessNumber_easy")
	public String guessNumberEasy(Model model) {
		field = new Field(10);
		difficult = "1 - 10";
		difficultLevel = 1;
		fillMethod(model);
		return "game";
	}

	@RequestMapping("/guessNumber_hard")
	public String guessNumberHard(Model model) {
		field = new Field(1000);
		difficult = "1 - 1000";
		difficultLevel = 3;
		fillMethod(model);
		return "game";
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
		return "game";
	}

	public String render() {

		StringBuilder sb = new StringBuilder();
			
	sb.append("<div class='pic'>");
	sb.append("	<img src=\"/pictures/guess.jpg\" style=\"visibility: hidden;\" />");
	sb.append("	<form class='frmAsk' action=\"/guessNumber_ask.html\">");
	sb.append("	<input type=\"text\" name=\"guess\" placeholder='Input'");
	sb.append("	autofocus='autofocus' /><br /> <input type=\"submit\"");
	sb.append("	value=\"Guess\" />");
	sb.append(String.format("<p class=\"difficultSize\"> %s</p>" ,difficult ));
	sb.append("	</form>");
	sb.append("	</div>");
	

		sb.append("<div class ='buttons'>");
		sb.append("<button onclick=\"window.location.href='/guessNumber'\">New Game</button>\n");
		sb.append("<button onclick=\"window.location.href='/guessNumber_easy'\">Easy</button>\n");
		sb.append("<button onclick=\"window.location.href='/guessNumber_hard'\">Hard</button>\n");	
		sb.append("</div>\n");
		sb.append("<br/>");
		
		return sb.toString();
	}

	public void saveScore() {
		int finalScore = 0;
		int time = (int) (field.getEndTime() - field.getStartTime()) / 1000;
		if (difficultLevel == 1) {
			finalScore = 300 - time;
		}
		if (difficultLevel == 2) {
			finalScore = 500 - time;
		}
		if (difficultLevel == 3) {
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
