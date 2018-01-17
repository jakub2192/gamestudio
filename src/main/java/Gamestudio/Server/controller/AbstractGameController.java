package Gamestudio.Server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import Gamestudio.service.CommentService;
import Gamestudio.service.FavoriteService;
import Gamestudio.service.GameService;
import Gamestudio.service.RatingService;
import Gamestudio.service.ScoreService;

public abstract class AbstractGameController {
	@Autowired
	protected ScoreService scoreService;
	@Autowired
	protected UserController userController;
	@Autowired
	protected RatingService ratingService;
	@Autowired
	protected CommentService commentService;
	@Autowired
	protected FavoriteService favoriteService;
	@Autowired
	protected GameService gameService;

	private String message;

	protected void fillMethod(Model model) {
		model.addAttribute("controller", this);
		model.addAttribute("game", gameService.getGame(getGameName()));
		model.addAttribute("scores", scoreService.getTopScores(getGameName()));
		model.addAttribute("comment", commentService.getComments(getGameName()));
		model.addAttribute("rating", ratingService.getAverageRating(getGameName()));
		if (userController.isLogged()) {
			model.addAttribute("value",
					ratingService.getValue(userController.getLoggedPlayer().getLogin(), getGameName()));
			model.addAttribute("favorite",
					favoriteService.isFavorite(userController.getLoggedPlayer().getLogin(), getGameName()));
		}

	}

	protected abstract String getGameName();

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
