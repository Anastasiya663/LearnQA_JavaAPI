package other;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloWorldTest {

    @ParameterizedTest
    @ValueSource(strings = {"", "John", "Pete"})
    void testHelloMethodWithoutName (String name) {
        Map<String, String> queryParams = new HashMap<>();

        if(name.length() > 0) {
            queryParams.put("name", name);
        }

        JsonPath response = RestAssured
                .given()
                .queryParams(queryParams)
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();
        String answer = response.getString("answer");
        String expectedName = (name.length() > 0) ? name : "someone";

        assertEquals("Hello, " + expectedName, answer, "The answer is not expected");
    }




//        System.out.println("\nPretty text:");
//        response.prettyPrint();
//
//        System.out.println("\nHeaders:");
//        Headers responseHeaders = response.getHeaders();
//        System.out.println(responseHeaders);
//
//        System.out.println("\nCookies:");
//        Map<String, String> responseCookies = response.getCookies();
//        System.out.println(responseCookies);

//    --------------------------------------
//        Map<String, String> params = new HashMap<>();
//        params.put("name", "John");
//
//        JsonPath response = RestAssured
//                .given()
//                .queryParams(params)
//                .get("https://playground.learnqa.ru/api/hello")
//                .jsonPath();
//
//        String name = response.get("answer2");
//        if(name == null) {
//            System.out.println("The key 'answer2' is absent");
//        } else {
//            System.out.println(name);
//        }
//        ----------------------------
//        Map<String, Object> body = new HashMap<>();
//        body.put("param1", "value1");
//        body.put("param2", "value2");
//
//        Response response = RestAssured
//                .given()
//                .body(body) //2 способ передать параметры в post-запросе
////                .body("param1=value1&param2=value2") //1 способ передать параметры в post-запросе
////                .queryParam("param1", "value1") //параметры для get-запроса
////                .queryParam("param2", "value2")
//                .post("https://playground.learnqa.ru/api/check_type")
//                .andReturn();
//        response.print();

//      --------------------------------
//        Response response = RestAssured
//                .given()
//                .redirects()
//                .follow(true)
//                .when()
//                .get("https://playground.learnqa.ru/api/get_303")
//                .andReturn();
//
//        int statusCode = response.getStatusCode();
//        System.out.println(statusCode);

//       --------------------------------
//        Map<String, String> headers = new HashMap<>();
//        headers.put("myHeader1", "myValue1");
//        headers.put("myHeader2", "myValue2");
//
//        Response response = RestAssured
//                .given()
//                .redirects()
//                .follow(false)
////                .headers(headers)
//                .when()
////                .get("https://playground.learnqa.ru/api/show_all_headers")
//                .get("https://playground.learnqa.ru/api/get_303")
//                .andReturn();
//
//        response.prettyPrint();
//
//        String locationHeader = response.getHeader("Location"); //получили ссылку, куда нас перенаправляют
////        Headers responseHeaders = response.getHeaders();
//        System.out.println(locationHeader);

//     ----------------------------------
//        Map<String, String> data = new HashMap<>();
//        data.put("login", "secret_login2");
//        data.put("password", "secret_pass");
//
//        Response responseForGet = RestAssured //отправляем запрос, получаем cookie, которые нужно передать серверу
//                .given()
//                .body(data)
//                .when()
//                .post("https://playground.learnqa.ru/api/get_auth_cookie")
//                .andReturn();
//
//        String responseCookie = responseForGet.getCookie("auth_cookie"); //сохраняем в переменную
//
//        Map<String, String> cookies = new HashMap<>();
//        if(responseCookie != null) {
//            cookies.put("auth_cookie", responseCookie);
//        }
//
//        Response responseForCheck = RestAssured //передаем необходимые для авторизации cookie
//                .given()
//                .body(data)
//                .cookies(cookies)
//                .when()
//                .post("https://playground.learnqa.ru/api/check_auth_cookie")
//                .andReturn();
//
//        responseForCheck.print(); // успешно авторизовались
//      --------------------------------------
//        JsonPath response = RestAssured
//                .given()
//                .when()
//                .get("https://playground.learnqa.ru/api/get_json_homework")
//                .jsonPath();
//        response.prettyPrint();
//
//        ArrayList<LinkedHashMap<String, String>> messages = response.get("messages");
//        LinkedHashMap<String, String> message = messages.get(1);
//
//        System.out.println(message.get("message"));
//    ----------------------------------
//      @Test
//      void testRestAssured () {
//          Response response = RestAssured
//            .get("https://playground.learnqa.ru/api/map")
//            .andReturn();
//    //assertTrue(response.statusCode() == 200, "Unexpected status code"); // если condition вернет false, то увидим message
//    assertEquals(200, response.statusCode(), "Unexpected status code");
//     }
//       @Test
//       void testFailed() {
//          Response response = RestAssured
//                .get("https://playground.learnqa.ru/api/map2")
//                .andReturn();
//        //assertTrue(response.statusCode() == 200, "Unexpected status code"); // если condition вернет false, то увидим message
//        assertEquals(200, response.statusCode(), "Unexpected status code");
//    }
}
