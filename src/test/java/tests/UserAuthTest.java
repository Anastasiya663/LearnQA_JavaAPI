package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

public class UserAuthTest extends BaseTestCase {

    String cookie;
    String header;
    int userIdOnAuth;

    @BeforeEach
    public void loginUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

//        this.cookie = responseGetAuth.cookie("auth_sid");
//        this.header = responseGetAuth.header("x-csrf-token");
//        this.userIdOnAuth = responseGetAuth.jsonPath().getInt("user_id");
          this.cookie = this.getCookie(responseGetAuth,"auth_sid");
          this.header = this.getHeader(responseGetAuth,"x-csrf-token");
          this.userIdOnAuth = this.getIntFromJson(responseGetAuth,"user_id");
    }

    @Test
    public void testAuthUser() {
//        Map<String, String> cookies = responseGetAuth.getCookies();
//        Headers headers = responseGetAuth.getHeaders();
//        int userIdOnAuth = responseGetAuth.jsonPath().getInt("user_id");
//
//        assertEquals(200, responseGetAuth.statusCode(), "Unexpected status code");
//        assertTrue(cookies.containsKey("auth_sid"), "Response doesn't have 'auth_sid' cookie");
//        assertTrue(headers.hasHeaderWithName("x-csrf-token"), "Response doesn't have 'x-csrf-token' header");
//        assertTrue(responseGetAuth.jsonPath().getInt("user_id") > 0, "User id should be greater than 0");

        Response responseCheckAuth = RestAssured
                .given()
                .header("x-csrf-token", this.header)
                .cookie("auth_sid", this.cookie)
                .get("https://playground.learnqa.ru/api/user/auth")
                .andReturn();

//        int userIdOnCheck = responseCheckAuth.getInt("user_id");
//        assertTrue(userIdOnCheck > 0, "Unexpected user id " + userIdOnCheck);
//
//        assertEquals(
//                userIdOnAuth,
//                userIdOnCheck,
//                "User_id from auth request doesn't equal to user_id from check request"
//        );

        Assertions.assertJsonByName(responseCheckAuth, "user_id", this.userIdOnAuth);
    }

    @ParameterizedTest
    @ValueSource (strings = {"cookie", "headers"})
    public void testNegativeAuthUser(String condition) {
//        Map<String, String> authData = new HashMap<>();
//        authData.put("email", "vinkotov@example.com");
//        authData.put("password", "1234");
//
//        Response responseGetAuth = RestAssured
//                .given()
//                .body(authData)
//                .post("https://playground.learnqa.ru/api/user/login")
//                .andReturn();
//        Map<String, String> cookies = responseGetAuth.getCookies();
//        Headers headers = responseGetAuth.getHeaders();

        RequestSpecification spec = RestAssured.given();
        spec.baseUri("https://playground.learnqa.ru/api/user/auth");

        if(condition.equals("cookie")) {
            spec.cookie("auth_sid", this.cookie);
        } else if (condition.equals("headers")) {
            spec.header("x-csrf-token", this.header);
        } else {
            throw new IllegalArgumentException("Condition value is known: " + condition);
        }

        Response responseForCheck = spec.get().andReturn();
        Assertions.assertJsonByName(responseForCheck,"user_id", 0);
//        assertEquals(0, responseForCheck.getInt("user_id"), "User_id should be 0 for unauth request");
    }
}
