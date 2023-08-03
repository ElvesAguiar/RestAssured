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

public class OrderControllerRA {


    private Map<String, Object> postOrderInstance;




    private Long existingOrderId, nonExistingOrderId;
    private String userNameAdmin, passswordAdmin, userNameClient, passswordClient;
    private String tokenAdmin, tokenClient, tokenInvalid;


    @BeforeEach
    public void setUp() {

        postOrderInstance = new HashMap<>();

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
    public void findByIdShouldReturnOrderWhenIdExistsAndAdmin() {
        existingOrderId = 2L;
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenAdmin)
                .get("/orders/{id}", existingOrderId)
                .then()
                .statusCode(200)
                .body("id", is(existingOrderId.intValue()))
                .body("moment", equalTo("2022-07-29T15:50:00Z"))
                .body("status", equalTo("DELIVERED"))
                .body("client", notNullValue())
                .body("payment", notNullValue())
                .body("items.name", hasItems("Macbook Pro"));
    }

    @Test
    public void findByIdShouldReturnOrderWhenIdExistsAndClient() {
        existingOrderId = 1L;
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenClient)
                .get("/orders/{id}", existingOrderId)
                .then()
                .statusCode(200)
                .body("id", is(existingOrderId.intValue()))
                .body("moment", notNullValue())
                .body("status", equalTo("PAID"))
                .body("client", notNullValue())
                .body("payment", notNullValue())
                .body("items.name", hasItems("Macbook Pro","The Lord of the Rings"));
    }


    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExistsAndAdmin() {
        nonExistingOrderId = 100L;
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenAdmin)
                .get("/orders/{id}", nonExistingOrderId)
                .then()
                .statusCode(404);

    }

    @Test
    public void findByIdShouldReturnUnAuthorizedWhenIdExistsAndTokenInvalid() {
        existingOrderId = 100L;
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenInvalid)
                .get("/orders/{id}", existingOrderId)
                .then()
                .statusCode(401);

    }

    @Test
    public void findByIdShouldReturnForbiddenWhenOrderBelongsToOtherAndClient() {
        existingOrderId = 2L;
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenClient)
                .get("/orders/{id}", existingOrderId)
                .then()
                .statusCode(403);

    }



}
