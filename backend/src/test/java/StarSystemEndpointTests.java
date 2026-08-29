import org.example.SpringBootApplication;
import org.example.models.StarSystem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import support.FinalProjectConfiguration;
import support.PlanetsAndStarSystemsTest;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringBootApplication.class)
@Import(FinalProjectConfiguration.class)
public class StarSystemEndpointTests extends PlanetsAndStarSystemsTest
{
    private void assertStarSystemEquals(StarSystem s, int ID, String name, String starType,
                                        String starMass, String distance,
                                        String description, Boolean publicSystem, String owner)
    {
        assertEquals(ID, s.getID());
        assertEquals(name, s.getName());
        assertEquals(starType, s.getStarType());
        assertEquals(0, new BigDecimal(starMass).compareTo(s.getStarMass()));
        if(distance != null) assertEquals(0, new BigDecimal(distance).compareTo(s.getDistanceFromEarth()));
        else assertNull(s.getDistanceFromEarth());
        assertEquals(description, s.getDescription());
        assertEquals(publicSystem, s.isPublicSystem());
        assertEquals(owner, s.getOwnerUsername());
    }

    @Nested
    @DisplayName("GET-All Requests")
    class GET_All_Tests
    {
        @Test
        @DisplayName("GET /api/star-systems as ADMIN should return all star systems")
        public void getStarSystemsAsADMINShouldReturnAllStarSystems()
        {
            var requestEntity = GetAuthEntity("test-admin", "admin");
            var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems",
                    HttpMethod.GET, requestEntity, StarSystem[].class);
            var responseCode = result.getStatusCode();
            var starSystems = result.getBody();
            assertNotNull(starSystems);

            assertEquals(HttpStatus.OK, responseCode);
            assertStarSystemEquals(starSystems[0], 1, "Our Solar System", "Yellow Main-Sequence", 
                    "1", null, "Home sweet home.", true, "test-admin");
            assertStarSystemEquals(starSystems[1], 2, "Kepler-452", "Yellow Main-Sequence",
                    "1.04", "1810", "A sun-like star with a potentially habitable world.",
                    false, "test-user");
        }
        @Test
        @DisplayName("GET /api/star-systems as owner USER should return public and USER's private star systems")
        public void getStarSystemsAsOwnerUSERShouldReturnPublicAndUSERsPrivateStarSystems()
        {
            var requestEntity = GetAuthEntity("test-user", "user");
            var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems",
                    HttpMethod.GET, requestEntity, StarSystem[].class);
            var responseCode = result.getStatusCode();
            var starSystems = result.getBody();
            assertNotNull(starSystems);

