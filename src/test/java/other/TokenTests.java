package other;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.apache.commons.lang3.Validate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TokenTests {
    @Test
    void testToken() throws InterruptedException {
        JsonPath response = RestAssured // создание задачи
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String token = response.get("token"); //сохранение токена
        int deadline = response.get("seconds"); //сохранение времени готовности задачи

        response = RestAssured
                .given()
                .queryParam("token", token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        Assertions.assertEquals("Job is NOT ready", response.get("status")); //убеждаемся в правильности поля status

        Thread.sleep(deadline * 1000L); //ждем готовности задачи

        response = RestAssured // проверяем готовность задачи, убеждаемся в правильности поля status и наличии поля result
                .given()
                .queryParam("token", token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        Assertions.assertEquals("Job is ready", response.get("status"));

        String result = response.get("result");
        Validate.notNull(result);

        System.out.println("Job is ready and result is not null");
    }
}
