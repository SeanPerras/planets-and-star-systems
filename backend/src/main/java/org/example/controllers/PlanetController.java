package org.example.controllers;

import org.example.daos.PlanetDao;
import org.example.daos.UserDao;
import org.example.models.Planet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/planets")
@PreAuthorize("isAuthenticated()")
public class PlanetController
{
    @Autowired
    private PlanetDao planetDao;
    @Autowired
    private UserDao userDao;

    @GetMapping
    @PreAuthorize("permitAll()")
    public List<Planet> getAll(@RequestParam(required = false) String name,
                               @RequestParam(required = false) Integer systemID,
                               @RequestParam(required = false) Boolean rogue,
                               @RequestParam(required = false) String sortBy,
                               @RequestParam(required = false, defaultValue = "false") boolean descending,
                               Principal p)
    {
        if(systemID != null && rogue != null)
            BAD_REQUEST("A system ID and rogue status cannot be searched together.");

        Comparator<Planet> comparator = null;
        if(sortBy != null) {
             comparator = switch (sortBy) {
                case "name" -> Comparator.comparing(Planet::getName);
                case "mass" -> Comparator.comparing(Planet::getMass);
                default -> null;
            };

            if(comparator == null) BAD_REQUEST("Invalid sort field");
            if(descending) comparator = comparator.reversed();
        }

        String username = p == null ? null : p.getName();
        boolean admin = isAdmin(p),
                hasNoFilters = systemID == null && name == null && rogue == null;
        List<Planet> planets;

        if(hasNoFilters && p != null) {
            if (admin) planets = planetDao.getAllPlanets();
            else planets = planetDao.getAllPlanets(username);
        }
        else if(admin) planets = planetDao.getAllPlanets(name, systemID, rogue);
        else planets = planetDao.getAllPlanets(name, systemID, rogue, username);

        if(comparator != null) return planets.stream().sorted(comparator).toList();
        return planets;
    }

    @GetMapping("/{ID}")
    @PreAuthorize("permitAll()")
    public Planet get(@PathVariable int ID, Principal p)
    {
        String username = p == null ? null : p.getName();
        Planet planet;
        if(isAdmin(p)) planet = planetDao.getPlanetByID(ID);
        else planet = planetDao.getPlanetByID(ID, username);

        if (planet == null) NOT_FOUND(ID);
        return planet;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Planet create(@RequestBody Planet planet, Principal p)
    {
        if (p == null) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "You must be logged in to create a planet.");
        }
        if (planet.getSystemID() == null && planet.getDistanceFromStar() != null)
            BAD_REQUEST("A rogue planet cannot have a distance from a star.");
        else if (planet.getSystemID() != null && planet.getDistanceFromStar() == null)
            BAD_REQUEST("A planet in a system must have a distance from its star.");
        if(planet.getMass() == null || planet.getMass().compareTo(BigDecimal.ZERO) <= 0)
            BAD_REQUEST("A planet's mass must have a positive.");

        return planetDao.createPlanet(planet, p.getName());
    }

    @PutMapping("/{ID}")
    public Planet update(@PathVariable int ID, @RequestBody Planet planet, Principal p)
    {
        boolean admin = isAdmin(p);
        Planet dbPlanet;

        if(admin) {
            dbPlanet = planetDao.getPlanetByID(ID);
            if(dbPlanet == null) NOT_FOUND(ID);
            if(planet.getOwnerUsername() == null || planet.getOwnerUsername().isEmpty())
                planet.setOwnerUsername(dbPlanet.getOwnerUsername());
        }
        else {
            dbPlanet = planetDao.getPlanetByID(ID, p.getName());
            if(dbPlanet == null) NOT_FOUND(ID);

            if(!dbPlanet.getOwnerUsername().equals(p.getName()))
                throw new AccessDeniedException("You don't have access to this planet.");

            planet.setOwnerUsername(dbPlanet.getOwnerUsername());
        }

        if (planet.getSystemID() == null && planet.getDistanceFromStar() != null)
            BAD_REQUEST("A rogue planet cannot have a distance from a star.");
        else if (planet.getSystemID() != null && planet.getDistanceFromStar() == null)
            BAD_REQUEST("A planet in a system must have a distance from its star.");
        if(planet.getMass() == null || planet.getMass().compareTo(BigDecimal.ZERO) <= 0)
            BAD_REQUEST("A planet's mass must have a positive.");

        planet.setID(ID);
        Planet updatedPlanet = admin? planetDao.updatePlanetAdmin(planet) :
                planetDao.updatePlanet(planet);

        if (updatedPlanet == null) NOT_FOUND(ID);
        return updatedPlanet;
    }

    @DeleteMapping("/{ID}")
    public int delete(@PathVariable int ID, Principal p)
    {
        boolean admin = isAdmin(p);
        Planet planet;
        if(admin) planet = planetDao.getPlanetByID(ID);
        else planet = planetDao.getPlanetByID(ID, p.getName());

        if(planet == null) NOT_FOUND(ID);
        if(!admin && !planet.getOwnerUsername().equals(p.getName()))
            throw new AccessDeniedException("You don't have access to this planet.");

        int rows = planetDao.destroyPlanet(ID);
        if (rows == 0) NOT_FOUND(ID);
        return rows;
    }


    private void NOT_FOUND(int ID)
    {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Planet with ID: %s wasn't found".formatted(ID));
    }
    private void BAD_REQUEST(String message)
    {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
    private boolean isAdmin(Principal p) {
        return p != null && userDao.getRoles(p.getName()).contains("ADMIN");
    }
}
