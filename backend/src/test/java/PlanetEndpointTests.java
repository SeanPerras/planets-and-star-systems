import org.example.SpringBootApplication;
import org.example.models.Planet;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringBootApplication.class)
@Import(FinalProjectConfiguration.class)
public class PlanetEndpointTests extends PlanetsAndStarSystemsTest
{
    private void assertPlanetEquals(Planet p, int ID, String name, String type, String mass, String distance,
                                    String description, String moons, Boolean publicPlanet, String owner, Integer systemId)
    {
        assertEquals(ID, p.getID());
        assertEquals(name, p.getName());
        assertEquals(type, p.getType());
        assertEquals(0, new BigDecimal(mass).compareTo(p.getMass()));
        if(distance == null) assertNull(systemId);
        else assertEquals(0, new BigDecimal(distance).compareTo(p.getDistanceFromStar()));
        assertEquals(description, p.getDescription());
        assertEquals(moons, p.getMoons());
        assertEquals(publicPlanet, p.isPublicPlanet());
        assertEquals(owner, p.getOwnerUsername());
        if(systemId == null) assertNull(distance);
        else assertEquals(systemId, p.getSystemID());
    }

    @Nested
    @DisplayName("GET-All Requests")
    class GET_All_Tests
    {
        @Test
        @DisplayName("GET /api/planets as ADMIN should return all planets")
        public void getPlanetsAsADMINShouldReturnAllPlanets()
        {
            var requestEntity = GetAuthEntity("test-admin", "admin");
            var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets",
                    HttpMethod.GET, requestEntity, Planet[].class);
            var responseCode = result.getStatusCode();
            var planets = result.getBody();
            assertNotNull(planets);

            assertEquals(HttpStatus.OK, responseCode);
            assertPlanetEquals(planets[0], 1, "Mercury", "Terrestrial", "0.055", "0.39",
                    "The first planet.", "[]", true, "test-admin", 1);
            assertPlanetEquals(planets[1], 2, "Venus", "Terrestrial", "0.815", "0.72",
                    "Our warm sibling.", "[]", true, "test-admin", 1);
            assertPlanetEquals(planets[2], 3, "Earth", "Terrestrial", "1.000", "1.000",
                    "The Pale Blue Dot.", "[\"The Moon\"]", true, "test-admin", 1);
            assertPlanetEquals(planets[3], 4, "Mars", "Terrestrial", "0.107", "1.52",
                    "Our weird sibling.", "[\"Phobos\", \"Deimos\"]", true, "test-admin", 1);
            assertPlanetEquals(planets[4], 5, "Jupiter", "Gas Giant", "317.8", "5.2",
                    "The big one!", "[\"Ganymede\", \"95 more!\"]", true, "test-admin", 1);
            assertPlanetEquals(planets[5], 6, "Saturn", "Gas Giant", "95.2", "9.54",
                    "The ring-bearer.", "[\"Titan\", \"146 more!\"]", true, "test-admin", 1);
            assertPlanetEquals(planets[6], 7, "Uranus", "Ice Giant", "14.5", "19.2",
                    "The butt of all the jokes.", "[\"Titania\", \"27 more!\"]", true,
                    "test-admin", 1);
            assertPlanetEquals(planets[7], 8, "Neptune", "Ice Giant", "17.1", "30.06",
                    "The other blue guy.", "[\"Triton\", \"14 more!\"]", true,
                    "test-admin", 1);
            assertPlanetEquals(planets[8], 9, "Pluto", "Dwarf", "0.002", "39.5",
                    "The little guy.", "[\"Charon\", \"Styx\", \"Nix\", \"Kerberos\", \"Hydra\"]",
                    true, "test-admin", 1);
            assertPlanetEquals(planets[9], 10, "Shadow Planet", "Unknown", "0.5", null,
                    "A hidden world.", "[]", false, "test-user", null);
        }
        @Test
        @DisplayName("GET /api/planets as owner should return public and USER's private planets")
        public void getPlanetsAsOwnerShouldReturnPublicAndUSERsPrivatePlanets()
        {
            var requestEntity = GetAuthEntity("test-user", "user");
            var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets",
                    HttpMethod.GET, requestEntity, Planet[].class);
            var responseCode = result.getStatusCode();
            var planets = result.getBody();
            assertNotNull(planets);

