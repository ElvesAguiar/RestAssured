package com.devsuperior.dscommerce.controllers;

import com.devsuperior.dscommerce.tests.TokenUtil;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ProductControllerRA {


    private Map<String, Object> postProductInstance;

    private Map<String, Object> putProductInstance;


    private Long existingProductId, NonExistingProductId;
    private String userNameAdmin, passswordAdmin, userNameClient, passswordClient;
    private String tokenAdmin, tokenClient, tokenInvalid;


    @BeforeEach
    public void setUp() {

        postProductInstance = new HashMap<>();
        postProductInstance.put("name", "PS5");
        postProductInstance.put("description", "Lorem ipsum, dolor sit amet consectetur adipisicing elit. Qui ad, adipisci illum ipsam velit et odit eaque reprehenderit ex maxime delectus dolore labore, quisquam quae tempora natus esse aliquam veniam doloremque quam minima culpa alias maiores commodi. Perferendis enim");
        postProductInstance.put("imgUrl", "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpgd");
        postProductInstance.put("price", "1000.0");

        List<Map<String, Object>> categories = new ArrayList<>();

        Map<String, Object> category1 = new HashMap<>();
        category1.put("id", 2);
        Map<String, Object> category2 = new HashMap<>();
        category2.put("id", 3);

        categories.add(category1);
        categories.add(category2);

        postProductInstance.put("categories", categories);

        userNameAdmin = "alex@gmail.com";
        passswordAdmin = "123456";
        userNameClient = "maria@gmail.com";
        passswordClient = "123456";

        tokenAdmin = TokenUtil.obtenAcessToken(userNameAdmin, passswordAdmin);
        tokenClient = TokenUtil.obtenAcessToken(userNameClient, passswordClient);
        tokenInvalid = tokenAdmin + "aeawe";

        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() {
        existingProductId = 2L;
        given()
                .get("/products/{id}", existingProductId)
                .then()
                .statusCode(200)
                .body("id", is(existingProductId.intValue()))
                .body("name", equalTo("Smart TV"))
                .body("imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg"))
                .body("price", is(2190.0F))
                .body("categories.id", hasItems(2, 3))
                .body("categories.name", hasItems("Eletrônicos", "Computadores"));
    }

    @Test
    public void findAllShouldReturnPagedProductsWhenNameParamIsBlank() {
        given()
                .get("/products")
                .then()
                .statusCode(200)
                .body("content[0].id", is(1))
                .body("content[0].name", equalTo("The Lord of the Rings"))
                .body("content.name", hasItems("Macbook Pro", "PC Gamer Tera"))
                .body("content.price", hasItems(greaterThan(2000.0F)));
    }

    @Test
    public void findAllShouldReturnPagedProductsWhenNameParamIsNotBlank() {
        given()
                .get("/products?name=Macbook")
                .then()
                .statusCode(200)
                .body("content[0].id", is(3))
                .body("content.name", hasItems("Macbook Pro"));
    }

    @Test
    public void insertShouldReturnProductWhenValidDataAndUserAdmin() {
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenAdmin)
                .body(postProductInstance)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("PS5"))
                .body("imgUrl", notNullValue())
                .body("price", is(1000.0F))
                .body("description", notNullValue())
                .body("categories.id", hasItems(2, 3));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenInValidDataAndUserAdmin() {
        postProductInstance.put("name", "ps");

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenAdmin)
                .body(postProductInstance)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(422)
                .body("errors.message[0]", equalTo("Nome precisar ter de 3 a 80 caracteres"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenInValidDescriptionDataAndUserAdmin() {
        postProductInstance.put("description", "weaew");

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenAdmin)
                .body(postProductInstance)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(422)
                .body("errors.message[0]", equalTo("Descrição precisa ter no mínimo 10 caracteres"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenPriceIsNegativeAndUserAdmin() {
        postProductInstance.put("price", -50);

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenAdmin)
                .body(postProductInstance)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(422)
                .body("errors.message[0]", equalTo("O preço deve ser positivo"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenPriceIsZeroAndUserAdmin() {
        postProductInstance.put("price", 0);

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenAdmin)
                .body(postProductInstance)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(422)
                .body("errors.message[0]", equalTo("O preço deve ser positivo"));
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenProductHasNoCategoryAndUserAdmin() {
        postProductInstance.put("categories", null);

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenAdmin)
                .body(postProductInstance)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(422)
                .body("errors.message[0]", equalTo("Deve ter pelo menos uma categoria"));
    }

    @Test
    public void insertShouldReturnForbiddenWhenUserClient() {

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenClient)
                .body(postProductInstance)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(403);

    }

    @Test
    public void insertShouldReturnUnauthorizedWhenUserClient() {

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenInvalid)
                .body(postProductInstance)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(401);

    }


}
