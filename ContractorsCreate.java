package steps;

import java.util.Map;

import io.cucumber.java.ru.И;

import static asserts.responseFormatCheck.ResponseFormatCheck.assertError;
import static endpoints.api.contractors.Contractors.createNewContractor;
import static endpoints.api.contractors.Contractors.createNewContractor_valid;
import static utils.Attachments.makeContextAttachment;
import static utils.Context.getContext;
import static utils.Context.randomizeValues;
import static utils.RequestMaps.fillContractorMap;

public class ContractorsCreate {

    @И("создаем подрядчика со следующими данными:")
    public static void createContractor(Map<String, String> arg) {
        createNewContractor_valid(
                fillContractorMap(
                        randomizeValues(arg)));

        makeContextAttachment();
    }

    @И("^проверяем, что при попытке создать подрядчика с параметром (.+) равным пустой строке, возвращается ответ со статус-кодом (\\d+)$")
    public static void createContractorEmptyString(String parameter, int statusCode, Map<String, String> arg) {
        Map<String, String> body = fillContractorMap(randomizeValues(arg));
        body.put(parameter, "");

        assertError(
                createNewContractor(body),
                statusCode);
    }

    @И("^проверяем, что при попытке создать подрядчика с параметром (.+) равным null, возвращается ответ со статус-кодом (\\d+)$")
    public static void createContractorNull(String parameter, int statusCode, Map<String, String> arg) {
        Map<String, String> body = fillContractorMap(randomizeValues(arg));
        body.put(parameter, null);

        assertError(
                createNewContractor(body),
                statusCode);
    }

    @И("^проверяем, что при попытке создать подрядчика с параметром (.+) больше допустимой длины, возвращается ответ со статус-кодом (\\d+)$")
    public static void createContractorTooLong(String parameter, int statusCode, Map<String, String> arg) {
        Map<String, String> body = fillContractorMap(randomizeValues(arg));
        body.put(parameter, getContext("LoremIpsum"));

        assertError(
                createNewContractor(body),
                statusCode);
    }

    @И("^проверяем, что при попытке создать подрядчика с параметром (.+) равным уже существующему, возвращается ответ со статус-кодом (\\d+)$")
    public static void createContractorExisting(String parameter, int statusCode, Map<String, String> arg) {
        String value;
        if (parameter.equals("fullName")) {
            value = getContext("Полное название");
        } else {
            value = getContext("Краткое название");
        }

        Map<String, String> body = fillContractorMap(randomizeValues(arg));
        body.put(parameter, value);

        assertError(
                createNewContractor(body),
                statusCode);
    }

}
