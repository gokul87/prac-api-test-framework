package mocks;

import io.restassured.response.Response;
import org.junit.Test;
import org.junit.Rule;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static io.restassured.RestAssured.given;
import com.github.tomakehurst.wiremock.WireMockServer;

public class TestStubEndpoint {

//    @Rule
//    public WireMockRule wireMockRule = new WireMockRule();

    private String respText = "{" +
            " \"city\" : \"London\", \n" +
            " \"country\": \"UK\" \n" +
            "}";

//    @Test
//    public void testSetUpStub() throws InterruptedException {

//        stubFor(get(urlEqualTo("/mock/api"))
//                    .willReturn(aResponse()
//                        .withHeader("Content-Type", "application/json")
//                        .withStatus(200)
//                        .withBody(respText)));

//        WireMockServer wireMockServer1 = new WireMockServer();
//        wireMockServer1.start();
//        setupStub();
//        System.out.println("Server started");
//        Thread.sleep(2000);
//        Response resp = given().get("http://localhost:8080/an/endpoint");
//        resp.then().log().all().statusCode(200);
//        wireMockServer1.stop();
//    }

    public void setupStub() {

        stubFor(get(urlEqualTo("/an/endpoint"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/plain")
                        .withStatus(200)
                        .withBody(respText)));
    }
}
