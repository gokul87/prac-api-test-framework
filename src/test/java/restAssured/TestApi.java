package restAssured;

import api.AuthApi;
import api.BookingApi;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.Random;
import static io.restassured.RestAssured.*;
import static org.testng.Assert.assertEquals;

/**
 * This is a test class which gets executed against fake rest api data created by json-server
 * I have created a maven build file using which you can run the whole test suite used just one command
 *
 * mvn exec:exec@Shellscript
 * @author gokul
 * @param <Posts>
 *
 */
public class TestApi{

    public int bookingId, dId;
    public String name;
    public String token;
    public String baseurl = "http://localhost:3001/";
    public static ResponseSpecBuilder builder;
    public static ResponseSpecification responseSpec;

    JsonNodeFactory factory = JsonNodeFactory.instance;
    ObjectNode pushContent = factory.objectNode();
    ObjectNode dates = factory.objectNode();

    public TestApi() {

    }

    @BeforeClass
    public void setUp(){
        builder = new ResponseSpecBuilder();
        builder.expectStatusCode(200);
        responseSpec = builder.build();
    }

    @Test(priority=1)
    public void testApiStatus() {

        Response resp = given().get(baseurl+"ping");
        assertEquals(resp.getHeader("Content-Type"), "text/plain; charset=utf-8");
        assertEquals(resp.getStatusCode(), 201);
    }

    @Test(priority=2)
    public void testApiBooking() {

        //Generate random integer to pass in jsonPath
        Random rand = new Random();
        int x = rand.nextInt(5);

        Response resp = BookingApi.getBooking();
        resp.then().spec(responseSpec);
        String responseBody = resp.body().asString();

        JsonPath jsonPath = new JsonPath(responseBody);
        bookingId = jsonPath.getInt("bookingid["+x+"]");

        System.out.println("The value of booking Id is "+bookingId);
    }

    @Test(priority=3)
    public void testSingleApiBooking() {

        Response resp = BookingApi.getSingleBooking(bookingId);
        assertEquals(resp.getStatusCode(), 200);
        assertEquals(resp.getContentType(), "application/json; charset=utf-8");

        resp.then().log().all();
        resp.then().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("jsonSchema.json"));
    }

    @Test(priority=4)
    public void testPostAuth() {

            String jsonString = "{" +
                    " \"username\": \"admin\", \n" +
                    " \"password\": \"password123\" \n" +
                    "}";

        Response resp = AuthApi.postAuth(jsonString);
        token = resp.then().extract().path("token");

        System.out.println("The unique token for the posted data is "+ this.token);
    }

    @Test(priority=5)
    public void testPutApi() {

        String id = Integer.toString(bookingId);

        dates.put("checkin", "2018-01-04");
        dates.put("checkout", "2018-01-05");

        pushContent.put("firstname", "tharu");
        pushContent.put("lastname", "gokul");
        pushContent.put("totalprice", 200);
        pushContent.put("depositpaid", false);
        pushContent.put("additionalneeds", "Lunch");
        pushContent.put("bookingdates", dates);

        System.out.println("The json array looks like" + pushContent);
        System.out.println("The token issssssss" + this.token);
        System.out.println("The value of booking Id issssss "+id);

        Response resp = BookingApi.updateBooking("application/json", "token="+this.token, pushContent, id);
        resp.then().spec(responseSpec);
        String responseBody = resp.body().asString();

        JsonPath jsonPath = new JsonPath(responseBody);
        String fname = jsonPath.getString("firstname");
        assertEquals(fname, "tharu");
    }

    @Test
    public void testPostApi() {

        Fairy fairy = Fairy.create();
        Person person = fairy.person();

        name = person.getFirstName();
        dates.put("checkin", "2018-01-04");
        dates.put("checkout", "2018-01-05");

        pushContent.put("firstname", name);
        pushContent.put("lastname", person.getLastName());
        pushContent.put("totalprice", 200);
        pushContent.put("depositpaid", false);
        pushContent.put("additionalneeds", "Lunch");
        pushContent.put("bookingdates", dates);

        System.out.println("The json array looks like" + pushContent);

        Response resp = BookingApi.postBooking("application/json", pushContent);
        resp.then().spec(responseSpec);
        String responseBody = resp.body().asString();

        JsonPath jsonPath = new JsonPath(responseBody);
        String fname = jsonPath.getString("booking.firstname");
        dId = jsonPath.getInt("bookingid");
        assertEquals(fname, name);
    }

    @Test(priority=6)
    public void testDeleteApi() {

        Response resp = BookingApi.deleteBooking("token="+this.token, Integer.toString(dId));
        resp.then().statusCode(201);
    }
}
