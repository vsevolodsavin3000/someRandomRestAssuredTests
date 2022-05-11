package endpoints.api.contractors;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import io.restassured.http.Headers;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import utils.Endpoints;
import utils.pojo.Item;
import utils.pojo.Root;

import static asserts.responseFormatCheck.Contractors.assertContractorDetails_post;
import static asserts.responseFormatCheck.Contractors.assertGetContractors;
import static io.restassured.RestAssured.given;
import static utils.Context.setContext;
import static utils.Context.setEntityForTearDown;
import static utils.Headers.getHeaders;
import static utils.Headers.getHeadersJsonLd;
import static utils.PropertiesManager.getProperty;

public class Contractors {

    private static final String contractors = Endpoints.contractors;

    public static ValidatableResponse getContractors(Map<String, String> arg) {
        RequestSpecification request = getHeaders();

        for (Map.Entry<String, String> entry : arg.entrySet()) {
            request.queryParam(entry.getKey(), entry.getValue());
        }
        return request
                .get(getProperty("baseUri") + contractors)
                .then();
    }

    public static ValidatableResponse getContractors_valid(Map<String, String> arg) {
        return assertGetContractors(getContractors(arg));
    }

    public static ValidatableResponse createNewContractor(Map<String, String> arg) {
        ValidatableResponse response = given()
                .spec(getHeaders())
                .contentType("application/json")
                .body(arg)
                .post(getProperty("baseUri") + contractors)
                .then();
        setIds(response);

        return response;
    }

    private static void setIds(ValidatableResponse response) {
        Headers headers = response.extract().headers();
        if (headers.hasHeaderWithName("location")) {
            String id = StringUtils.substringAfterLast(headers.getValue("location"), "/");

            setContext("ContractorId", id);
            setEntityForTearDown("Contractors", id);
        }
    }

    public static ValidatableResponse createNewContractor_valid(Map<String, String> arg) {
        return assertContractorDetails_post(createNewContractor(arg));
    }

    public static List<Item> getContractorsList() {
        return getHeadersJsonLd()
                .queryParam("itemsPerPage", 500)
                .get(getProperty("baseUri") + contractors)
                .then()
                .extract().body().as(Root.class)
                .getHydraMember();
    }

}
