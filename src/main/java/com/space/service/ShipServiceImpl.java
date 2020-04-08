package com.space.service;

import com.space.Exception.NotFoundException;
import com.space.Exception.RequestException;
import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class ShipServiceImpl implements ShipService {
	@Autowired
	private ShipRepository shipRepository;

	@Override
	public List<Ship> getShips(String name,
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
	                           Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));

		return shipRepository.findAll(
				Specification.where(findByName(name)
						.and(findByPlanet(planet)))
						.and(findByShipType(shipType))
						.and(findByDate(after, before))
						.and(findByUsage(isUsed))
						.and(findBySpeed(minSpeed, maxSpeed))
						.and(findByCrewSize(minCrewSize, maxCrewSize))
						.and(findByRating(minRating, maxRating)), pageable).getContent();
	}

	@Override
	public Integer getShipsList(String name,
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
	                            Double maxRating) {
		return shipRepository.findAll(Specification.where(findByName(name)
				.and(findByPlanet(planet)))
				.and(findByShipType(shipType))
				.and(findByDate(after, before))
				.and(findByUsage(isUsed))
				.and(findBySpeed(minSpeed, maxSpeed))
				.and(findByCrewSize(minCrewSize, maxCrewSize))
				.and(findByRating(minRating, maxRating))).size();
	}

	@Override
	public Ship createShip(Ship ship) {
		if (ship.getName() == null || ship.getPlanet() == null || ship.getShipType() == null ||
				ship.getProdDate() == null || ship.getSpeed() == null || ship.getCrewSize() == null)
			throw new RequestException("Не верный параметр");

		checkParameters(ship);
		if (ship.getUsed() == null)
			ship.setUsed(false);
		Double rating = shipRating(ship);
		ship.setRating(rating);

		return shipRepository.saveAndFlush(ship);
	}

	@Override
	public Ship updateShip(String id, Ship ship) {
		Long longId = parseId(id);
		checkParameters(ship);

		if (!shipRepository.findById(longId).isPresent())
			throw new NotFoundException("Корабль не найден");

		Ship updateShip = shipRepository.findById(longId).get();

		if (ship.getName() != null) updateShip.setName(ship.getName());

		if (ship.getPlanet() != null) updateShip.setPlanet(ship.getPlanet());

		if (ship.getShipType() != null) updateShip.setShipType(ship.getShipType());

		if (ship.getProdDate() != null) updateShip.setProdDate(ship.getProdDate());

		if (ship.getSpeed() != null) updateShip.setSpeed(ship.getSpeed());

		if (ship.getUsed() != null) updateShip.setUsed(ship.getUsed());

		if (ship.getCrewSize() != null) updateShip.setCrewSize(ship.getCrewSize());

		Double rating = shipRating(updateShip);
		updateShip.setRating(rating);

		return shipRepository.save(updateShip);
	}

	private void checkParameters(Ship ship) {
		if (ship.getName() != null && (ship.getName().length() < 1 || ship.getName().length() > 50))
			throw new RequestException("Не верное название корабля");
		if (ship.getPlanet() != null && (ship.getPlanet().length() < 1 || ship.getPlanet().length() > 50))
			throw new RequestException("Не верное название планеты");
		if (ship.getCrewSize() != null && (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999))
			throw new RequestException("Не верное количество команды");
		if (ship.getSpeed() != null && (ship.getSpeed() < 0.01D || ship.getSpeed() > 0.99D))
			throw new RequestException("Не верная скорость");
		if (ship.getProdDate() != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(ship.getProdDate());
			if (cal.get(Calendar.YEAR) < 2800 || cal.get(Calendar.YEAR) > 3019)
				throw new RequestException("Не верная дата создания");
		}
	}

	@Override
	public Ship getShip(String id) {
		Long longId = parseId(id);
		if (!shipRepository.existsById(longId))
			throw new NotFoundException("Корабль не найден");
		return shipRepository.findById(longId).get();
	}


	@Override
	public void deleteShip(String id) {
		Long longId = parseId(id);
		if (shipRepository.existsById(longId))
			shipRepository.deleteById(longId);
		else throw new NotFoundException("Ship not found");
	}

	private Long parseId(String pathId) {
		if (pathId.isEmpty() || pathId.equals("0"))
			throw new RequestException("ID не должен быть путым или 0");
		try {
			return Long.parseLong(pathId);
		} catch (NumberFormatException e) {
			throw new RequestException("ID долже быть числом");
		}
	}

	private Double shipRating(Ship ship) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(ship.getProdDate());
		int year = calendar.get(Calendar.YEAR);
		BigDecimal rating = new BigDecimal((80 * ship.getSpeed() * (ship.getUsed() ? 0.5 : 1)) / (3019 - year + 1));
		return rating.setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	private Specification<Ship> findByName(String name) {
		return (Specification<Ship>) (root, criteriaQuery, criteriaBuilder) ->
				name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
	}

	private Specification<Ship> findByPlanet(String planet) {
		return (Specification<Ship>) (root, criteriaQuery, criteriaBuilder) ->
				planet == null ? null : criteriaBuilder.like(root.get("planet"), "%" + planet + "%");
	}

	private Specification<Ship> findByShipType(ShipType shipType) {
		return (Specification<Ship>) (root, criteriaQuery, criteriaBuilder) ->
				shipType == null ? null : criteriaBuilder.equal(root.get("shipType"), shipType);
	}


	private Specification<Ship> findByDate(Long after, Long before) {
		return (Specification<Ship>) (root, criteriaQuery, criteriaBuilder) -> {
			if (after == null && before == null) return null;
			if (after == null) return criteriaBuilder.lessThanOrEqualTo(root.get("prodDate"), new Date(before));
			if (before == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("prodDate"), new Date(after));

			return criteriaBuilder.between(root.get("prodDate"), new Date(after), new Date(before));
		};
	}

	private Specification<Ship> findByUsage(Boolean isUsed) {
		return (Specification<Ship>) (root, criteriaQuery, criteriaBuilder) -> {
			if (isUsed == null) return null;
			if (isUsed) return criteriaBuilder.isTrue(root.get("isUsed"));
			else return criteriaBuilder.isFalse(root.get("isUsed"));
		};
	}

	private Specification<Ship> findBySpeed(Double min, Double max) {
		return (Specification<Ship>) (root, criteriaQuery, criteriaBuilder) -> {
			if (min == null && max == null) return null;
			if (min == null) return criteriaBuilder.lessThanOrEqualTo(root.get("speed"), max);
			if (max == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("speed"), min);

			return criteriaBuilder.between(root.get("speed"), min, max);
		};
	}

	private Specification<Ship> findByCrewSize(Integer min, Integer max) {
		return (Specification<Ship>) (root, criteriaQuery, criteriaBuilder) -> {
			if (min == null && max == null) return null;
			if (min == null) return criteriaBuilder.lessThanOrEqualTo(root.get("crewSize"), max);
			if (max == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("crewSize"), min);

			return criteriaBuilder.between(root.get("crewSize"), min, max);


		};
	}

	private Specification<Ship> findByRating(Double min, Double max) {
		return (Specification<Ship>) (root, criteriaQuery, criteriaBuilder) -> {
			if (min == null && max == null) return null;
			if (min == null) return criteriaBuilder.lessThanOrEqualTo(root.get("rating"), max);
			if (max == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), min);

			return criteriaBuilder.between(root.get("rating"), min, max);

		};
	}
}
