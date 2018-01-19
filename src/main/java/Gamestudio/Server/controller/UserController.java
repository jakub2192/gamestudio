package Gamestudio.Server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

import Gamestudio.entity.Game;
import Gamestudio.entity.Player;
import Gamestudio.service.GameService;
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
	private GameService gameService;


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

	@RequestMapping("/admin")
	public String admin(Model model) {
		fillMethod(model);
		return "admin";
	}


	private void fillMethod(Model model) {
		List<Game> games = gameService.getGames(); 
		setRatingToGame(games);
		model.addAttribute("games", games);
		if (isLogged()) {
			model.addAttribute("allPlayers", playerService.getPlayers());
			model.addAttribute("admin", playerService.isAdmin(getLoggedPlayer().getLogin()));
			model.addAttribute("favorite1", gameService.getFavoriteGames(getLoggedPlayer().getLogin()));
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
	public String register(Player player, Model model) {
		if (!playerService.isPlayer(player.getLogin())) {			
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
public void  setRatingToGame(List<Game> games){
	for (Game game : games) {
		game.setAvgRating(ratingService.getAverageRating(game.getIdent()));
	}
	
}
}
