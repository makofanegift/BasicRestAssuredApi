package basics;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;

public class ApiTest {

    String baseUrl = "https://www.ndosiautomation.co.za/APIDEV";
    String authToken;
    String testimonial_id;

    @Test
    public void loginTest(){

        String path = "/login";
        String payload = "{\n" +
                "  \"email\": \"GiftTest+210@gmail.com\",\n" +
                "  \"password\": \"Ndosi_TesSite2\"\n" +
                "}";

        Response response = RestAssured.given()
                .baseUri(baseUrl)
                .basePath(path)
                .contentType(ContentType.JSON)
                .body(payload)
                .log().all()
                .post().prettyPeek();

        authToken = response.jsonPath().getString("data.token");
        int responseCode = response.getStatusCode();
        assert responseCode == 200 : "Expected status code 200 but got " + responseCode;

    }
    @Test (dependsOnMethods = "loginTest")
    public void getProfile(){

        String path = "/profile";
        RestAssured.given()
                .baseUri(baseUrl)
                .basePath(path)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get()
                .then()
                .statusCode(200)
                .log().all();

    }
    @Test (dependsOnMethods = "loginTest")
    public void createTestimonial(){
        String path = "/testimonials";
        String payload = "{\n" +
                "  \"title\": \"Best Bootcamp Ever\",\n" +
                "  \"content\": \"Best Automation Bootcamp ever\",\n" +
                "  \"rating\": 5,\n" +
                "  \"isPublic\": true\n" +
                "}";
        Response response = RestAssured.given()
                .baseUri(baseUrl)
                .basePath(path)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(payload)
                .when()
                .log().all()
                .post().prettyPeek();
        testimonial_id = response.jsonPath().getString("data.Id");
        System.out.println("Testimonial ID is: " + testimonial_id);

        int responseCode = response.getStatusCode();
        assert responseCode == 201 : "Expected status code is 201 but got " + responseCode;

    }
    @Test (dependsOnMethods = "createTestimonial")
    public void getMyTestimonials(){
        String path="/testimonials";

        RestAssured.given()
                .baseUri(baseUrl)
                .basePath(path)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get()
                .then()
                .statusCode(200)
                .log().all();

    }

    @Test (dependsOnMethods = "createTestimonial")
    public void updateTestimonial(){
        String path = "/testimonials";
        String payload = "{\n" +
                "  \"title\": \"Updated Best Bootcamp ever Testimonials\",\n" +
                "  \"content\": \"Updated the Automation Bootcamp ever Testimonial\",\n" +
                "  \"rating\": 5\n" +
                "}";
        RestAssured.given()
                .baseUri(baseUrl)
                .basePath(path + "/" + testimonial_id)
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .put()
                .then()
                .statusCode(200)
                .body("data.Title", equalTo("Updated Best Bootcamp ever Testimonials"))
                .log().all();

    }
    @Test (dependsOnMethods = "updateTestimonial")
    public void deleteTestimonial(){
        String path="/testimonials";

        RestAssured.given()
                .baseUri(baseUrl)
                .basePath(path + "/" + testimonial_id)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .delete()
                .then()
                .statusCode(200)
                .log().all();

    }
}
