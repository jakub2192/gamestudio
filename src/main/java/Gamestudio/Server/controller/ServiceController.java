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
import Gamestudio.service.CommentService;
import Gamestudio.service.FavoriteService;
import Gamestudio.service.RatingService;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class ServiceController {
	@Autowired
	protected UserController userController;
	@Autowired
	protected RatingService ratingService;
	@Autowired
	protected CommentService commentService;
	@Autowired
	protected FavoriteService favoriteService;
	
	@RequestMapping("/addComment")
	public String addComment(Comment comment, Model model) {
		if (!"".equals(comment.getContent())) {
			commentService.addComment(new Comment(userController.getLoggedPlayer().getLogin(), comment.getGame(), comment.getContent()));
		}	
		return "forward:/" + comment.getGame();
	}

	@RequestMapping("/addRating")
	public String addRating(Rating rating, Model model) {
		ratingService.setRating(new Rating(userController.getLoggedPlayer().getLogin(), rating.getGame(), rating.getValue()));
		return "forward:/" + rating.getGame();
	}
	
	@RequestMapping("/addFavorite")
	public String addFavorite(String game, Model model) {
		favoriteService.setFavorite(new Favorite(userController.getLoggedPlayer().getLogin(), game));
		return "forward:/" + game;
	}
}
