package org.example.api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.example.model.user.User;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public class TestUser {
    private User user = new User();
    private static int id;
    private static ResponseSpecification responseSpecificationOK;
    private static RequestSpecification requestSpecification;
    private static ResponseSpecification responceSpecificationNotFound;

    /**
     * Начальные параметры
     */

    @BeforeClass
    public void configuration() throws IOException {
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("application.properties"));
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io/v2/")
                .addHeader("api_key", System.getProperty("api.key"))
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
        responseSpecificationOK = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .log(LogDetail.ALL)
                .build();
        responceSpecificationNotFound = new ResponseSpecBuilder()
                .expectStatusCode(404)
                .log(LogDetail.ALL)
                .build();

        id = new Random().nextInt(500000);
        String name = "User_" + UUID.randomUUID().toString();
        user.setId(id);
        user.setUsername(name);
    }

    /**
     * Выполняем авторизацию пользователем
     */

    @Test(priority = 1)
    public void checkLogin() {
        given()
                .spec(requestSpecification)
                .when()
                .get("/user/login")
                .then()
                .spec(responseSpecificationOK);
    }

    /**
     * Тестирование POST запроса
     */

    @Test(priority = 2)
    public void checkPostRequest() {
        given()
                .spec(requestSpecification)
                .body(user)
                .when()
                .post("/user")
                .then()
                .spec(responseSpecificationOK);

        User actual = given()
                .spec(requestSpecification)
                .pathParam("username", user.getUsername())
                .when()
                .get("/user/{username}")
                .then()
                .spec(responseSpecificationOK)
                .extract().body().as(User.class);
        Assert.assertEquals(actual.getUsername(), user.getUsername());
    }

    /**
     * Тестирование PUT запроса
     */

    @Test(priority = 3)
    public void checkPutRequest() {
        user.setFirstName("qwerty");
        given()
                .spec(requestSpecification)
                .body(user)
                .pathParam("username", user.getUsername())
                .when()
                .put("/user/{username}")
                .then()
                .spec(responseSpecificationOK);

        User actual = given()
                .spec(requestSpecification)
                .pathParam("username", user.getUsername())
                .when()
                .get("/user/{username}")
                .then()
                .spec(responseSpecificationOK)
                .extract().body().as(User.class);
        Assert.assertEquals(actual.getFirstName(), user.getFirstName());
    }

    /**
     * Тестирование DELETE запроса
     */

    @Test(priority = 4)
    public void checkDeleteRequest() {
        given()
                .spec(requestSpecification)
                .body(user)
                .when()
                .post("/user")
                .then()
                .spec(responseSpecificationOK);

        given()
                .spec(requestSpecification)
                .pathParam("username", user.getUsername())
                .when()
                .delete("/user/{username}")
                .then()
                .spec(responseSpecificationOK);
    }

    /**
     * Выполнение GET запроса у удаленного User
     */

    @Test(priority = 5)
    public void CheckPetAfterDelete() {
        given()
                .spec(requestSpecification)
                .pathParam("username", user.getUsername())
                .when()
                .get("/user/{username}")
                .then()
                .spec(responceSpecificationNotFound);
    }

}
