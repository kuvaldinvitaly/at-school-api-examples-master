package org.example.api;



import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.example.model.pet.Pet;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import static io.restassured.RestAssured.given;



public class TestPet {

    private Pet pet = new Pet();
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
        String name = "Pet_" + UUID.randomUUID().toString();
        pet.setId(id);
        pet.setName(name);
    }

    /**
     * Тестирование POST запроса
     */

    @Test(priority = 1)
    public void checkPostRequest(){
        given()
                .spec(requestSpecification)
                .body(pet)
                .when()
                .post("/pet")
                .then()
                .spec(responseSpecificationOK);

        Pet actual =
                given()
                        .spec(requestSpecification)
                        .pathParam("petId", id)
                        .when()
                        .get("/pet/{petId}")
                        .then()
                        .spec(responseSpecificationOK)
                        .extract().body()
                        .as(Pet.class);
        Assert.assertEquals(actual.getName(), pet.getName(), "TestPost not complited");
    }

    /**
     * Тестирование PUT запроса
     */

    @Test(priority = 2)
    public void checkPutRequest(){
        given()
                .spec(requestSpecification)
                .body(pet)
                .when()
                .put("/pet")
                .then()
                .spec(responseSpecificationOK);

        Pet actual =
                given()
                        .spec(requestSpecification)
                        .pathParam("petId", id)
                        .when()
                        .get("/pet/{petId}")
                        .then()
                        .spec(responseSpecificationOK)
                        .extract().body()
                        .as(Pet.class);
        Assert.assertEquals(actual.getName(), pet.getName(), "TestPost not complited");
    }

    /**
     * Тестирование DELETE запроса
     */

    @Test(priority = 3)
    public void checkDeleteRequest(){
        given()
                .spec(requestSpecification)
                .body(pet)
                .when()
                .post("/pet")
                .then()
                .spec(responseSpecificationOK);

        given()
                .spec(requestSpecification)
                .pathParam("petId", id)
                .when()
                .delete("/pet/{petId}")
                .then()
                .spec(responseSpecificationOK);
    }

    /**
     * Выполнение GET запроса у удаленного Pet
     */

    @Test(priority = 4)
    public void CheckPetAfterDelete(){
        given()
                .spec(requestSpecification)
                .pathParam("petId", id)
                .when()
                .get("/pet/{petId}")
                .then()
                .spec(responceSpecificationNotFound);
    }

}
