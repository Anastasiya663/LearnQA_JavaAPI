package other;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.*;

public class TryPasswordTests {

    @Test
    void testToken() {
        String login = "super_admin";
        List<String> passwords = List.of("123456", "123456789", "qwerty", "password", "1234567",
                "12345678", "12345", "iloveyou", "111111", "123123", "abc123", "qwerty123", "1q2w3e4r",
                "admin", "qwertyuiop", "654321", "555555", "lovely", "7777777", "welcome", "888888", "princess",
                "dragon", "password1", "123qwe");

        for(String pass:passwords) {
            Map<String, String> data = new HashMap<>();
            data.put("login", login);
            data.put("password", pass);

            Response responseForGet = RestAssured
                    .given()
                    .body(data)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();
            String responseCookie = responseForGet.getCookie("auth_cookie");

            Map<String, String> cookies = new HashMap<>();
            if (responseCookie != null) {
                cookies.put("auth_cookie", responseCookie);
            }

            Response responseForCheck = RestAssured
                    .given()
                    .cookies(cookies)
                    .when()
                    .post("https://playground.learnqa.ru/api/check_auth_cookie")
                    .andReturn();

            String responseAuth = responseForCheck.asString();

            if (responseAuth.equals("You are authorized")) {
                System.out.println(responseAuth);
                System.out.println("Correctly password is:" + pass);
            break;
            }
        }
    }
}