            assertEquals(HttpStatus.OK, responseCode);
            assertPlanetEquals(planets[0], 1, "Mercury", "Terrestrial", "0.055", "0.39",
                    "The first planet.", "[]", true, "test-admin", 1);
            assertPlanetEquals(planets[1], 2, "Venus", "Terrestrial", "0.815", "0.72",
                    "Our warm sibling.", "[]", true, "test-admin", 1);
            assertPlanetEquals(planets[2], 3, "Earth", "Terrestrial", "1.000", "1.000",
                    "The Pale Blue Dot.", "[\"The Moon\"]", true, "test-admin", 1);
            assertPlanetEquals(planets[3], 4, "Mars", "Terrestrial", "0.107", "1.52",
                    "Our weird sibling.", "[\"Phobos\", \"Deimos\"]", true, "test-admin", 1);
            assertPlanetEquals(planets[4], 5, "Jupiter", "Gas Giant", "317.8", "5.2",
                    "The big one!", "[\"Ganymede\", \"95 more!\"]", true, "test-admin", 1);
            assertPlanetEquals(planets[5], 6, "Saturn", "Gas Giant", "95.2", "9.54",
                    "The ring-bearer.", "[\"Titan\", \"146 more!\"]", true, "test-admin", 1);
            assertPlanetEquals(planets[6], 7, "Uranus", "Ice Giant", "14.5", "19.2",
                    "The butt of all the jokes.", "[\"Titania\", \"27 more!\"]", true,
                    "test-admin", 1);
            assertPlanetEquals(planets[7], 8, "Neptune", "Ice Giant", "17.1", "30.06",
                    "The other blue guy.", "[\"Triton\", \"14 more!\"]", true,
                    "test-admin", 1);
            assertPlanetEquals(planets[8], 9, "Pluto", "Dwarf", "0.002", "39.5",
                    "The little guy.", "[\"Charon\", \"Styx\", \"Nix\", \"Kerberos\", \"Hydra\"]",
                    true, "test-admin", 1);
            assertPlanetEquals(planets[9], 10, "Shadow Planet", "Unknown", "0.5", null,
                    "A hidden world.", "[]", false, "test-user", null);
        }
        @Test
        @DisplayName("GET /api/planets as anonymous should return only public planets")
        public void getPlanetsAsAnonymousShouldReturnOnlyPublicPlanets()
        {
            var requestEntity = GetAnonEntity();
            var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets",
                    HttpMethod.GET, requestEntity, Planet[].class);
            var responseCode = result.getStatusCode();
            var planets = result.getBody();
            assertNotNull(planets);

            assertEquals(HttpStatus.OK, responseCode);
            assertEquals(9, planets.length);
            assertPlanetEquals(planets[0], 1, "Mercury", "Terrestrial", "0.055", "0.39",
                    "The first planet.", "[]", true, "test-admin", 1);
            assertPlanetEquals(planets[1], 2, "Venus", "Terrestrial", "0.815", "0.72",
                    "Our warm sibling.", "[]", true, "test-admin", 1);
            assertPlanetEquals(planets[2], 3, "Earth", "Terrestrial", "1.000", "1.000",
                    "The Pale Blue Dot.", "[\"The Moon\"]", true, "test-admin", 1);
            assertPlanetEquals(planets[3], 4, "Mars", "Terrestrial", "0.107", "1.52",
                    "Our weird sibling.", "[\"Phobos\", \"Deimos\"]", true, "test-admin", 1);
            assertPlanetEquals(planets[4], 5, "Jupiter", "Gas Giant", "317.8", "5.2",
                    "The big one!", "[\"Ganymede\", \"95 more!\"]", true, "test-admin", 1);
            assertPlanetEquals(planets[5], 6, "Saturn", "Gas Giant", "95.2", "9.54",
                    "The ring-bearer.", "[\"Titan\", \"146 more!\"]", true, "test-admin", 1);
            assertPlanetEquals(planets[6], 7, "Uranus", "Ice Giant", "14.5", "19.2",
                    "The butt of all the jokes.", "[\"Titania\", \"27 more!\"]", true,
                    "test-admin", 1);
            assertPlanetEquals(planets[7], 8, "Neptune", "Ice Giant", "17.1", "30.06",
                    "The other blue guy.", "[\"Triton\", \"14 more!\"]", true,
                    "test-admin", 1);
            assertPlanetEquals(planets[8], 9, "Pluto", "Dwarf", "0.002", "39.5",
                    "The little guy.", "[\"Charon\", \"Styx\", \"Nix\", \"Kerberos\", \"Hydra\"]",
                    true, "test-admin", 1);
        }
        @Test
        @DisplayName("GET /api/planets?name=M should return only planets with 'M' in the name, regardless of user")
        public void getPlanetsWithNameContaining_M_ShouldReturnOnlyPlanetsWithNameContaining_M_()
        {
            var requestEntities = Arrays.asList(
                    GetAuthEntity("test-admin", "admin"),
                    GetAuthEntity("test-user", "user"),
                    GetAnonEntity()
            );
            for(var requestEntity : requestEntities){
                var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets?name=M",
                        HttpMethod.GET, requestEntity, Planet[].class);
                var responseCode = result.getStatusCode();
                var planets = result.getBody();
                assertNotNull(planets);

                assertEquals(HttpStatus.OK, responseCode);
                assertEquals(2, planets.length);
                assertPlanetEquals(planets[0], 1, "Mercury", "Terrestrial", "0.055", "0.39",
                        "The first planet.", "[]", true, "test-admin", 1);
                assertPlanetEquals(planets[1], 4, "Mars", "Terrestrial", "0.107", "1.52",
                        "Our weird sibling.", "[\"Phobos\", \"Deimos\"]", true, "test-admin", 1);
            }
        }
        @Test
        @DisplayName("GET /api/planets?systemID=1 should return only systemID 1 planets, regardless of user")
        public void getPlanetsWithSystemID1ShouldReturnOnlySystemID1Planets()
        {
            var requestEntities = Arrays.asList(
                    GetAuthEntity("test-admin", "admin"),
                    GetAuthEntity("test-user", "user"),
                    GetAnonEntity()
            );
            for(var requestEntity : requestEntities){
                var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets?systemID=1",
                        HttpMethod.GET, requestEntity, Planet[].class);
                var responseCode = result.getStatusCode();
                var planets = result.getBody();
                assertNotNull(planets);

                assertEquals(HttpStatus.OK, responseCode);
                assertEquals(9, planets.length);
                assertPlanetEquals(planets[0], 1, "Mercury", "Terrestrial", "0.055", "0.39",
                        "The first planet.", "[]", true, "test-admin", 1);
                assertPlanetEquals(planets[1], 2, "Venus", "Terrestrial", "0.815", "0.72",
                        "Our warm sibling.", "[]", true, "test-admin", 1);
                assertPlanetEquals(planets[2], 3, "Earth", "Terrestrial", "1.000", "1.000",
                        "The Pale Blue Dot.", "[\"The Moon\"]", true, "test-admin", 1);
                assertPlanetEquals(planets[3], 4, "Mars", "Terrestrial", "0.107", "1.52",
                        "Our weird sibling.", "[\"Phobos\", \"Deimos\"]", true, "test-admin", 1);
                assertPlanetEquals(planets[4], 5, "Jupiter", "Gas Giant", "317.8", "5.2",
                        "The big one!", "[\"Ganymede\", \"95 more!\"]", true, "test-admin", 1);
                assertPlanetEquals(planets[5], 6, "Saturn", "Gas Giant", "95.2", "9.54",
                        "The ring-bearer.", "[\"Titan\", \"146 more!\"]", true, "test-admin", 1);
                assertPlanetEquals(planets[6], 7, "Uranus", "Ice Giant", "14.5", "19.2",
                        "The butt of all the jokes.", "[\"Titania\", \"27 more!\"]", true,
                        "test-admin", 1);
                assertPlanetEquals(planets[7], 8, "Neptune", "Ice Giant", "17.1", "30.06",
                        "The other blue guy.", "[\"Triton\", \"14 more!\"]", true,
                        "test-admin", 1);
                assertPlanetEquals(planets[8], 9, "Pluto", "Dwarf", "0.002", "39.5",
                        "The little guy.", "[\"Charon\", \"Styx\", \"Nix\", \"Kerberos\", \"Hydra\"]",
                        true, "test-admin", 1);
            }
        }
        @Test
        @DisplayName("GET /api/planets?rogue=true as ADMIN or owner USER should return only Shadow planet")
        public void getPlanetsRogueAsADMINOrOwnerUSERShouldReturnOnlyShadowPlanet()
        {
            var requestEntities = Arrays.asList(
                    GetAuthEntity("test-admin", "admin"),
                    GetAuthEntity("test-user", "user")
            );
            for(var requestEntity : requestEntities){
                var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets?rogue=true",
                        HttpMethod.GET, requestEntity, Planet[].class);
                var responseCode = result.getStatusCode();
                var planets = result.getBody();
                assertNotNull(planets);

                assertEquals(HttpStatus.OK, responseCode);
                assertEquals(1, planets.length);
                assertPlanetEquals(planets[0], 10, "Shadow Planet", "Unknown", "0.5", null,
                        "A hidden world.", "[]", false, "test-user", null);
            }
        }
        @Test
        @DisplayName("GET /api/planets?rogue=true as Anonymous should return no planets")
        public void getPlanetsRogueAsAnonymousShouldReturnNoPlanets()
        {
            var requestEntity = GetAnonEntity();
            var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets?rogue=true",
                    HttpMethod.GET, requestEntity, Planet[].class);
            var responseCode = result.getStatusCode();
            var planets = result.getBody();
            assertNotNull(planets);

            assertEquals(HttpStatus.OK, responseCode);
            assertEquals(0, planets.length);
        }
    }
    @Nested
    @DisplayName("GET-One Requests")
    class GET_One_Tests
    {
        @Test
        @DisplayName("GET /api/planets/3 should return the third planet, Earth, regardless of user")
        public void getPlanetShouldReturnThirdPlanet()
		{
            var requestEntities = Arrays.asList(
                    GetAuthEntity("test-admin", "admin"),
                    GetAuthEntity("test-user", "user"),
                    GetAnonEntity()
            );
            for (var requestEntity : requestEntities){
                var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets/3",
                        HttpMethod.GET, requestEntity, Planet.class);
                var responseCode = result.getStatusCode();
                var planet = result.getBody();
                assertNotNull(planet);

                assertEquals(HttpStatus.OK, responseCode);
                assertPlanetEquals(planet, 3, "Earth", "Terrestrial", "1.000", "1.000",
                        "The Pale Blue Dot.", "[\"The Moon\"]", true, "test-admin", 1);
            }
        }
        @Test
        @DisplayName("GET /api/planets/10 as anonymous should return 404")
        public void getPrivatePlanetAsAnonymousShouldReturn404()
		{
            var requestEntity = GetAnonEntity();
            var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets/10",
                    HttpMethod.GET, requestEntity, Planet.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.NOT_FOUND, responseCode);
        }
        @Test
        @DisplayName("GET /api/planets/10 as ADMIN or owner USER should not return 404")
        public void getPrivatePlanetAsADMINOrOwnerUSERShouldNotReturn404()
		{
            var requestEntities = Arrays.asList(
                    GetAuthEntity("test-admin", "admin"),
                    GetAuthEntity("test-user", "user")
            );
            for(var requestEntity : requestEntities){
                var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets/10",
                        HttpMethod.GET, requestEntity, Planet.class);
                var responseCode = result.getStatusCode();
                assertNotEquals(HttpStatus.NOT_FOUND, responseCode);
                var planet = result.getBody();
                assertPlanetEquals(planet, 10, "Shadow Planet", "Unknown", "0.5", null,
                        "A hidden world.", "[]", false, "test-user", null);
            }
        }
    }

    @Nested
    @DisplayName("POST Requests")
    class POST_Tests
    {
        @Test
        @DisplayName("POST /api/planets as ADMIN should create a new planet")
        public void postPlanetAsADMINShouldCreateNewPlanet()
		{
            var postedPlanet = new Planet(0, "Nomad-X", "Ice Giant", new BigDecimal("12.5"),
                    null, "A wandering world ejected from its birth system.", "[]",
                    false, "test-admin", null);
            var requestEntity = GetAuthEntity("test-admin", "admin", postedPlanet);
            var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets",
                    HttpMethod.POST, requestEntity, Planet.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.CREATED, responseCode);
            var planet = result.getBody();
            assertPlanetEquals(planet, 11, "Nomad-X", "Ice Giant", "12.5", null,
                    "A wandering world ejected from its birth system.", "[]", false,
                    "test-admin", null);
        }
        @Test
        @DisplayName("POST /api/planets as USER should create a new planet")
        public void postPlanetAsUSERShouldCreateNewPlanet()
		{
            var postedPlanet = new Planet(0, "Nomad-X", "Ice Giant", new BigDecimal("12.5"),
                    null, "A wandering world ejected from its birth system.", "[]",
                    false, "test-user", null);
            var requestEntity = GetAuthEntity("test-user", "user", postedPlanet);
            var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets",
                    HttpMethod.POST, requestEntity, Planet.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.CREATED, responseCode);
            var planet = result.getBody();
            assertPlanetEquals(planet, 11, "Nomad-X", "Ice Giant", "12.5", null,
                    "A wandering world ejected from its birth system.", "[]", false,
                    "test-user", null);
        }
        @Test
        @DisplayName("POST /api/planets as anonymous should NOT create a new planet")
        public void postPlanetAsAnonymousShouldNotCreateNewPlanet()
		{
            var postedPlanet = new Planet(0, "Nomad-X", "Ice Giant", new BigDecimal("12.5"),
                    null, "A wandering world ejected from its birth system.", "[]",
                    false, null, null);
            var requestEntity = GetAnonEntity(postedPlanet);
            var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets",
                    HttpMethod.POST, requestEntity, Planet.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.UNAUTHORIZED, responseCode);
            var planet = result.getBody();
            assertNull(planet);
        }
        @Test
        @DisplayName("POST /api/planets as ADMIN or USER a null systemID w/ distanceFromStar should return 400")
        public void postPlanetAsADMINOrUSERRogueWithDistanceFromStarShouldReturn400()
        {
            var postedPlanet = new Planet(0, "Nomad-X", "Ice Giant", new BigDecimal("12.5"),
                    new BigDecimal("1"), "A wandering world ejected from its birth system with an AU.",
                    "[]", false, null, null);
            var requestEntities = Arrays.asList(
                    GetAuthEntity("test-admin", "admin", postedPlanet),
                    GetAuthEntity("test-user", "user", postedPlanet)
            );
            for(var requestEntity : requestEntities){
                var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets",
                        HttpMethod.POST, requestEntity, Planet.class);
                var responseCode = result.getStatusCode();
                assertEquals(HttpStatus.BAD_REQUEST, responseCode);
            }
        }
        @Test
        @DisplayName("POST /api/planets as Anonymous a null systemID w/ distanceFromStar should return 401")
        public void postPlanetAsAnonymousARogueWithDistanceFromStarShouldReturn401()
        {
            var postedPlanet = new Planet(0, "Nomad-X", "Ice Giant", new BigDecimal("12.5"),
                    new BigDecimal("1"), "A wandering world ejected from its birth system with an AU.",
                    "[]", false, null, null);
            var requestEntity = GetAnonEntity(postedPlanet);
            var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets",
                    HttpMethod.POST, requestEntity, Planet.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.UNAUTHORIZED, responseCode);
        }
        @Test
        @DisplayName("POST /api/planets as ADMIN or USER a non-null systemID w/o distanceFromStar should return 400")
        public void postPlanetAsADMINOrUSERWithSystemIDAndDistanceFromStarShouldReturn400()
        {
            var postedPlanet = new Planet(0, "Nomad-X", "Ice Giant", new BigDecimal("12.5"),
                    null, "A wandering world, not yet, ejected from its birth system without an AU.",
                    "[]", false, null, 1);
            var requestEntities = Arrays.asList(
                    GetAuthEntity("test-admin", "admin", postedPlanet),
                    GetAuthEntity("test-user", "user", postedPlanet)
            );
            for (var requestEntity : requestEntities) {
                var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets",
                        HttpMethod.POST, requestEntity, String.class);
                var responseCode = result.getStatusCode();
                assertEquals(HttpStatus.BAD_REQUEST, responseCode);
            }
        }
        @Test
        @DisplayName("POST /api/planets as Anonymous a non-null systemID w/o distanceFromStar should return 401")
        public void postPlanetAsAnonymousWithSystemIDAndDistanceFromStarShouldReturn401()
        {
            var postedPlanet = new Planet(0, "Nomad-X", "Ice Giant", new BigDecimal("12.5"),
                    null, "A wandering world, not yet, ejected from its birth system without an AU.",
                    "[]", false, null, 1);
            var requestEntity = GetAnonEntity(postedPlanet);
            var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets",
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
        @DisplayName("PUT /api/planets/10 as ADMIN should update the tenth planet")
        public void putPlanetAsADMINShouldUpdateTenthPlanet()
		{
            var putPlanet = new Planet(0, "Nomad-X", "Ice Giant", new BigDecimal("12.5"),
                    null, "A wandering world ejected from its birth system.", "[]",
                    false, "test-admin", null);
            var requestEntity = GetAuthEntity("test-admin", "admin", putPlanet);
            var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets/10",
                    HttpMethod.PUT, requestEntity, Planet.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.OK, responseCode);
            var planet = result.getBody();
            assertNotNull(planet);

            assertPlanetEquals(planet, 10, "Nomad-X", "Ice Giant", "12.5",
                    null, "A wandering world ejected from its birth system.", "[]",
                    false, "test-admin", null);
        }
        @Test
        @DisplayName("PUT /api/planets/10 as owner USER should update the tenth planet")
        public void putPlanetAsOwnerUSERShouldUpdateTenthPlanet()
        {
            var putPlanet = new Planet(0, "Nomad-X", "Ice Giant", new BigDecimal("12.5"),
                    null, "A wandering world ejected from its birth system.", "[]",
                    false, "test-admin", null);
            var requestEntity = GetAuthEntity("test-user", "user", putPlanet);
            var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets/10",
                    HttpMethod.PUT, requestEntity, Planet.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.OK, responseCode);
            var planet = result.getBody();
            assertNotNull(planet);

            //OwnerUsername should NOT be 'test-admin' as PlanetController overrides the username
            // of a non-admin sender with the username of the DB planet.
            assertPlanetEquals(planet, 10, "Nomad-X", "Ice Giant", "12.5",
                    null, "A wandering world ejected from its birth system.", "[]",
                    false, "test-user", null);
        }
        @Test
        @DisplayName("PUT /api/planets/10 as anonymous should NOT update the tenth planet")
        public void putPlanetAsAnonymousShouldNotUpdateTenthPlanet()
		{
            var putPlanet = new Planet(0, "Nomad-X", "Ice Giant", new BigDecimal("12.5"),
                    null, "A wandering world ejected from its birth system.", "[]",
                    false, "test-other-user", null);
            var requestEntity = GetAnonEntity(putPlanet);
            var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets/10",
                    HttpMethod.PUT, requestEntity, Planet.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.UNAUTHORIZED, responseCode);
            var planet = result.getBody();
            assertNull(planet);
        }
        @Test
        @DisplayName("PUT /api/planets/11 as ADMIN or USER should return 404")
        public void putPlanetAsADMINOrUSERShouldReturn404() {
            var putPlanet = new Planet(0, "Nomad-X", "Ice Giant", new BigDecimal("12.5"),
                    null, "A wandering world ejected from its birth system.", "[]",
                    false, "test-user", null);
            var requestEntities = Arrays.asList(
                    GetAuthEntity("test-admin", "admin", putPlanet),
                    GetAuthEntity("test-user", "user", putPlanet)
            );
            for(var requestEntity : requestEntities) {
                var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets/11",
                        HttpMethod.PUT, requestEntity, Planet.class);
                var responseCode = result.getStatusCode();
                assertEquals(HttpStatus.NOT_FOUND, responseCode);
            }
        }
        @Test
        @DisplayName("PUT /api/planets/11 as anonymous should return 401")
        public void putPlanetAsAnonymousShouldReturn401() {
            var putPlanet = new Planet(0, "Nomad-X", "Ice Giant", new BigDecimal("12.5"),
                    null, "A wandering world ejected from its birth system.", "[]",
                    false, "test-user", null);
            var requestEntity = GetAnonEntity(putPlanet);
            var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets/11",
                    HttpMethod.PUT, requestEntity, Planet.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.UNAUTHORIZED, responseCode);
        }
    }

    @Nested
    @DisplayName("DELETE Requests")
    class DELETE_Tests
    {
        @Test
        @DisplayName("DELETE /api/planets/10 as ADMIN should delete the third planet")
        public void deletePlanetAsADMINShouldDeleteTenthPlanet()
        {
            var requestEntity = GetAuthEntity("test-admin", "admin");
            var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets/3", HttpMethod.DELETE, requestEntity, Integer.class);
            var responseCode = result.getStatusCode();
            var affectedRows = result.getBody();
            assertEquals(HttpStatus.OK, responseCode);
            assertEquals(1, affectedRows);
        }
        @Test
        @DisplayName("DELETE /api/planets/10 as owner USER should delete the third planet")
        public void deletePlanetAsOwnerUSERShouldDeleteTenthPlanet()
        {
            var requestEntity = GetAuthEntity("test-user", "user");
            var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets/10",
                    HttpMethod.DELETE, requestEntity, Integer.class);
            var responseCode = result.getStatusCode();
            var affectedRows = result.getBody();
            assertEquals(HttpStatus.OK, responseCode);
            assertEquals(1, affectedRows);
        }
        @Test
        @DisplayName("DELETE /api/planets/10 as anonymous should return 401")
        public void deletePlanetAsAnonymousShouldNotDeleteTenthPlanet()
        {
            var requestEntity = GetAnonEntity();
            var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets/3", HttpMethod.DELETE, requestEntity, Integer.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.UNAUTHORIZED, responseCode);
        }
        @Test
        @DisplayName("DELETE /api/planets/11 as ADMIN or owner USER should return 404")
        public void deletePlanetAsADMINOrUSERShouldReturn404() {
            var requestEntities = Arrays.asList(
                    GetAuthEntity("test-admin", "admin"),
                    GetAuthEntity("test-user", "user")
            );
            for(var requestEntity : requestEntities) {
                var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets/11",
                        HttpMethod.DELETE, requestEntity, HttpEntity.class);
                var responseCode = result.getStatusCode();
                assertEquals(HttpStatus.NOT_FOUND, responseCode);
            }
        }
        @Test
        @DisplayName("DELETE /api/planets/11 as anonymous should return 401")
        public void deletePlanetAsAnonymousShouldReturn401() {
            var requestEntity = GetAnonEntity();
            var result = PlanetEndpointTests.this.restTemplate.exchange(getBaseUrl() + "/api/planets/11",
                    HttpMethod.DELETE, requestEntity, HttpEntity.class);
            var responseCode = result.getStatusCode();
            assertEquals(HttpStatus.UNAUTHORIZED, responseCode);
        }
    }
}
