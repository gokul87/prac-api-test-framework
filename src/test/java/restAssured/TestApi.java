package restAssured;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;
import io.restassured.module.jsv.*;
import org.testng.annotations.Test;

import java.util.Random;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.lessThan;
import static org.testng.Assert.assertEquals;
import restAssured.config.TestConfig;

/**
 * This is a test class which gets executed against fake rest api data created by json-server
 * I have created a maven build file using which you can run the whole test suite used just one command
 *
 * mvn exec:exec@Shellscript
 * @author gokul
 * @param <Posts>
 *
 */
public class TestApi extends TestConfig {

//    public java.lang.Object uniqueNo;
    public int bookingId, dId;
    public String name;
    public String token;
    ResponseSpecBuilder builder;
    static ResponseSpecification rspec;

    JsonNodeFactory factory = JsonNodeFactory.instance;
    ObjectNode pushContent = factory.objectNode();
    ObjectNode dates = factory.objectNode();

    public TestApi() {


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

        Response resp = given().get(baseurl+"booking");
        assertEquals(resp.getStatusCode(), 200);
        String responseBody = resp.body().asString();

        JsonPath jsonPath = new JsonPath(responseBody);
        bookingId = jsonPath.getInt("bookingid["+x+"]");

        System.out.println("The value of booking Id is "+bookingId);
    }

    @Test(priority=3)
    public void testSingleApiBooking() {

        Response resp = given().get(baseurl+"booking/"+bookingId);
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

        token = given().
                contentType("application/json").
                body(jsonString).
                when().
                post(baseurl + "auth").
                then().
                assertThat().
                statusCode(200).
                extract().
                path("token");

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

        Response resp = given().
                                header("Content-Type", "application/json").
                                header("Cookie","token="+this.token).
                                body(pushContent).log().all().
                        when().
                                put(baseurl+"booking/"+id);

        String responseBody = resp.body().asString();

        JsonPath jsonPath = new JsonPath(responseBody);
        String fname = jsonPath.getString("firstname");
        assertEquals(fname, "tharu");
        assertEquals(resp.getStatusCode(), 200);
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

        Response resp = given().
                header("Content-Type", "application/json").
                body(pushContent).log().all().
                when().
                post(baseurl+"booking");

        String responseBody = resp.body().asString();

        JsonPath jsonPath = new JsonPath(responseBody);
        String fname = jsonPath.getString("booking.firstname");
        dId = jsonPath.getInt("bookingid");
        assertEquals(fname, name);
        assertEquals(resp.getStatusCode(), 200);
    }

    @Test(priority=6)
    public void testDeleteApi() {

        given().
                header("Cookie", "token="+this.token).
        when().
                delete(baseurl+"booking/"+Integer.toString(dId)).
        then().
                assertThat().
                statusCode(201);
    }
}