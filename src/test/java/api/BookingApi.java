package api;

import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

public class BookingApi extends BaseApi {

    private static final String apiUrl = baseurl + "booking/";

    public static Response getBooking() {

        return given().get(apiUrl);
    }

    public static Response getSingleBooking(int bookingId) {

        return given().log().all().get(apiUrl+bookingId);
    }

    public static Response updateBooking(String contentType, String cookieToken, Object content, String bookingId) {
        return  given().
                header("Content-Type", contentType).
                header("Cookie", cookieToken).
                body(content).log().all().
                when().
                put(apiUrl+bookingId);
    }

    public static Response postBooking(String contentType, Object content) {
        return given().
                header("Content-Type", contentType).
                body(content).log().all().
                when().
                post(apiUrl);
    }

    public static Response deleteBooking(String cookieToken, String dId) {
        return given().
                header("Cookie", cookieToken).
                when().
                delete(apiUrl+dId);
    }
}
