package api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class AuthApi extends BaseApi {

    private static final String apiUrl = baseurl + "auth";

    public static Response postAuth(String payload){
               return given()
                     .contentType(ContentType.JSON)
                        .body(payload)
                        .when()
                        .post(apiUrl);
    }
}
