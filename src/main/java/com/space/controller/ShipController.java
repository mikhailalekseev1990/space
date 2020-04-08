package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/rest")
public class ShipController {

	@Autowired
	private ShipService shipService;

	@GetMapping(value = "/ships")
	public List<Ship> getShipsList(@RequestParam(value = "name", required = false) String name,
	                               @RequestParam(value = "planet", required = false) String planet,
	                               @RequestParam(value = "shipType", required = false) ShipType shipType,
	                               @RequestParam(value = "after", required = false) Long after,
	                               @RequestParam(value = "before", required = false) Long before,
	                               @RequestParam(value = "isUsed", required = false) Boolean isUsed,
	                               @RequestParam(value = "minSpeed", required = false) Double minSpeed,
	                               @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
	                               @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
	                               @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
	                               @RequestParam(value = "minRating", required = false) Double minRating,
	                               @RequestParam(value = "maxRating", required = false) Double maxRating,
	                               @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order,
	                               @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
	                               @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {


		return shipService.getShips(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed,
				minCrewSize, maxCrewSize, minRating, maxRating, order, pageNumber, pageSize);
	}

	@GetMapping(value = "/ships/count")
	public Integer getShipsCount(@RequestParam(value = "name", required = false) String name,
	                             @RequestParam(value = "planet", required = false) String planet,
	                             @RequestParam(value = "shipType", required = false) ShipType shipType,
	                             @RequestParam(value = "after", required = false) Long after,
	                             @RequestParam(value = "before", required = false) Long before,
	                             @RequestParam(value = "isUsed", required = false) Boolean isUsed,
	                             @RequestParam(value = "minSpeed", required = false) Double minSpeed,
	                             @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
	                             @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
	                             @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
	                             @RequestParam(value = "minRating", required = false) Double minRating,
	                             @RequestParam(value = "maxRating", required = false) Double maxRating) {

		return shipService.getShipsList(name, planet, shipType, after, before, isUsed, minSpeed,
				maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
	}

	@PostMapping(value = "/ships")
	@ResponseBody
	public Ship createShip(@RequestBody Ship ship) {
		return shipService.createShip(ship);
	}

	@GetMapping(value = "/ships/{id}")
	@ResponseBody
	public Ship getShip(@PathVariable(value = "id") String id) {
		return shipService.getShip(id);
	}

	@PostMapping(value = "/ships/{id}")
	@ResponseBody
	public Ship updateShip(@PathVariable(value = "id") String id, @RequestBody Ship ship) {
		return shipService.updateShip(id, ship);
	}

	@DeleteMapping(value = "/ships/{id}")
	public void deleteShip(@PathVariable(value = "id") String id) {
		shipService.deleteShip(id);

	}
}
