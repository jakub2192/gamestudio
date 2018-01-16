package Gamestudio.Server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import Gamestudio.entity.Comment;
import Gamestudio.entity.Player;
import Gamestudio.service.FavoriteService;
import Gamestudio.service.PlayerService;
import Gamestudio.service.RatingService;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class UserController {
	@Autowired
	private PlayerService playerService;
	@Autowired
	private RatingService ratingService;
	@Autowired
	private FavoriteService favoriteService;

	private Player loggedPlayer;
	private String loginMessage;

	public Player getLoggedPlayer() {
		return loggedPlayer;
	}

	@RequestMapping("/user")
	public String user(Model model) {
		fillMethod(model);
		loginMessage = "";
		return "login";
	}

	@RequestMapping("/")
	public String index(Model model) {
		fillMethod(model);
		return "index";
	}


	private void fillMethod(Model model) {
		model.addAttribute("avgRatingMines", ratingService.getAverageRating("mines"));
		model.addAttribute("avgRatingPuzzle", ratingService.getAverageRating("puzzle"));
		model.addAttribute("avgRatingGuess", ratingService.getAverageRating("guessNumber"));
		model.addAttribute("avgRatingSudoku", ratingService.getAverageRating("sudoku"));
		if (isLogged()) {
			model.addAttribute("favorite", favoriteService.getFavorite(loggedPlayer.getLogin()));
		}

	}

	@RequestMapping("/login")
	public String login(Player player, Model model) {
		loggedPlayer = playerService.login(player.getLogin(), player.getPassword());
loginMessage= "";
		fillMethod(model);
		return isLogged() ? "index" : "login";
	}

	@RequestMapping("/register")
	public String register(Player player, String login, Model model) {
		if (!playerService.isPlayer(login)) {			
				playerService.register(player);
				loginMessage = "";
				login(player, model);
		} else {
			loginMessage = "Login already exists!!";
		}

		fillMethod(model);
		return "/login";

	}

	@RequestMapping("/logout")
	public String login(Model model) {
		loggedPlayer = null;
		fillMethod(model);
		return "login";
	}

	public boolean isLogged() {
		return loggedPlayer != null;
	}

	public String getLoginMessage() {
		return loginMessage;
	}

}
