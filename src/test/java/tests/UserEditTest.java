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

public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    public void testEditJustCreatedTest() {
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

        //EDIT (запрос на изменение данных пользователя)
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequest("https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData);

//        Response responseEditUser = RestAssured
//                .given()
//                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
//                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
//                .body(editData)
//                .put("https://playground.learnqa.ru/api/user/" + userId)
//                .andReturn();

        //GET (получение данных пользователя и сравнение его данных с новыми измененными)
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

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test //Изменение данных пользователя, будучи неавторизованными
    public void testEditWithoutAuth() {
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

        //EDIT (запрос на изменение данных пользователя)
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequest("https://playground.learnqa.ru/api/user/" + userId, editData);

//        Response responseEditUser = RestAssured
//                .given()
//                .body(editData)
//                .put("https://playground.learnqa.ru/api/user/" + userId)
//                .andReturn();

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonByName(responseEditUser, "error",  "Auth token not supplied");
    }

    @Test //Изменение данных пользователя, будучи авторизованными другим пользователем
    public void testEditWithAuthDifferentUser() {
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

        //LOGIN (авторизуем другого пользователя)
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "petrov@example.com"); //берем данные другого пользователя
        authData.put("password", "12345");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

//        Response responseGetAuth = RestAssured
//                .given()
//                .body(authData)
//                .post("https://playground.learnqa.ru/api/user/login")
//                .andReturn();

        //EDIT (запрос на изменение данных первоначального пользователя)
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequest("https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData);

//        Response responseEditUser = RestAssured
//                .given()
//                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
//                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
//                .body(editData)
//                .put("https://playground.learnqa.ru/api/user/" + userId)
//                .andReturn();

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonByName(responseEditUser, "error",  "This user can only edit their own data.");
    }

    @Test //Изменяем email пользователя, будучи авторизованными тем же пользователем, на новый email без символа @
    public void testEditWithWrongEmail() {
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

        //EDIT (запрос на изменение данных пользователя на новый email без символа @)
        String newEmail = DataGenerator.getRandomWrongEmail();
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newEmail);

        Response responseEditUser = apiCoreRequests
                .makePutRequest("https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData);

//        Response responseEditUser = RestAssured
//                .given()
//                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
//                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
//                .body(editData)
//                .put("https://playground.learnqa.ru/api/user/" + userId)
//                .andReturn();

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonByName(responseEditUser, "error",  "Invalid email format");
    }

    @Test //Изменяем firstName пользователя, будучи авторизованными тем же пользователем, на очень короткое значение в один символ
    public void testEditWithWrongFirstName() {
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

        //EDIT (запрос на изменение имени пользователя на очень короткое значение в один символ)
        String newFirstName = String.valueOf(DataGenerator.getRandomShortName());
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newFirstName);

        Response responseEditUser = apiCoreRequests
                .makePutRequest("https://playground.learnqa.ru/api/user/" + userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData);

//        Response responseEditUser = RestAssured
//                .given()
//                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
//                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
//                .body(editData)
//                .put("https://playground.learnqa.ru/api/user/" + userId)
//                .andReturn();

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertJsonByName(responseEditUser, "error",  "The value for field `firstName` is too short");
    }
}