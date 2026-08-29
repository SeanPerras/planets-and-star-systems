package org.example.daos;

import org.example.exceptions.DaoException;
import org.example.models.Planet;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class PlanetDao
{
    private final JdbcTemplate jdbcTemplate;

    public PlanetDao(DataSource ds) { this.jdbcTemplate = new JdbcTemplate(ds); }

    public List<Planet> getAllPlanets()
    {
        String query = "SELECT * FROM planets ORDER BY system_id IS NULL, system_id";
        return jdbcTemplate.query(query, this::mapToPlanet);
    }
    public List<Planet> getAllPlanets(String name, Integer sID, Boolean rogue)
    {
        if(sID == null && (name == null || name.isBlank()) && rogue == null)
            return getAllPlanets();

        String query = "SELECT * FROM planets WHERE ";
        List<Object> params = new ArrayList<>();
        boolean newQ = true;
        if(sID != null) {
            query += "system_id = ?";
            params.add(sID);
            newQ = false;
        }
        else if(rogue != null) {
            query += "system_id IS" + (rogue? "" : " NOT") + " NULL";
            newQ = false;
        }
        if(name != null) {
            query += (newQ? "" : " AND ") + "name LIKE CONCAT('%', ?, '%')";
            params.add(name);
        }
        query += " ORDER BY system_id IS NULL, system_id";
        return jdbcTemplate.query(query, this::mapToPlanet, params.toArray());
    }
    public List<Planet> getAllPlanets(String username)
    {
        String query = "SELECT * FROM planets WHERE is_public = true";
        List<Object> params = new ArrayList<>();
        if(username != null) {
            query += " OR owner_username = ?";
            params.add(username);
        }
        query+= " ORDER BY system_id IS NULL, system_id";
        return jdbcTemplate.query(query, this::mapToPlanet, params.toArray());
    }
    public List<Planet> getAllPlanets(String name, Integer sID, Boolean rogue, String username)
    {
        if(sID == null && name == null && rogue == null && username != null)
            return getAllPlanets(username);

        String query = "SELECT * FROM planets WHERE (is_public = true";
        List<Object> params = new ArrayList<>();
        if(username != null) {
            query += " OR owner_username = ?";
            params.add(username);
        }
        query += ")";
        if(sID != null) {
            query += " AND system_id = ?";
            params.add(sID);
        }
        else if(rogue != null) {
            query += " AND system_id IS" + (rogue? "" : " NOT") + " NULL";
        }
        if(name != null && !name.isBlank()) {
            query += " AND name LIKE CONCAT('%', ?, '%')";
            params.add(name);
        }
        query += " ORDER BY system_id IS NULL, system_id";
        return jdbcTemplate.query(query, this::mapToPlanet, params.toArray());
    }
    public Planet getPlanetByID(int ID)
    {
        String query = "SELECT * FROM planets WHERE id = ?";
        try { return jdbcTemplate.queryForObject(query, this::mapToPlanet, ID); }
        catch (EmptyResultDataAccessException e) { return null; }
    }
    public Planet getPlanetByID(int ID, String username)
    {
        String query = "SELECT * FROM planets WHERE id = ? AND (is_public = true";
        List<Object> params = new ArrayList<>();
        params.add(ID);
        if(username != null) {
            query += " OR owner_username = ?";
            params.add(username);
        }
        query += ")";

        try { return jdbcTemplate.queryForObject(query, this::mapToPlanet, params.toArray()); }
        catch (EmptyResultDataAccessException e) { return null; }
    }
    public Planet createPlanet(Planet p, String username)
    {
        String query = """
                INSERT INTO planets (name, type, mass, distance_from_star, description, moons,
                    is_public, owner_username, system_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getType());
            ps.setBigDecimal(3, p.getMass());
            ps.setBigDecimal(4, p.getSystemID() == null ? null : p.getDistanceFromStar());
            ps.setString(5, p.getDescription());
            String moons = p.getMoons();
            ps.setString(6, moons == null || moons.isBlank() ? "[]" : moons);
            ps.setBoolean(7, p.isPublicPlanet());
            ps.setString(8, username);
            ps.setObject(9, p.getSystemID(), Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if(rs.next()) p.setID(rs.getInt(1));
                else throw new DaoException("Failed to generate Planet id");
            }
        } catch (SQLException sqle) { throw new DaoException("Failed to form Planet"); }
        return getPlanetByID(p.getID(), username);
    }
    public Planet updatePlanetAdmin(Planet p)
    {
        String query = """
                    UPDATE planets
                    SET name = ?, type = ?, mass = ?, distance_from_star = ?,
                        description = ?, moons = ?, owner_username = ?, is_public = ?, system_id = ?
                    WHERE id = ?
                    """;
        int rows = jdbcTemplate.update(query, p.getName(), p.getType(), p.getMass(), p.getDistanceFromStar(),
                p.getDescription(), p.getMoons(), p.getOwnerUsername(), p.isPublicPlanet(), p.getSystemID(), p.getID());
        if (rows == 0) return null;
        return getPlanetByID(p.getID());
    }
    public Planet updatePlanet(Planet p)
    {
        String query = """
                UPDATE planets
                SET name = ?, type = ?, mass = ?, distance_from_star = ?,
                    description = ?, moons = ?, is_public = ?, system_id = ?
                WHERE id = ?
                """;
        int rows = jdbcTemplate.update(query, p.getName(), p.getType(), p.getMass(), p.getDistanceFromStar(),
                p.getDescription(), p.getMoons(), p.isPublicPlanet(), p.getSystemID(), p.getID());
        if (rows == 0) return null;
        return getPlanetByID(p.getID(), p.getOwnerUsername());
    }
    public int destroyPlanet(int ID)
    {
        String query = "DELETE FROM planets WHERE id = ?";
        return jdbcTemplate.update(query, ID);
    }


    private Planet mapToPlanet(ResultSet rs, int row) throws SQLException
    {
        return new Planet(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("type"),
                rs.getBigDecimal("mass"),
                rs.getBigDecimal("distance_from_star"),
                rs.getString("description"),
                rs.getString("moons"),
                rs.getBoolean("is_public"),
                rs.getString("owner_username"),
                rs.getObject("system_id", Integer.class)
        );
    }
}
