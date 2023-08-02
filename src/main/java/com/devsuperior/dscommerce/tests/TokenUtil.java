package com.devsuperior.dscommerce.tests;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class TokenUtil {

    public static String obtenAcessToken(String username, String password){
        Response response = authRequest(username,password);
        return response.jsonPath().getString("access_token");

    }

    private static Response authRequest(String username, String password){
        return given()
                .auth()
                .preemptive()
                .basic("myclientid","myclientsecret")
            .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", "password")
                .formParam("username", username)
                .formParam("password", password)
            .when()
                    .post("/oauth2/token");
    }
}
