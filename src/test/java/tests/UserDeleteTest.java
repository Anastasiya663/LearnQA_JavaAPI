package tests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test //попытка удалить пользователя по ID 2
    public void testDeleteUserWithId2() {

        //LOGIN (авторизуем пользователя)
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

//        Response responseGetAuth = RestAssured
//                .given()
//                .body(authData)
//                .post("https://playground.learnqa.ru/api/user/login")
//                .andReturn();

        //DELETE (запрос на удаление пользователя)
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/2",
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        authData);

//        Response responseDeleteUser = RestAssured
//                .given()
//                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
//                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
//                .body(authData)
//                .delete("https://playground.learnqa.ru/api/user/2")
//                .andReturn();

        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertJsonByName(responseDeleteUser, "error", "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test //успешное удаление пользователя
    public void testDeleteSuccess() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        //CREATE USER
        JsonPath responseCreateAuth = apiCoreRequests
                .makePostJsonRequest("https://playground.learnqa.ru/api/user/", userData);

//        JsonPath responseCreateAuth = RestAssured
//                .given()
//                .body(userData)
//                .post("https://playground.learnqa.ru/api/user/")
//                .jsonPath();

        String userId = responseCreateAuth.getString("id"); // сохраняем id нового пользователя

        //LOGIN (авторизуем нового пользователя)
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email")); //берем данные сгенерированного и созданного выше пользователя
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

//        Response responseGetAuth = RestAssured
//                .given()
//                .body(authData)
//                .post("https://playground.learnqa.ru/api/user/login")
//                .andReturn();

        //DELETE(запрос на удаление данных пользователя)
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        authData);

//        Response responseDeleteUser = RestAssured
//                .given()
//                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
//                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
//                .body(authData)
//                .delete("https://playground.learnqa.ru/api/user/" + userId)
//                .andReturn();

        Assertions.assertJsonByName(responseDeleteUser, "success", "!");

        //GET (получаем данные пользователя и убеждаемся, что пользователь удален)
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

//        Response responseUserData = RestAssured
//                .given()
//                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
//                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
//                .get("https://playground.learnqa.ru/api/user/" + userId)
//                .andReturn();

        Assertions.assertResponseTextEquals(responseUserData, "User not found");
    }

    @Test //удалить пользователя, будучи авторизованными другим пользователем
    public void testDeleteFailed() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        //CREATE USER
        JsonPath responseCreateAuth = apiCoreRequests
                .makePostJsonRequest("https://playground.learnqa.ru/api/user/", userData);

//        JsonPath responseCreateAuth = RestAssured
//                .given()
//                .body(userData)
//                .post("https://playground.learnqa.ru/api/user/")
//                .jsonPath();

        String userId = responseCreateAuth.getString("id"); // сохраняем id нового пользователя

        //LOGIN (авторизуем нового пользователя)
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "petrov@example.com"); //авторизуем другого пользователя
        authData.put("password", "12345");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

//        Response responseGetAuth = RestAssured
//                .given()
//                .body(authData)
//                .post("https://playground.learnqa.ru/api/user/login")
//                .andReturn();

        //DELETE(запрос на удаление данных первоначального пользователя)
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        userData);

//        Response responseDeleteUser = RestAssured
//                .given()
//                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
//                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
//                .body(userData)
//                .delete("https://playground.learnqa.ru/api/user/" + userId)
//                .andReturn();

        Assertions.assertJsonByName(responseDeleteUser, "error", "This user can only delete their own account.");
    }
}