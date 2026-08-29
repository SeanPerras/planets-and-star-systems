package support;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

public class PlanetsAndStarSystemsTest {
    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;
    @Autowired
    protected DataSource dataSource;

    protected String getBaseUrl() { return "http://localhost:" + port; }

    protected void executeSql(String sql) throws SQLException
    {
        try(var statement = dataSource.getConnection().createStatement()) {
            statement.execute(sql);
        }
    }

    protected JdbcTemplate getJdbcTemplate() { return new JdbcTemplate(dataSource); }

    @BeforeEach
    public void setUp() throws SQLException, IOException
    {
        var connection = dataSource.getConnection();
        var reader = new java.io.InputStreamReader(
                PlanetsAndStarSystemsTest.class.getResource
                        ("/create-database.sql").openStream()
        );
        var sr = new ScriptRunner(connection);
        sr.setStopOnError(true);
        sr.setLogWriter(null);
        sr.setErrorLogWriter(null);
        sr.runScript(reader);
        connection.close();
    }

    protected HttpEntity<Object> GetAuthEntity(String username, String password)
    {
        var user = new LoginRequest(username, password);
        var loginResult = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/auth/login",
                user,
                LoginResponse.class
        );

        if (loginResult.getStatusCode() != HttpStatus.OK)
            throw new RuntimeException("Failed to login: " + loginResult.getStatusCode());

        var token = Objects.requireNonNull(loginResult.getBody()).getAccessToken().getToken();
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }
    protected <T> HttpEntity<T> GetAnonEntity()
    {
        var headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }

    protected <T> HttpEntity<T> GetAuthEntity(String username, String password, T body)
    {
        var user = new LoginRequest(username, password);
        var loginResult = this.restTemplate.postForEntity(
                "http://localhost:" + port + "/auth/login",
                user,
                LoginResponse.class
        );

        assertEquals(HttpStatus.OK, loginResult.getStatusCode());
        var token = Objects.requireNonNull(loginResult.getBody()).getAccessToken().getToken();
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
    protected <T> HttpEntity<T> GetAnonEntity(T body)
    {
        var headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}