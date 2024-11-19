package tests;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("Создание пользователя с уже существующим email")
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

//        userData.put("password", "123");
//        userData.put("username", "learnqa");
//        userData.put("firstName", "learnqa");
//        userData.put("lastName", "learnqa");

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

//        Response responseCreateAuth = RestAssured
//                .given()
//                .body(userData)
//                .post("https://playground.learnqa.ru/api/user/")
//                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    @Test
    @Description("Создание пользователя с  некорректным email - без символа @")
    public void testCreateUserWithWrongEmail() {

        Map<String, String> userData = new HashMap<>();
        userData.put("email", DataGenerator.getRandomWrongEmail());
        userData = DataGenerator.getRegistrationData(userData);

//        userData.put("password", "123");
//        userData.put("username", "learnqa");
//        userData.put("firstName", "learnqa");
//        userData.put("lastName", "learnqa");

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

//        Response responseCreateAuth = RestAssured
//                .given()
//                .body(userData)
//                .post("https://playground.learnqa.ru/api/user/")
//                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }

    @ParameterizedTest
    @Description("Создание пользователя без указания одного из полей")
    @CsvSource({
            "'petrov1@example.com', '12345', 'username', 'firstName',",
            "'petrov1@example.com', '12345', 'username', , 'lastName'",
            "'petrov1@example.com', '12345', , 'firstName', 'lastName'",
            "'petrov1@example.com', , 'username', 'firstName', 'lastName'",
            ", '12345', 'username', 'firstName', 'lastName'"})
    public void testCreateUserWithoutOneField(String email,
                                              String password,
                                              String username,
                                              String firstName,
                                              String lastName) {

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("password", password);
        userData.put("username", username);
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

//        Response responseCreateAuth = RestAssured
//                .given()
//                .body(userData)
//                .post("https://playground.learnqa.ru/api/user/")
//                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: " + getAbsentParameterOfMap(userData));
    }

    @Test
    @Description("Создание пользователя с очень коротким именем в один символ")
    public void testCreateUserWithTooShortName() {
        String username = String.valueOf(DataGenerator.getRandomShortName());

        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData = DataGenerator.getRegistrationData(userData);

//        userData.put("password", "12345");
//        userData.put("username", username);
//        userData.put("firstName", "learnqa");
//        userData.put("lastName", "learnqa");

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

//        Response responseCreateAuth = RestAssured
//                .given()
//                .body(userData)
//                .post("https://playground.learnqa.ru/api/user/")
//                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too short");
    }

    @Test
    @Description("Создание пользователя с очень длинным именем - длиннее 250 символов")
    public void testCreateUserWithTooLongName() {
        String username = DataGenerator.getRandomLongName();

        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData = DataGenerator.getRegistrationData(userData);

//        userData.put("email", DataGenerator.getRandomEmail());
//        userData.put("password", "12345");
//        userData.put("username", username);
//        userData.put("firstName", "learnqa");
//        userData.put("lastName", "learnqa");

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

//        Response responseCreateAuth = RestAssured
//                .given()
//                .body(userData)
//                .post("https://playground.learnqa.ru/api/user/")
//                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too long");
    }

    @Test //успешное создание пользователя
    public void testCreateUserSuccessfully() {
//        String email = DataGenerator.getRandomEmail();

        Map<String, String> userData = DataGenerator.getRegistrationData();
//        userData.put("email", email);
//        userData.put("password", "123");
//        userData.put("username", "learnqa");
//        userData.put("firstName", "learnqa");
//        userData.put("lastName", "learnqa");

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

//        Response responseCreateAuth = RestAssured
//                .given()
//                .body(userData)
//                .post("https://playground.learnqa.ru/api/user/")
//                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

}
