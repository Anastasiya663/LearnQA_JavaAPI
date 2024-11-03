package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCookie {

    @Test
    public void testCookie() {
        Map<String, String> expectedCookie = new HashMap<>();
        expectedCookie.put("HomeWork", "hw_value");

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        assertEquals(200,response.statusCode(),"Status code != 200, something's wrong");

        Map<String, String> cookies = response.getCookies();
        assertEquals(expectedCookie, cookies);
    }
}