            assertEquals(HttpStatus.OK, responseCode);
            assertStarSystemEquals(starSystems[0], 1, "Our Solar System", "Yellow Main-Sequence",
                    "1", null, "Home sweet home.", true, "test-admin");
            assertStarSystemEquals(starSystems[1], 2, "Kepler-452", "Yellow Main-Sequence",
                    "1.04", "1810", "A sun-like star with a potentially habitable world.",
                    false, "test-user");
        }
        @Test
        @DisplayName("GET /api/star-systems as anonymous should return only public star systems")
        public void getStarSystemsAsAnonymousShouldReturnOnlyPublicStarSystems()
        {
            var requestEntity = GetAnonEntity();
            var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems",
                    HttpMethod.GET, requestEntity, StarSystem[].class);
            var responseCode = result.getStatusCode();
            var starSystems = result.getBody();
            assertNotNull(starSystems);

            assertEquals(HttpStatus.OK, responseCode);
            assertEquals(1, starSystems.length);
            assertStarSystemEquals(starSystems[0], 1, "Our Solar System", "Yellow Main-Sequence",
                    "1", null, "Home sweet home.", true, "test-admin");
        }
        @Test
        @DisplayName("GET /api/star-systems/designators as ADMIN should return all star system designators")
        public void getStarSystemDesignatorsAsADMINShouldReturnAllStarSystemDesignators()
        {
            var requestEntity = GetAuthEntity("test-admin", "admin");
            var result = StarSystemEndpointTests.this.restTemplate.exchange(
                    getBaseUrl() + "/api/star-systems/designators",
                    HttpMethod.GET, requestEntity, StarSystem.Designator[].class
            );
            var responseCode = result.getStatusCode();
            var starSystemDesignators = result.getBody();
            assertNotNull(starSystemDesignators);

            assertEquals(HttpStatus.OK, responseCode);
            assertEquals(starSystemDesignators[0].ID(), 1);
            assertEquals(starSystemDesignators[0].name(), "Our Solar System");
            assertEquals(starSystemDesignators[1].ID(), 2);
            assertEquals(starSystemDesignators[1].name(), "Kepler-452");
        }
        @Test
        @DisplayName("GET /api/star-systems/designators as owner USER should return public and USER's private star system designators")
        public void getStarSystemDesignatorsAsOwnerUSERShouldReturnPublicAndUSERsPrivateStarSystemDesignators()
        {
            var requestEntity = GetAuthEntity("test-user", "user");
            var result = StarSystemEndpointTests.this.restTemplate.exchange(
                    getBaseUrl() + "/api/star-systems/designators",
                    HttpMethod.GET, requestEntity, StarSystem.Designator[].class
            );
            var responseCode = result.getStatusCode();
            var starSystemDesignators = result.getBody();
            assertNotNull(starSystemDesignators);

            assertEquals(HttpStatus.OK, responseCode);
            assertEquals(starSystemDesignators[0].ID(), 1);
            assertEquals(starSystemDesignators[0].name(), "Our Solar System");
            assertEquals(starSystemDesignators[1].ID(), 2);
            assertEquals(starSystemDesignators[1].name(), "Kepler-452");
        }
        @Test
        @DisplayName("GET /api/star-systems/designators as anonymous should return only public star system designators")
        public void getStarSystemDesignatorsAsAnonymousShouldReturnOnlyPublicStarSystemDesignators()
        {
            var requestEntity = GetAnonEntity();
            var result = StarSystemEndpointTests.this.restTemplate.exchange(
                    getBaseUrl() + "/api/star-systems/designators",
                    HttpMethod.GET, requestEntity, StarSystem.Designator[].class
            );
            var responseCode = result.getStatusCode();
            var starSystemDesignators = result.getBody();
            assertNotNull(starSystemDesignators);

            assertEquals(HttpStatus.OK, responseCode);
            assertEquals(starSystemDesignators[0].ID(), 1);
            assertEquals(starSystemDesignators[0].name(), "Our Solar System");
        }
        @Test
        @DisplayName("GET /api/star-systems?name=Sol should return Our Solar System regardless of user")
        public void getStarSystemsWithNameContaining_Sol_ShouldReturnSolarSystem()
        {
            var requestEntities = Arrays.asList(
                    GetAuthEntity("test-admin", "admin"),
                    GetAuthEntity("test-user", "user"),
                    GetAnonEntity()
            );
            for(var requestEntity : requestEntities){
                var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems?name=Sol",
                        HttpMethod.GET, requestEntity, StarSystem[].class);
                var responseCode = result.getStatusCode();
                var starSystems = result.getBody();
                assertNotNull(starSystems);

                assertEquals(HttpStatus.OK, responseCode);
                assertEquals(1, starSystems.length);
                assertStarSystemEquals(starSystems[0], 1, "Our Solar System", "Yellow Main-Sequence",
                        "1", null, "Home sweet home.", true, "test-admin");
            }
        }
        @Test
        @DisplayName("GET /api/star-systems?starType=Yellow as ADMIN or owner USER should return both star systems")
        public void getStarSystemsAsADMINOrOwnerUSERWithStarTypeContaining_Yellow_ShouldReturnBothStarSystems()
        {
            var requestEntities = Arrays.asList(
                    GetAuthEntity("test-admin", "admin"),
                    GetAuthEntity("test-user", "user")
            );
            for(var requestEntity : requestEntities){
                var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems?starType=Yellow",
                        HttpMethod.GET, requestEntity, StarSystem[].class);
                var responseCode = result.getStatusCode();
                var starSystems = result.getBody();
                assertNotNull(starSystems);

                assertEquals(HttpStatus.OK, responseCode);
                assertEquals(2, starSystems.length);
                assertStarSystemEquals(starSystems[0], 1, "Our Solar System", "Yellow Main-Sequence",
                        "1", null, "Home sweet home.", true, "test-admin");
                assertStarSystemEquals(starSystems[1], 2, "Kepler-452", "Yellow Main-Sequence",
                        "1.04", "1810", "A sun-like star with a potentially habitable world.",
                        false, "test-user");
            }
        }
        @Test
        @DisplayName("GET /api/star-systems?starType=Yellow as Anonymous should return just Solar System")
        public void getStarSystemsAsAnonymousWithStarTypeContaining_Yellow_ShouldReturnOnlySolarSystem()
        {
            var requestEntity = GetAnonEntity();
            var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems?starType=Yellow",
                    HttpMethod.GET, requestEntity, StarSystem[].class);
            var responseCode = result.getStatusCode();
            var starSystems = result.getBody();
            assertNotNull(starSystems);

            assertEquals(HttpStatus.OK, responseCode);
            assertEquals(1, starSystems.length);
            assertStarSystemEquals(starSystems[0], 1, "Our Solar System", "Yellow Main-Sequence",
                    "1", null, "Home sweet home.", true, "test-admin");
        }
    }
    @Nested
    @DisplayName("GET-One Requests")
    class GET_One_Tests
    {
        @Test
        @DisplayName("GET /api/star-systems/1 should return the first star system, Ours, regardless of user")
        public void getStarSystemShouldReturnThirdStarSystem()
        {
            var requestEntities = Arrays.asList(
                    GetAuthEntity("test-admin", "admin"),
                    GetAuthEntity("test-user", "user"),
                    GetAnonEntity()
            );
            for (var requestEntity : requestEntities){
                var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems/1",
                        HttpMethod.GET, requestEntity, StarSystem.class);
                var responseCode = result.getStatusCode();
                var starSystem = result.getBody();
                assertNotNull(starSystem);

                assertEquals(HttpStatus.OK, responseCode);
            }
        }
        @Test
        @DisplayName("GET /api/star-systems/2 as anonymous should return 404")
        public void getPrivateStarSystemAsAnonymousShouldReturn404()
        {
            var requestEntity = GetAnonEntity();
            var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems/2",
                    HttpMethod.GET, requestEntity, StarSystem.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.NOT_FOUND, responseCode);
        }
        @Test
        @DisplayName("GET /api/star-systems/2 as ADMIN or owner USER should not return 404")
        public void getPrivateStarSystemAsADMINOrOwnerUSERShouldNotReturn404()
        {
            var requestEntities = Arrays.asList(
                    GetAuthEntity("test-admin", "admin"),
                    GetAuthEntity("test-user", "user")
            );
            for(var requestEntity : requestEntities){
                var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems/2",
                        HttpMethod.GET, requestEntity, StarSystem.class);
                var responseCode = result.getStatusCode();
                assertNotEquals(HttpStatus.NOT_FOUND, responseCode);
                var starSystem = result.getBody();
                assertStarSystemEquals(starSystem, 2, "Kepler-452", "Yellow Main-Sequence",
                        "1.04", "1810", "A sun-like star with a potentially habitable world.",
                        false, "test-user");
            }
        }
    }

    @Nested
    @DisplayName("POST Requests")
    class POST_Tests
    {
        @Test
        @DisplayName("POST /api/star-systems as ADMIN should create a new star system")
        public void postStarSystemAsADMINShouldCreateNewStarSystem()
        {
            var postedStarSystem = new StarSystem(0, "Veyra-9", "Blue Supergiant", new BigDecimal("18.7"),
                    new BigDecimal("42.3"), "A blazing blue giant at the edge of the known chart.",
                    true, "test-admin");
            var requestEntity = GetAuthEntity("test-admin", "admin", postedStarSystem);
            var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems",
                    HttpMethod.POST, requestEntity, StarSystem.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.CREATED, responseCode);
            var starSystem = result.getBody();
            assertStarSystemEquals(starSystem, 3, "Veyra-9", "Blue Supergiant", "18.7",
                    "42.3", "A blazing blue giant at the edge of the known chart.",
                    true, "test-admin");
        }
        @Test
        @DisplayName("POST /api/star-systems as USER should create a new star system")
        public void postStarSystemAsUSERShouldCreateNewStarSystem()
        {
            var postedStarSystem = new StarSystem(0, "Veyra-9", "Blue Supergiant", new BigDecimal("18.7"),
                    new BigDecimal("42.3"), "A blazing blue giant at the edge of the known chart.",
                    true, "test-user");
            var requestEntity = GetAuthEntity("test-user", "user", postedStarSystem);
            var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems",
                    HttpMethod.POST, requestEntity, StarSystem.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.CREATED, responseCode);
            var starSystem = result.getBody();
            assertStarSystemEquals(starSystem, 3, "Veyra-9", "Blue Supergiant", "18.7",
                    "42.3", "A blazing blue giant at the edge of the known chart.",
                    true, "test-user");
        }
        @Test
        @DisplayName("POST /api/star-systems as anonymous should NOT create a new star system")
        public void postStarSystemAsAnonymousShouldNotCreateNewStarSystem()
        {
            var postedStarSystem = new StarSystem(0, "Veyra-9", "Blue Supergiant", new BigDecimal("18.7"),
                    new BigDecimal("42.3"), "A blazing blue giant at the edge of the known chart.",
                    true, "test-user");
            var requestEntity = GetAnonEntity(postedStarSystem);
            var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems",
                    HttpMethod.POST, requestEntity, StarSystem.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.UNAUTHORIZED, responseCode);
            var starSystem = result.getBody();
            assertNull(starSystem);
        }
        @Test
        @DisplayName("POST /api/star-systems as ADMIN or User with negative mass should return 400")
        public void postStarSystemAsADMINOrUSERWithNegativeMassShouldReturn400()
        {
            var postedSystem = new StarSystem(0, "Negative Mass System", "Red Main-Sequence",
                    new BigDecimal("-1"), null, "Invalid mass.", true,
                    "test-admin");
            var requestEntities = Arrays.asList(
                    GetAuthEntity("test-admin", "admin", postedSystem),
                    GetAuthEntity("test-user", "user", postedSystem)
            );
            for(var requestEntity : requestEntities){
                var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems",
                        HttpMethod.POST, requestEntity, String.class);
                var responseCode = result.getStatusCode();
                assertEquals(HttpStatus.BAD_REQUEST, responseCode);
            }
        }

        @Test
        @DisplayName("POST /api/star-systems as Anonymous with negative mass should still return 401")
        public void postStarSystemAsAnonymousWithNegativeMassShouldReturn401()
        {
            var postedSystem = new StarSystem(0, "Negative Mass System", "Red Main-Sequence",
                    new BigDecimal("-1"), null, "Invalid mass.", true, "test-admin");
            var requestEntity = GetAnonEntity(postedSystem);
            var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems",
                    HttpMethod.POST, requestEntity, String.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.UNAUTHORIZED, responseCode);
        }
    }

    @Nested
    @DisplayName("PUT Requests")
    class PUT_Tests
    {
        @Test
        @DisplayName("PUT /api/star-systems/2 as ADMIN should update the second star system")
        public void putStarSystemAsADMINShouldUpdateTenthStarSystem()
        {
            var putStarSystem = new StarSystem(0, "Veyra-9", "Blue Supergiant", new BigDecimal("18.7"),
                    new BigDecimal("42.3"), "A blazing blue giant at the edge of the known chart.",
                    true, "test-user");
            var requestEntity = GetAuthEntity("test-admin", "admin", putStarSystem);
            var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems/2",
                    HttpMethod.PUT, requestEntity, StarSystem.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.OK, responseCode);
            var starSystem = result.getBody();
            assertNotNull(starSystem);

            assertStarSystemEquals(starSystem, 2, "Veyra-9", "Blue Supergiant", "18.7",
                    "42.3", "A blazing blue giant at the edge of the known chart.",
                    true, "test-user");
        }
        @Test
        @DisplayName("PUT /api/star-systems/2 as owner USER should update the second star system")
        public void putStarSystemAsOwnerUSERShouldUpdateTenthStarSystem()
        {
            var putStarSystem = new StarSystem(0, "Veyra-9", "Blue Supergiant", new BigDecimal("18.7"),
                    new BigDecimal("42.3"), "A blazing blue giant at the edge of the known chart.",
                    true, "test-user");
            var requestEntity = GetAuthEntity("test-user", "user", putStarSystem);
            var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems/2",
                    HttpMethod.PUT, requestEntity, StarSystem.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.OK, responseCode);
            var starSystem = result.getBody();
            assertNotNull(starSystem);

            //OwnerUsername should NOT be 'test-admin' as StarSystemController overrides the username
            // of a non-admin sender with the username of the DB star system.
            assertStarSystemEquals(starSystem, 2, "Veyra-9", "Blue Supergiant", "18.7",
                    "42.3", "A blazing blue giant at the edge of the known chart.",
                    true, "test-user");
        }
        @Test
        @DisplayName("PUT /api/star-systems/2 as anonymous should NOT update the second star system")
        public void putStarSystemAsAnonymousShouldNotUpdateTenthStarSystem()
        {
            var putStarSystem = new StarSystem(0, "Veyra-9", "Blue Supergiant", new BigDecimal("18.7"),
                    new BigDecimal("42.3"), "A blazing blue giant at the edge of the known chart.",
                    true, "test-user");
            var requestEntity = GetAnonEntity(putStarSystem);
            var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems/2",
                    HttpMethod.PUT, requestEntity, StarSystem.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.UNAUTHORIZED, responseCode);
            var starSystem = result.getBody();
            assertNull(starSystem);
        }
        @Test
        @DisplayName("PUT /api/star-systems/3 as ADMIN or USER should return 404")
        public void putStarSystemAsADMINOrUSERShouldReturn404() {
            var putStarSystem = new StarSystem(0, "Veyra-9", "Blue Supergiant", new BigDecimal("18.7"),
                    new BigDecimal("42.3"), "A blazing blue giant at the edge of the known chart.",
                    true, "test-user");
            var requestEntities = Arrays.asList(
                    GetAuthEntity("test-admin", "admin", putStarSystem),
                    GetAuthEntity("test-user", "user", putStarSystem)
            );
            for(var requestEntity : requestEntities) {
                var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems/3",
                        HttpMethod.PUT, requestEntity, StarSystem.class);
                var responseCode = result.getStatusCode();
                assertEquals(HttpStatus.NOT_FOUND, responseCode);
            }
        }
        @Test
        @DisplayName("PUT /api/star-systems/3 as anonymous should return 401")
        public void putStarSystemAsAnonymousShouldReturn401() {
            var putStarSystem = new StarSystem(0, "Veyra-9", "Blue Supergiant", new BigDecimal("18.7"),
                    new BigDecimal("42.3"), "A blazing blue giant at the edge of the known chart.",
                    true, "test-user");
            var requestEntity = GetAnonEntity(putStarSystem);
            var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems/3",
                    HttpMethod.PUT, requestEntity, StarSystem.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.UNAUTHORIZED, responseCode);
        }
        @Test
        @DisplayName("PUT /api/star-systems/1 as ADMIN or User with negative mass should return 400")
        public void postStarSystemAsADMINOrUSERWithNegativeMassShouldReturn400()
        {
            var putSystem = new StarSystem(2, "Negative Mass System", "Red Main-Sequence",
                    new BigDecimal("-1"), null, "Invalid mass.", true,
                    "");
            var requestEntities = Arrays.asList(
                    GetAuthEntity("test-admin", "admin", putSystem),
                    GetAuthEntity("test-user", "user", putSystem)
            );
            for(var requestEntity : requestEntities){
                var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems/2",
                        HttpMethod.PUT, requestEntity, String.class);
                var responseCode = result.getStatusCode();
                assertEquals(HttpStatus.BAD_REQUEST, responseCode);
            }
        }
        @Test
        @DisplayName("PUT /api/star-systems as Anonymous with negative mass should still return 401")
        public void postStarSystemAsAnonymousWithNegativeMassShouldReturn401()
        {
            var postedSystem = new StarSystem(1, "Negative Mass System", "Red Main-Sequence",
                    new BigDecimal("-1"), null, "Invalid mass.", true,
                    "test-admin");
            var requestEntity = GetAnonEntity(postedSystem);
            var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems/1",
                    HttpMethod.PUT, requestEntity, String.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.UNAUTHORIZED, responseCode);
        }
    }

    @Nested
    @DisplayName("DELETE Requests")
    class DELETE_Tests
    {
        @Test
        @DisplayName("DELETE /api/star-systems/2 as ADMIN should delete the second star system")
        public void deleteStarSystemAsADMINShouldDeleteTenthStarSystem()
        {
            var requestEntity = GetAuthEntity("test-admin", "admin");
            var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems/2",
                    HttpMethod.DELETE, requestEntity, Integer.class);
            var responseCode = result.getStatusCode();
            var affectedRows = result.getBody();
            assertEquals(HttpStatus.OK, responseCode);
            assertEquals(1, affectedRows);
        }
        @Test
        @DisplayName("DELETE /api/star-systems/2 as owner USER should delete the second star system")
        public void deleteStarSystemAsOwnerUSERShouldDeleteTenthStarSystem()
        {
            var requestEntity = GetAuthEntity("test-user", "user");
            var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems/2",
                    HttpMethod.DELETE, requestEntity, Integer.class);
            var responseCode = result.getStatusCode();
            var affectedRows = result.getBody();
            assertEquals(HttpStatus.OK, responseCode);
            assertEquals(1, affectedRows);
        }
        @Test
        @DisplayName("DELETE /api/star-systems/2 as anonymous should return 401")
        public void deleteStarSystemAsAnonymousShouldNotDeleteTenthStarSystem()
        {
            var requestEntity = GetAnonEntity();
            var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems/2",
                    HttpMethod.DELETE, requestEntity, Integer.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.UNAUTHORIZED, responseCode);
        }
        @Test
        @DisplayName("DELETE /api/star-systems/3 as ADMIN or owner USER should return 404")
        public void deleteStarSystemAsADMINOrUSERShouldReturn404() {
            var requestEntities = Arrays.asList(
                    GetAuthEntity("test-admin", "admin"),
                    GetAuthEntity("test-user", "user")
            );
            for(var requestEntity : requestEntities) {
                var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems/3",
                        HttpMethod.DELETE, requestEntity, HttpEntity.class);
                var responseCode = result.getStatusCode();
                assertEquals(HttpStatus.NOT_FOUND, responseCode);
            }
        }
        @Test
        @DisplayName("DELETE /api/star-systems/3 as anonymous should return 401")
        public void deleteStarSystemAsAnonymousShouldReturn401() {
            var requestEntity = GetAnonEntity();
            var result = StarSystemEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/star-systems/3",
                    HttpMethod.DELETE, requestEntity, HttpEntity.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.UNAUTHORIZED, responseCode);
        }
    }
}
