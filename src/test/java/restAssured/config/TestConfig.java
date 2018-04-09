package restAssured.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;

public class TestConfig {

    public String baseurl;

    @BeforeClass
    public void setup() {

        baseurl = "http://localhost:3001/";
    }
}
