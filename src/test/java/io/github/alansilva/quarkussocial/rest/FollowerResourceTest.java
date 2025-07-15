package io.github.alansilva.quarkussocial.rest;

import io.github.alansilva.quarkussocial.domain.model.Follower;
import io.github.alansilva.quarkussocial.domain.model.User;
import io.github.alansilva.quarkussocial.domain.repository.FollowerRepository;
import io.github.alansilva.quarkussocial.domain.repository.UserRepository;
import io.github.alansilva.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long followerId;


    @BeforeEach
    @Transactional
    void setUp() {
        //usuario padrão dos testes
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        //o seguidor
        var follower = new User();
        follower.setAge(45);
        follower.setName("Cicrano");
        userRepository.persist(follower);
        followerId = follower.getId();

        //cria um follower
        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);
    }

    @Test
    @DisplayName(" should return 409 when followerId is equal to User Id")
    //@Order(1)
    public void sameUserAsFollowerTest(){

        var body = new FollowerRequest();
        body.setFollowerId(userId);

            given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .pathParam("userId", userId)
                .when()
                    .put()
                .then()
                    .statusCode(Response.Status.CONFLICT.getStatusCode())
                    .body(Matchers.is("You can't follow yourself"));
    }

    @Test
    @DisplayName(" should return 404 on follow a user when User Id doen't exist ")
    //@Order(2)
    public void userNotFoundWhenTryingToFollowTest(){

        var body = new FollowerRequest();
        body.setFollowerId(userId);

        var inexistentUserId = 888;

            given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .pathParam("userId", inexistentUserId)
                .when()
                    .put()
                .then()
                    .statusCode(Response.Status.NOT_FOUND.getStatusCode());

    }


    @Test
    @DisplayName(" should follow a user ")
    //@Order(3)
    public void followUserTest(){

        var body = new FollowerRequest();
        body.setFollowerId(followerId);

        given()
                    .contentType(ContentType.JSON)
                    .body(body)
                    .pathParam("userId", userId)
                .when()
                    .put()
                .then()
                    .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName(" should return 404 on list user followers anda User Id doen't exist ")
    //@Order(4)
    public void userNotFoundWhenListingFollowersTest(){

        var inexistentUserId = 123;

            given()
                    .contentType(ContentType.JSON)
                    .pathParam("userId", inexistentUserId)
                .when()
                    .get()
                .then()
                    .statusCode(Response.Status.NOT_FOUND.getStatusCode());

    }

    @Test
    @DisplayName("should list a user's followers")
    //@Order(5)
    public void listingFollowersTest(){

        var response =

            given()
                    .contentType(ContentType.JSON)
                    .pathParam("userId", userId)
                .when()
                    .get()
                .then()
                    .extract().response();

        System.out.println("RESPONSE: " + response.asString());

        var followersConut = response.jsonPath().get("followersCount");
        var followersContent = response.jsonPath().getList("content");
        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
        assertEquals(1, followersConut);
        assertEquals(1, followersContent.size());

    }

    @Test
    @DisplayName(" should return 404 on unfollow user and User Id doen't exist ")
    public void userNotFoundWhenUnFollowingAUserTest(){

        var inexistentUserId = 123;


        given()
                    .pathParam("userId", inexistentUserId)
                    .queryParam("followerId", followerId)
                .when()
                    .delete()
                .then()
                    .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName(" should Unfollow an user")
    public void unfollowUserTest(){

        var inexistentUserId = 123;


            given()
                    .pathParam("userId", userId)
                    .queryParam("followerId", followerId)
                .when()
                    .delete()
                .then()
                    .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }
}