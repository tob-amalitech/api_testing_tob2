package com.apitest.tests;

import com.apitest.config.BaseTest;
import com.apitest.models.Post;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("JSONPlaceholder API Tests")
@Feature("POST Requests")
public class PostPostsTest extends BaseTest {

    @Test(priority = 1, description = "Verify POST /posts creates a new resource and returns 201")
    @Story("Create Post")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Validates that creating a new post returns HTTP 201 and echoes the sent payload.")
    public void testCreatePost_Returns201WithCreatedPost() {
        Post newPost = new Post();
        newPost.setUserId(1);
        newPost.setTitle("Test Post Title");
        newPost.setBody("This is the body of the test post created via REST Assured.");

        log.info("Testing POST /posts with payload: {}", newPost);

        Response response = given()
                .spec(requestSpec)
                .body(newPost)
                .when()
                .post("/posts")
                .then()
                .statusCode(CREATED)
                .body("id", notNullValue())
                .body("title", equalTo(newPost.getTitle()))
                .body("body", equalTo(newPost.getBody()))
                .body("userId", equalTo(newPost.getUserId()))
                .extract().response();

        Post created = response.as(Post.class);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getTitle()).isEqualTo(newPost.getTitle());
        log.info("Post created successfully with ID: {}", created.getId());
    }

    @Test(priority = 2, description = "Verify POST /posts with minimal payload returns 201")
    @Story("Create Post - Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates API behaviour when creating a post with only required fields.")
    public void testCreatePost_WithMinimalPayload_Returns201() {
        Post minimalPost = new Post();
        minimalPost.setTitle("Minimal Post");
        minimalPost.setBody("Minimal body content");
        minimalPost.setUserId(5);

        log.info("Testing POST /posts with minimal payload");

        given()
                .spec(requestSpec)
                .body(minimalPost)
                .when()
                .post("/posts")
                .then()
                .statusCode(CREATED)
                .body("id", notNullValue());
    }

    @Test(priority = 3, description = "Verify POST /posts response headers contain Content-Type")
    @Story("Create Post - Headers")
    @Severity(SeverityLevel.MINOR)
    @Description("Validates that the response headers from a POST include the expected Content-Type.")
    public void testCreatePost_ResponseHeaders() {
        Post newPost = new Post();
        newPost.setUserId(1);
        newPost.setTitle("Header Validation Post");
        newPost.setBody("Checking response headers");

        log.info("Testing POST /posts - validating response headers");

        given()
                .spec(requestSpec)
                .body(newPost)
                .when()
                .post("/posts")
                .then()
                .statusCode(CREATED)
                .header("Content-Type", containsString("application/json"));
    }

    @Test(priority = 4, description = "Verify POST /posts with raw JSON string body")
    @Story("Create Post - Raw JSON")
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that the API accepts a raw JSON string as the request body.")
    public void testCreatePost_WithRawJsonBody() {
        String rawJson = "{ \"userId\": 3, \"title\": \"Raw JSON Post\", \"body\": \"Posted via raw JSON string\" }";

        log.info("Testing POST /posts with raw JSON body");

        given()
                .spec(requestSpec)
                .body(rawJson)
                .when()
                .post("/posts")
                .then()
                .statusCode(CREATED)
                .body("title", equalTo("Raw JSON Post"))
                .body("userId", equalTo(3));
    }
}
