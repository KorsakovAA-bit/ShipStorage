package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.hibernate.annotations.Proxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/rest/")
public class ShipController {

    @Autowired
    private ShipService shipService;

    @RequestMapping(value = "ships", method = RequestMethod.GET)
    public ResponseEntity<List<Ship>> getAllShips() {
        List<Ship> ships = this.shipService.getAll();
//        for (Ship ship: ships) {
//            System.out.println(ship.getName());
//        }
        if (ships.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(ships, HttpStatus.OK);
    }

    @RequestMapping(value = "ships", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Ship> saveShip(@RequestBody Ship enterShip) {
        Ship ship = enterShip;

        if (ship == null || ship.getName()==null || ship.getPlanet() == null || ship.getShipType() == null || ship.getCrewSize() == null || ship.getProdDate() == null || ship.getSpeed() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (ship.getName().length() > 50 || ship.getPlanet().length() > 50 || ship.getName().equals("") || ship.getPlanet().equals("")) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (ship.getSpeed() < 0.01 || ship.getSpeed() > 0.99 || ship.getCrewSize()<1 || ship.getCrewSize() > 9999) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        else {
            DecimalFormat decimalFormat = new DecimalFormat( "#.##" );
            String speed = decimalFormat.format(ship.getSpeed());
            speed = speed.replace(",", ".");
            Double dSpeed = Double.parseDouble(speed);
            ship.setSpeed(dSpeed);
        }
        //32526485470814 26192246400L 33134745599L
        Calendar calendar = new GregorianCalendar();
        calendar.set(2800, 0, 0);
        Calendar calendar2 = new GregorianCalendar();
        calendar2.set(3019, 11,30);
        if(ship.getProdDate().getTime() < calendar.getTimeInMillis() || ship.getProdDate().getTime() > calendar2.getTimeInMillis()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(ship.getUsed() == null) ship.setUsed(false);
        ship.setRating(calculateRating(ship));


        this.shipService.save(ship);
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }
    @RequestMapping(value = "ships/{id}", method = RequestMethod.GET)
    public ResponseEntity<Ship> getShipById(@PathVariable("id") String strShipId){
        long shipId;
        if(isValidityId(strShipId)) shipId = Long.parseLong(strShipId);
        else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Ship ship = shipService.getById(shipId);
        if(ship == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @RequestMapping(value = "ships/{id}", method = RequestMethod.POST)
    public ResponseEntity<Ship> updateShipById(@PathVariable("id") String strShipId, @RequestBody Ship enterShip) {
        long shipId;
        if(isValidityId(strShipId)) shipId = Long.parseLong(strShipId);
        else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Ship currentShip = shipService.getById(shipId);
        if(currentShip == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(enterShip.getName() != null && enterShip.getName().length() <= 50) currentShip.setName(enterShip.getName());
        if(enterShip.getPlanet() != null && enterShip.getPlanet().length() <= 50) currentShip.setPlanet(enterShip.getPlanet());
        if(enterShip.getShipType() != null) currentShip.setShipType(enterShip.getShipType());
        if(enterShip.getProdDate() != null){
            Calendar calendar = new GregorianCalendar();
            calendar.set(2800, 0, 0);
            Calendar calendar2 = new GregorianCalendar();
            calendar2.set(3019, 11,30);
            if(enterShip.getProdDate().getTime() >= calendar.getTimeInMillis() && enterShip.getProdDate().getTime() <= calendar2.getTimeInMillis()) currentShip.setProdDate(enterShip.getProdDate());
        }
        if(enterShip.getUsed() != null) currentShip.setUsed(enterShip.getUsed());
        if(enterShip.getSpeed() != null && enterShip.getSpeed() >= 0.01 && enterShip.getSpeed() <= 0.99){
            DecimalFormat decimalFormat = new DecimalFormat( "#.##" );
            String speed = decimalFormat.format(enterShip.getSpeed());
            speed = speed.replace(",", ".");
            Double dSpeed = Double.parseDouble(speed);
            currentShip.setSpeed(dSpeed);
        }
        if(enterShip.getCrewSize() != null && enterShip.getCrewSize() >= 1 && enterShip.getCrewSize() <= 9999) currentShip.setCrewSize(enterShip.getCrewSize());
        currentShip.setRating(calculateRating(currentShip));
        shipService.update(currentShip);

        return new ResponseEntity<>(currentShip, HttpStatus.OK);
    }

    private Double calculateRating(Ship ship){
        Double rating;
        Double k;
        if(ship.getUsed()) k = 0.5;
        else k = 1.0;

        rating = (80 * ship.getSpeed() * k)/(3019 - (ship.getProdDate().getYear()+1900) + 1);
        DecimalFormat decimalFormat = new DecimalFormat( "#.##" );
        rating = Double.parseDouble(decimalFormat.format(rating).replace(",","."));

        return rating;
    }

    private static boolean isPrime(final long number) {
        return IntStream.rangeClosed(2, Math.toIntExact(number / 2)).anyMatch(i -> number % i == 0);
    }
    //check the validity of id
    private boolean isValidityId(String strShipId){
        long shipId;
        try {
            shipId = Long.parseLong(strShipId);
            if(shipId <= 0){
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }


}
