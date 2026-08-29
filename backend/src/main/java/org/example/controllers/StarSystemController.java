package org.example.controllers;

import org.example.daos.StarSystemDao;
import org.example.daos.UserDao;
import org.example.models.Planet;
import org.example.models.StarSystem;
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
@RequestMapping("/api/star-systems")
@PreAuthorize("isAuthenticated()")
public class StarSystemController
{
    @Autowired
    private StarSystemDao starSystemDao;
    @Autowired
    private UserDao userDao;

    @GetMapping
    @PreAuthorize("permitAll()")
    public List<StarSystem> getAll(@RequestParam(required = false) String name,
                                   @RequestParam(required = false) String starType,
                                   @RequestParam(required = false) Integer planetCount,
                                   @RequestParam(required = false) String sortBy,
                                   @RequestParam(required = false, defaultValue = "false") boolean descending,
                                   Principal p)
    {
        if (planetCount != null && planetCount < 0)
            BAD_REQUEST("Planet count cannot be negative.");

        Comparator<StarSystem> comparator = null;
        if(sortBy != null) {
             comparator = switch (sortBy) {
                 case "name" -> Comparator.comparing(StarSystem::getName);
                 case "starType" -> Comparator.comparing(StarSystem::getStarType);
                 default -> null;
            };

            if(comparator == null) BAD_REQUEST("Invalid sort field");
            if (descending) comparator = comparator.reversed();
        }

        String username = p == null ? null : p.getName();
        boolean admin = isAdmin(p),
                hasNoFilters = name == null && starType == null && planetCount == null;
        List<StarSystem> systems;
        if(hasNoFilters && p != null) {
            if (admin) systems = starSystemDao.getAllStarSystems();
            else systems = starSystemDao.getAllStarSystems(username);
        }
        else if(admin) systems = starSystemDao.getAllStarSystems(name, starType, planetCount);
        else systems = starSystemDao.getAllStarSystems(name, starType, planetCount, username);

        if(comparator != null) return systems.stream().sorted(comparator).toList();
        return systems;
    }

    @GetMapping("/designators")
    @PreAuthorize("permitAll()")
    public List<StarSystem.Designator> getAllDesignators(Principal p)
    {
        String username = p == null ? null : p.getName();
        if (isAdmin(p)) return starSystemDao.getAllDesignators();
        return starSystemDao.getAllDesignators(username);
    }

    @GetMapping("/{ID}")
    @PreAuthorize("permitAll()")
    public StarSystem get(@PathVariable int ID, Principal p)
    {
        String username = p == null ? null : p.getName();

        StarSystem starSystem;
        if (isAdmin(p)) starSystem = starSystemDao.getStarSystemByID(ID);
        else starSystem = starSystemDao.getStarSystemByID(ID, username);

        if (starSystem == null) NOT_FOUND(ID);
        return starSystem;
    }

    @GetMapping("/designators/{ID}")
    @PreAuthorize("permitAll()")
    public StarSystem.Designator getDesignator(@PathVariable int ID, Principal p)
    {
        String username = p == null ? null : p.getName();

        StarSystem.Designator designator;
        if(isAdmin(p)) designator = starSystemDao.getDesignatorByID(ID);
        else designator = starSystemDao.getDesignatorByID(ID, username);

        if(designator == null) NOT_FOUND(ID);
        return designator;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StarSystem create(@RequestBody StarSystem starSystem, Principal p)
    {
        if (p == null)
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "You must be logged in to create a star system.");
        if (starSystem.getStarMass() == null ||
                starSystem.getStarMass().compareTo(BigDecimal.ZERO) <= 0)
            BAD_REQUEST("Star mass must be positive.");
        if (starSystem.getDistanceFromEarth() == null ||
                starSystem.getDistanceFromEarth().compareTo(BigDecimal.ONE) < 0)
            BAD_REQUEST("Distance from Earth must be, at least, 1 Ly.");

        return starSystemDao.createStarSystem(starSystem, p.getName());
    }

    @PutMapping("/{ID}")
    public StarSystem update(@PathVariable int ID, @RequestBody StarSystem starSystem, Principal p)
    {
        boolean admin = isAdmin(p);
        StarSystem dbStarSystem;
        if(admin) {
            dbStarSystem = starSystemDao.getStarSystemByID(ID);
            if(dbStarSystem == null) NOT_FOUND(ID);
        }
        else {
            dbStarSystem = starSystemDao.getStarSystemByID(ID, p.getName());
            if(dbStarSystem == null) NOT_FOUND(ID);

            if(!dbStarSystem.getOwnerUsername().equals(p.getName()))
                throw new AccessDeniedException("You don't have access to this star system.");

            starSystem.setOwnerUsername(dbStarSystem.getOwnerUsername());
        }
        if (starSystem.getStarMass() == null ||
                starSystem.getStarMass().compareTo(BigDecimal.ZERO) <= 0)
            BAD_REQUEST("Star mass must be positive.");
        if (starSystem.getDistanceFromEarth() == null ||
                starSystem.getDistanceFromEarth().compareTo(BigDecimal.ONE) < 0)
            BAD_REQUEST("Distance from Earth must be, at least, 1 Ly.");

        starSystem.setID(ID);
        StarSystem updatedStarSystem = admin? starSystemDao.updateStarSystemAdmin(starSystem) :
                starSystemDao.updateStarSystem(starSystem);

        if (updatedStarSystem == null) NOT_FOUND(ID);
        return updatedStarSystem;
    }

    @DeleteMapping("/{ID}")
    public int delete(@PathVariable int ID, Principal p)
    {
        boolean admin = isAdmin(p);
        StarSystem starSystem;
        if(admin) starSystem = starSystemDao.getStarSystemByID(ID);
        else starSystem = starSystemDao.getStarSystemByID(ID, p.getName());

        if(starSystem == null) NOT_FOUND(ID);
        else if(!admin && !starSystem.getOwnerUsername().equals(p.getName()))
            throw new AccessDeniedException("You don't have access to this star system.");

        int rows = starSystemDao.destroyStarSystem(ID);
        if (rows == 0) NOT_FOUND(ID);
        return rows;
    }


    private void NOT_FOUND(int ID)
    {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Star system with ID: %s wasn't found".formatted(ID));
    }
    private void BAD_REQUEST(String message)
    {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
    private boolean isAdmin(Principal p)
    {
        return p != null && userDao.getRoles(p.getName()).contains("ADMIN");
    }
}
