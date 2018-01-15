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
import Gamestudio.game.guessNumber.consoleUI.Field;
import Gamestudio.service.CommentService;
import Gamestudio.service.FavoriteService;
import Gamestudio.service.RatingService;
import Gamestudio.service.ScoreService;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class GuessNumberController {
	private Field field;
	private String message;
	private String difficult;
	private int difficultScore;
	int inputInt;
	private int finalScore;
	
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
		difficultScore = 2;
		return "guessNumber";
	}

	@RequestMapping("/guessNumber_easy")
	public String guessNumberEasy(Model model) {
		field = new Field(10);
		difficult = "1 - 10";
		fillMethod(model);
		difficultScore = 1;
		return "guessNumber";
	}

	@RequestMapping("/guessNumber_hard")
	public String guessNumberHard(Model model) {
		field = new Field(1000);
		difficult = "1 - 1000";
		fillMethod(model);
		difficultScore = 3;
		return "guessNumber";
	}

	@RequestMapping("/addComment_guess")
	public String addComment(@RequestParam(value = "content", required = false) String content, Model model) {

		if (!"".equals(content)) {
			commentService.addComment(new Comment(userController.getLoggedPlayer().getLogin(), "guessNumber", content));
		}
		fillMethod(model);
		return "/guessNumber";
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
			if(userController.isLogged() && field.isSolved()) {
				saveScore();
			}
			
		}
		fillMethod(model);
		return "/guessNumber";
	}

	@RequestMapping("/addRating_guess")
	public String addRating(@RequestParam(value = "value", required = false) String value, Model model) {
		int rating = Integer.parseInt(value);
		ratingService.setRating(new Rating(userController.getLoggedPlayer().getLogin(), "guessNumber", rating));
		fillMethod(model);
		return "/guessNumber";
	}

	@RequestMapping("/addFavorite_guess")
	public String addFavorite(Model model) {
		favoriteService.setFavorite(new Favorite(userController.getLoggedPlayer().getLogin(), "guessNumber"));
		fillMethod(model);
		return "/guessNumber";
	}

	private void fillMethod(Model model) {
		model.addAttribute("guessNumberController", this);
		model.addAttribute("scores", scoreService.getTopScores("guessNumber"));
		model.addAttribute("comment", commentService.getComments("guessNumber"));
		model.addAttribute("rating",ratingService.getAverageRating("guessNumber"));
		if (userController.isLogged()) {
			model.addAttribute("value",
					ratingService.getValue(userController.getLoggedPlayer().getLogin(), "guessNumber"));
			model.addAttribute("favorite",
					favoriteService.isFavorite(userController.getLoggedPlayer().getLogin(), "guessNumber"));
		}

	}
	
	public void saveScore() {		
			int time = (int) (field.getEndTime() - field.getStartTime()) / 1000;
			if (difficultScore == 1) {
				finalScore = 300 - time;
			}
			if (difficultScore == 2) {
				finalScore = 500 - time;
			}
			if (difficultScore == 3) {
				finalScore = 1000 - time;
			}
			Score score = new Score();
			score.setGame("guessNumber");
			score.setUsername(userController.getLoggedPlayer().getLogin());
			score.setValue(finalScore);
			scoreService.addScore(score);
		
	}

	

	
}
