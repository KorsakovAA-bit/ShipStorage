package com.space.service;

import com.space.model.Ship;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipServiceClass implements ShipService {

    @Autowired
    ShipRepository shipRepository;

    @Override
    public Ship getById(Long id) {
        return shipRepository.findById(id).orElse(null);
    }

    @Override
    public void update(Ship ship) {
        shipRepository.save(ship);
    }

    @Override
    public void save(Ship ship) {
        shipRepository.save(ship);
    }

    @Override
    public void delete(Long id) {
        shipRepository.delete(shipRepository.findById(id).orElse(null));
    }

    @Override
    public List<Ship> getAll() {
        return shipRepository.findAll();
    }
}
