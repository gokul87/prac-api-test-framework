package restAssured;

import api.AuthApi;
import api.BookingApi;
import ch.qos.logback.core.FileAppender;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;
import mocks.TestStubEndpoint;
import org.junit.*;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Random;
import static io.restassured.RestAssured.*;

import org.junit.runners.MethodSorters;
import org.slf4j.LoggerFactory;
import payloads.*;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

/**
 * This is a test class which gets executed against fake rest api data created by json-server
 * I have created a maven build file using which you can run the whole test suite used just one command
 *
 * mvn exec:exec@Shellscript
 * @author gokul
 * @param <Posts>
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestApi{

    public static int bookingId, dId;
    public String name;
    public static String token;
    public String baseurl = "http://192.168.99.100:3001/";
    public static ResponseSpecBuilder builder;
    public static ResponseSpecification responseSpec;
    private static final Logger logger = LoggerFactory.getLogger(TestApi.class.getName());
    Handler fileHandler = null;

    JsonNodeFactory factory = JsonNodeFactory.instance;
    ObjectNode pushContent = factory.objectNode();
    ObjectNode dates = factory.objectNode();
    WireMockServer wireMockServer1 = new WireMockServer();

    public TestApi() {

    }

    @Before
    public void setUp() throws IOException {
        FileAppender appender = new FileAppen
        fileHandler.setLevel(Level.INFO);

        builder = new ResponseSpecBuilder();
        builder.expectStatusCode(200);
        responseSpec = builder.build();

        logger.info("START THE TEST");
        wireMockServer1.start();

        logger.info("-------------------------------");
        TestStubEndpoint setStub = new TestStubEndpoint();
        setStub.setupStub();
    }

    @After
    public void tearDown(){
        logger.info("END THE TEST");
        wireMockServer1.stop();
    }

    @Test
    public void testAApiStatus() {

        logger.info("PERFORMING THE TEST");
        Response resp = given().get(baseurl+"ping");
        Assert.assertEquals(resp.getHeader("Content-Type"), "text/plain; charset=utf-8");
        Assert.assertEquals(resp.getStatusCode(), 201);
    }

    @Test
    public void testBApiBooking() {

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

    @Test
    public void testCSingleApiBooking() {

        Response resp = BookingApi.getSingleBooking(bookingId);
        Assert.assertEquals(resp.getStatusCode(), 200);
        Assert.assertEquals(resp.getContentType(), "application/json; charset=utf-8");

        resp.then().log().all();
        resp.then().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("jsonSchema.json"));
    }

    @Test
    public void testDPostAuth() {

        Auth auth = new Auth.Builder()
                    .setUsername("admin")
                    .setPassword("password123")
                    .build();

        Response resp = AuthApi.postAuth(auth);
        token = resp.then().extract().path("token");

        System.out.println("The unique token for the posted data is "+ this.token);
    }

    @Test
    public void testEPutApi() {

        String id = Integer.toString(bookingId);

        dates.put("checkin", "2018-01-04");
        dates.put("checkout", "2018-01-05");

        BookingDates dates = new BookingDates.Builder()
                             .setCheckin("2018-01-04")
                             .setCheckout("2018-01-05")
                             .build();

        Booking payload = new Booking.Builder()
                          .setFirstname("tharu")
                          .setLastname("gokul")
                          .setTotalprice(200)
                          .setAdditionalneeds("Lunch")
                          .setBookingdates(dates)
                          .build();

//        pushContent.put("firstname", "tharu");
//        pushContent.put("lastname", "gokul");
//        pushContent.put("totalprice", 200);
//        pushContent.put("depositpaid", false);
//        pushContent.put("additionalneeds", "Lunch");
//        pushContent.put("bookingdates" , dates);

        System.out.println("The json array looks like" + pushContent);
        System.out.println("The token issssssss" + this.token);
        System.out.println("The value of booking Id issssss "+id);

        Response resp = BookingApi.updateBooking("application/json", "token="+this.token, payload, id);
        resp.then().spec(responseSpec);
        String responseBody = resp.body().asString();

        JsonPath jsonPath = new JsonPath(responseBody);
        String fname = jsonPath.getString("firstname");
        Assert.assertEquals(fname, "tharu");
    }

    @Test
    public void testFPostApi() {

        Fairy fairy = Fairy.create();
        Person person = fairy.person();

        name = person.getFirstName();
        BookingDates dates = new BookingDates.Builder()
                .setCheckin("2018-01-04")
                .setCheckout("2018-01-05")
                .build();

        Booking payload = new Booking.Builder()
                .setFirstname(name)
                .setLastname(person.getLastName())
                .setTotalprice(200)
                .setDepositpaid(false)
                .setAdditionalneeds("Lunch")
                .setBookingdates(dates)
                .build();

        System.out.println("The json array looks like" + payload);

        BookingResponse resp = BookingApi.postBooking("application/json", payload).as(BookingResponse.class);
        String fname = resp.getBooking().getFirstname();
        dId = resp.getBookingid();
        System.out.println("The name fetched from response is "+fname);
    }

    @Test
    public void testGDeleteApi() {

        Response resp = BookingApi.deleteBooking("token="+this.token, Integer.toString(dId));
        resp.then().statusCode(201);
    }

   @Test
    public void testHverifyMocks() {

        Response resp = given().get("http://localhost:8080/an/endpoint");
        resp.then().log().all().statusCode(200);

    }
}
