package io.github.alansilva.quarkussocial.rest;

import io.github.alansilva.quarkussocial.domain.model.Follower;
import io.github.alansilva.quarkussocial.domain.model.Post;
import io.github.alansilva.quarkussocial.domain.model.User;
import io.github.alansilva.quarkussocial.domain.repository.FollowerRepository;
import io.github.alansilva.quarkussocial.domain.repository.PostRepository;
import io.github.alansilva.quarkussocial.domain.repository.UserRepository;
import io.github.alansilva.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;
    @Inject
    PostRepository postRepository;
    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;

    @BeforeEach
    @Transactional
    public void setUp(){
        //usuario padrão dos testes
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        //criada a postagem para o usuario
        Post post = new Post();
        post.setText("Hello");
        post.setUser(user);
        postRepository.persist(post);

        //usuario que nao segue ninguem
        var userNotFollower = new User();
        userNotFollower.setAge(32);
        userNotFollower.setName("Cicrano");
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        //usuario seguidor
        var userFollower = new User();
        userFollower.setAge(22);
        userFollower.setName("Javri");
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);
    }

    @Test
    @DisplayName("should create a post for a user")
    public void createPostTest(){

        var postRequest = new CreatePostRequest();
        postRequest.setText("some text");

            given()
                    .contentType(ContentType.JSON)
                    .body(postRequest)
                    .pathParams("userId", userId)
                .when()
                    .post()
                    .then()
                    .statusCode(201);

    }

    @Test
    @DisplayName("should return 404 when trying to make a post for an inexistent user")
    public void postForAnInexistentUserTest(){

        var postRequest = new CreatePostRequest();
        postRequest.setText("some text");

        var inexistentUserId = 999;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParams("userId", inexistentUserId)
                .when()
                .post()
                .then()
                .statusCode(404);

    }

    @Test
    @DisplayName("should return 404 when user doesn't exist")
    public void listPostUserNorFoundTest(){

        var inexistentUserId = 999;

            given()
                    .pathParams("userId", inexistentUserId)
                .when()
                    .get()
                .then()
                    .statusCode(404);

    }

    @Test
    @DisplayName("should return 400 when followerId header id not present")
    public void listPostFollowerHeaderNotSendTest(){

            given()
                    .pathParams("userId", userId)
                .when()
                    .get()
                .then()
                    .statusCode(400)
                    .body(Matchers.is("You forgot the header followerId"));

    }

    @Test
    @DisplayName("should return 400 when follower doesn't exist")
    public void listPostFollowerNotFoundTest(){

        var inexistentFollowerId = 999;

            given()
                    .pathParams("userId", userId)
                    .header("followerId", inexistentFollowerId)
                .when()
                    .get()
                .then()
                    .statusCode(400)
                    .body(Matchers.is("Inexistent FollowerId"));

    }

    @Test
    @DisplayName("should return 403 when follower isn't a follower")
    public void listPostNotAFollower(){

        given()
                    .pathParams("userId", userId)
                    .header("followerId", userNotFollowerId)
                .when()
                    .get()
                .then()
                    .statusCode(403)
                    .body(Matchers.is("You can't see these posts"));

    }

    @Test
    @DisplayName("should return posts")
    public void listPostTest(){

            given()
                    .pathParams("userId", userId)
                    .header("followerId", userFollowerId)
                .when()
                    .get()
                .then()
                    .statusCode(200)
                    .body("size()", Matchers.is(0));

    }

}