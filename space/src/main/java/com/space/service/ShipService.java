package com.space.service;

import com.space.model.Ship;

import java.util.List;

public interface ShipService {

    Ship getById(Long id);

    void update(Ship ship);

    void save(Ship ship);

    void delete(Long id);

    List<Ship> getAll();
}
