package Gamestudio.service.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import Gamestudio.entity.Favorite;
import Gamestudio.entity.Game;
import Gamestudio.service.FavoriteService;

@Transactional
public class FavoriteServiceJPA implements FavoriteService {

	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void setFavorite(Favorite favorite) {
		try {
			entityManager.createQuery("SELECT f FROM Favorite f WHERE f.username = :username AND f.game = :game")
					.setParameter("username", favorite.getUsername()).setParameter("game", favorite.getGame())
					.getSingleResult();
			removeFavorite(favorite.getUsername(), favorite.getGame());
		} catch (NoResultException e) {
			entityManager.persist(favorite);
		}

	}

	@Override
	public List<Game> getFavoriteGames(String player) {
		return entityManager.createQuery("SELECT g FROM Game g WHERE EXISTS (SELECT f FROM Favorite f where g.ident = f.game and f.username = :player").setParameter("player", player).getResultList();
	}
	
	

	
	@Override
	public boolean isFavorite(String username, String game) {
		try {
			 entityManager.createQuery("SELECT f FROM Favorite f WHERE f.game = :game AND f.username = :username ")
					.setParameter("game", game).setParameter("username", username).getSingleResult();
		} catch (NoResultException e) {
			 return false;
		}
		return true;
	}
	
	private void removeFavorite(String username, String game) {
		entityManager.createQuery("DELETE FROM Favorite f WHERE f.username = :username  AND f.game = :game")
				.setParameter("username", username).setParameter("game", game).executeUpdate();

	}

	@Override
	public List<Favorite> getFavorite(String username) {

		return null;
	}

}
