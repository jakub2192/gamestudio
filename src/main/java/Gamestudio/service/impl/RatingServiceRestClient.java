package Gamestudio.service.impl;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Gamestudio.entity.Rating;
import Gamestudio.service.RatingService;

public class RatingServiceRestClient implements RatingService {
	private static final String URL = "http://localhost:8080/rest/rating";


	

	@Override
	public void setRating(Rating rating) {
		Client client = ClientBuilder.newClient();
		Response response = client.target(URL).request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(rating, MediaType.APPLICATION_JSON), Response.class);
		
	}

	@Override
	public double getAverageRating(String game) {
		Client client = ClientBuilder.newClient();
		Double o =  client.target(URL).path("/" + game).request(MediaType.APPLICATION_JSON).get(Double.class);
		return o != null ? o : 0;
				
		
	}

	public int getValue(String game, String username) {
		Client client = ClientBuilder.newClient();
		return client.target(URL).path("/" + game +"/"+ username).request(MediaType.APPLICATION_JSON)
				.get(Integer.class);
	}

	
}

