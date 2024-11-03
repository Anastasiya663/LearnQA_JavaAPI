package tests;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestHeader extends BaseTestCase {

    @Test
    public void testHeader() {
        Map<String, String> expectedHeader = new HashMap<>();
        expectedHeader.put("x-secret-homework-header", "Some secret value");

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();
        assertEquals(200, response.getStatusCode(), "Status code != 200, something's wrong");

        Headers headers = response.getHeaders();
        if (headers.hasHeaderWithName("x-secret-homework-header")) {
            Map<String, String> actualHeader = new HashMap<>();
            actualHeader.put("x-secret-homework-header", headers.getValue("x-secret-homework-header"));
            assertEquals(expectedHeader, actualHeader);
        } else {
            System.out.println("Header with name 'x-secret-homework-header' doesn't exist");
        }
    }
}
