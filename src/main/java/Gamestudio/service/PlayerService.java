package Gamestudio.service;

import java.util.List;

import Gamestudio.entity.Player;

public interface PlayerService {

	void register(Player player);

	Player login(String login, String password);

	boolean isPlayer(String login);

	List<Player> getPlayers();

	boolean isAdmin(String login);

}