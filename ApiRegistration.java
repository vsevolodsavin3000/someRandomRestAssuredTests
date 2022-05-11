package api.v1.registration;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.PropertiesManager;
import utils.RunnerTest;
import utils.TestContext;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Map;

import static org.hamcrest.Matchers.lessThan;

public class ApiRegistration extends RunnerTest {

    TestContext testContext = new TestContext();
    public static String basePath = "/v1/registration.json";
    private static Long responseTime = Long.valueOf(PropertiesManager.getProp("SLA"));

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public void registration(Map<String, String> map) throws IOException {

        RequestSpecification requestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrlApi)
                .setBasePath(basePath)
                .setContentType("application/json;charset=UTF-8")
                .setAccept("application/json, text/plain, */*")
                .build()
                .filter(new AllureRestAssured());

        JSONObject password = new JSONObject()
                .put("first", testContext.dataRules(map, "Пароль"))
                .put("second", testContext.dataRules(map, "Повторный пароль"));

        JSONObject registration = new JSONObject()
                .put("city", testContext.dataRules(map, "Город"))
                .put("company_name", testContext.dataRules(map, "Название компании"))
                .put("email", testContext.dataRules(map, "Электронная почта"))
                .put("first_name", testContext.dataRules(map, "Имя"))
                .put("last_name", testContext.dataRules(map, "Фамилия"))
                .put("password", password)
                .put("phone", testContext.dataRules(map, "Номер телефона"))
                .put("timezone_id", testContext.dataRules(map, "Часовой пояс"));

        JSONObject recaptcha = new JSONObject()
                .put("response", "");

        JSONObject request = new JSONObject()
                .put("registration", registration);

        JSONObject meta = new JSONObject()
                .put("recaptcha", recaptcha);

        request.put("meta", meta);

        JSONObject payload = new JSONObject()
                .put("request", request);

        ValidatableResponse response = RestAssured.given()
                .spec(requestSpec)
                .body(payload.toString())
                .when()
                .post()
                .then()
                .assertThat().statusCode(200)
//                .body(matchesJsonSchemaInClasspath("schemes/registration.json"))
                .time(lessThan(responseTime));

        testContext.setContext(map.get("Сущность"), "company_id", response.extract().jsonPath().get("response.data.id").toString());

    }

}
