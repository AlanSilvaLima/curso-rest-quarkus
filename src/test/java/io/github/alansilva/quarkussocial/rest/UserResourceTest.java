package io.github.alansilva.quarkussocial.rest;

import io.github.alansilva.quarkussocial.rest.dto.CreateUserRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
@QuarkusTest
class UserResourceTest {

    @Test
    @DisplayName("should create and user successfuly")
    public void createUserTest(){

        var user = new CreateUserRequest();
        user.setName("fulano");
        user.setAge(30);

        var response =
                given()
                        .contentType(ContentType.JSON)
                        .body(user, ObjectMapperType.JACKSON_2) // 👈 forçar Jackson
                        .when()
                        .post("/users")
                        .then()
                        .extract().response();

        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }

}