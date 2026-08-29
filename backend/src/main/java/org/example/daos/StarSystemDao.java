package org.example.daos;

import org.example.exceptions.DaoException;
import org.example.models.StarSystem;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class StarSystemDao
{
    private final JdbcTemplate jdbcTemplate;

    public StarSystemDao(DataSource ds) { this.jdbcTemplate = new JdbcTemplate(ds); }

    public List<StarSystem> getAllStarSystems()
    {
        String query = "SELECT * FROM star_systems ORDER BY owner_username";
        return jdbcTemplate.query(query, this::mapToStarSystem);
    }
    public List<StarSystem> getAllStarSystems(String name, String starType, Integer planetCount)
    {
        if ((name == null || name.isBlank()) && starType == null && planetCount == null)
            return getAllStarSystems();

        String query = "SELECT * FROM star_systems ss WHERE ";
        List<Object> params = new ArrayList<>();
        boolean newQ = true;
        if(name != null) {
            query += "name LIKE CONCAT('%', ?, '%')";
            params.add(name);
            newQ = false;
        }
        if(starType != null) {
            query += "star_type LIKE CONCAT('%', ?, '%')";
            params.add(starType);
            newQ = false;
        }
        if(planetCount != null) {
            query += (newQ? "" : "AND ") +
                    "(SELECT COUNT(*) FROM planets p WHERE p.system_id = ss.id) = ? " +
                    "AND p.is_public = true";
            params.add(planetCount);
        }

        return jdbcTemplate.query(query, this::mapToStarSystem, params.toArray());
    }
    public List<StarSystem> getAllStarSystems(String username)
    {
        String query = "SELECT * FROM star_systems WHERE is_public = true";
        List<Object> params = new ArrayList<>();
        if (username != null) {
            query += " OR owner_username = ?";
            params.add(username);
        }
        return jdbcTemplate.query(query, this::mapToStarSystem, params.toArray());
    }
    public List<StarSystem> getAllStarSystems(String name, String starType, Integer planetCount, String username)
    {
        if ((name == null || name.isBlank()) && starType == null && planetCount == null)
            return getAllStarSystems(username);

        String query = "SELECT * FROM star_systems ss WHERE (is_public = true";

        List<Object> params = new ArrayList<>();

        if (username != null) {
            query += " OR owner_username = ?";
            params.add(username);
        }
        query += ")";
        if(name != null) {
            query += " AND name LIKE CONCAT('%', ?, '%')";
            params.add(name);
        }
        if(starType != null) {
            query += " AND star_type LIKE CONCAT('%', ?, '%')";
            params.add(starType);
        }
        if(planetCount != null) {
            query +=" AND (SELECT COUNT(*) FROM planets p WHERE p.system_id = ss.id AND p.is_public = true) = ?";
            params.add(planetCount);
        }

        return jdbcTemplate.query(query, this::mapToStarSystem, params.toArray());
    }
    public StarSystem getStarSystemByID(int ID)
    {
        String query = "SELECT * FROM star_systems WHERE id = ?";
        try { return jdbcTemplate.queryForObject(query, this::mapToStarSystem, ID); }
        catch (EmptyResultDataAccessException e) { return null; }
    }
    public StarSystem getStarSystemByID(int ID, String username)
    {
        String query = "SELECT * FROM star_systems WHERE id = ? AND (is_public = true";
        List<Object> params = new ArrayList<>();
        params.add(ID);
        if (username != null) {
            query += " OR owner_username = ?";
            params.add(username);
        }
        query += ")";
        try { return jdbcTemplate.queryForObject(query, this::mapToStarSystem, params.toArray()); }
        catch (EmptyResultDataAccessException e) { return null; }
    }
    public List<StarSystem.Designator> getAllDesignators()
    {
        String query = "SELECT id, name FROM star_systems";
        return jdbcTemplate.query(query, this::mapToDesignator);
    }
    public List<StarSystem.Designator> getAllDesignators(String username)
    {
        String query = "SELECT id, name FROM star_systems WHERE is_public = true";

        List<Object> params = new ArrayList<>();
        if (username != null) {
            query += " OR owner_username = ?";
            params.add(username);
        }

        return jdbcTemplate.query(query, this::mapToDesignator, params.toArray());
    }
    public StarSystem.Designator getDesignatorByID(int ID)
    {
        String query = "SELECT id, name FROM star_systems WHERE id = ?";

        try { return jdbcTemplate.queryForObject(query, this::mapToDesignator, ID); }
        catch (EmptyResultDataAccessException e) { return null; }
    }
    public StarSystem.Designator getDesignatorByID(int ID, String username)
    {
        String query = "SELECT id, name FROM star_systems WHERE id = ? AND (is_public = ?";
        List<Object> params = new ArrayList<>();
        params.add(ID);
        if (username != null) {
            query += " OR owner_username = ?";
            params.add(username);
        }
        query += ")";

        try { return jdbcTemplate.queryForObject(query, this::mapToDesignator, params.toArray()); }
        catch (EmptyResultDataAccessException e) { return null; }
    }

    public StarSystem createStarSystem(StarSystem s, String username)
    {
        String query = """
                INSERT INTO star_systems (name, star_type, star_mass, distance_from_Earth, description, is_public, owner_username)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getStarType());
            ps.setBigDecimal(3, s.getStarMass());
            ps.setBigDecimal(4, s.getDistanceFromEarth());
            ps.setString(5, s.getDescription());
            ps.setBoolean(6, s.isPublicSystem());
            ps.setString(7, username);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if(rs.next()) s.setID(rs.getInt(1));
                else throw new DaoException("Failed to generate star system ID");
            }
        } catch (SQLException sqle) { throw new DaoException("Failed to form star system"); }
        return getStarSystemByID(s.getID());
    }
    public StarSystem updateStarSystemAdmin(StarSystem s)
    {
        String query = """
                UPDATE star_systems
                SET name = ?, star_type = ?, star_mass = ?, distance_from_Earth = ?,
                    description = ?, owner_username = ?, is_public = ?
                WHERE id = ?
                """;
        int rows = jdbcTemplate.update(query, s.getName(), s.getStarType(), s.getStarMass(),
                s.getDistanceFromEarth(), s.getDescription(), s.getOwnerUsername(), s.isPublicSystem(), s.getID());
        if (rows == 0) return null;
        return getStarSystemByID(s.getID());
    }
    public StarSystem updateStarSystem(StarSystem s)
    {
        String query = """
                UPDATE star_systems
                SET name = ?, star_type = ?, star_mass = ?, distance_from_Earth = ?,
                    description = ?, is_public = ?
                WHERE id = ?
                """;
        int rows = jdbcTemplate.update(query, s.getName(), s.getStarType(), s.getStarMass(),
                s.getDistanceFromEarth(), s.getDescription(), s.isPublicSystem(), s.getID());
        if (rows == 0) return null;
        return getStarSystemByID(s.getID());
    }
    @Transactional
    public int destroyStarSystem(int ID)
    {
        String roguePlanets = "UPDATE planets SET distance_from_star = NULL WHERE system_id = ?";
        jdbcTemplate.update(roguePlanets, ID);

        String query = "DELETE FROM star_systems WHERE id = ?";
        return jdbcTemplate.update(query, ID);
    }

    private StarSystem mapToStarSystem(ResultSet rs, int row) throws SQLException
    {
        return new StarSystem(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("star_type"),
                rs.getBigDecimal("star_mass"),
                rs.getBigDecimal("distance_from_Earth"),
                rs.getString("description"),
                rs.getBoolean("is_public"),
                rs.getString("owner_username")
        );
    }
    private StarSystem.Designator mapToDesignator(ResultSet rs, int row) throws SQLException
    {
        return new StarSystem.Designator(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
