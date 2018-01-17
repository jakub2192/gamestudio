package Gamestudio.service;

import java.util.List;

import Gamestudio.entity.Game;


public interface GameService {
Game getGame(String ident);
List<Game> getFavoriteGames(String player);
List<Game> getGames();

}
