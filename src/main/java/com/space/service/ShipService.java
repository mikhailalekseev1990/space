package com.space.service;


import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;

public interface ShipService {
	List<Ship> getShips(String name,
	                    String planet,
	                    ShipType shipType,
	                    Long after,
	                    Long before,
	                    Boolean isUsed,
	                    Double minSpeed,
	                    Double maxSpeed,
	                    Integer minCrewSize,
	                    Integer maxCrewSize,
	                    Double minRating,
	                    Double maxRating,
	                    ShipOrder order,
	                    Integer pageNumber,
	                    Integer pageSize);

	Integer getShipsList(String name,
	                     String planet,
	                     ShipType shipType,
	                     Long after,
	                     Long before,
	                     Boolean isUsed,
	                     Double minSpeed,
	                     Double maxSpeed,
	                     Integer minCrewSize,
	                     Integer maxCrewSize,
	                     Double minRating,
	                     Double maxRating);

	Ship createShip(Ship requestShip);

	Ship getShip(String id);

	Ship updateShip(String id, Ship ship);

	void deleteShip(String id);

}