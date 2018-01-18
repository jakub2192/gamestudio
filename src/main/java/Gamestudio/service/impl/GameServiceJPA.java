package Gamestudio.service.impl;

import java.util.List;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import Gamestudio.entity.Game;
import Gamestudio.service.GameService;

@Transactional
public class GameServiceJPA  implements GameService{

	@PersistenceContext
	private EntityManager entityManager;

	
	@Override
	public Game getGame(String ident) {
		
		return entityManager.find(Game.class, ident);
	}

	
	@Override
	public List<Game> getGames() {		
		return entityManager.createQuery("SELECT g FROM Game g").getResultList();
	}

}
