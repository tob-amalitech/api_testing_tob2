package com.apitest.tests;

import com.apitest.config.BaseTest;
import com.apitest.utils.TestData;
import java.util.Map;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test suite for exercising POST-related operations against the JSONPlaceholder
 * API. Each test method is annotated with Allure metadata to provide rich
 * reporting. The class extends {@link BaseTest} to inherit shared
 * configuration.
 */
@Epic("JSONPlaceholder API Tests")
@Feature("POST Requests")
public class PostPostsTest extends BaseTest {

    /**
     * Sends a POST request to create a new post and verifies the server
     * responds with HTTP 201. The response body is deserialized into a
     * {@link com.apitest.models.Post} object and additional assertions are
     * performed on its fields to demonstrate both REST Assured matcher usage
     * and AssertJ fluent assertions.
     */
    @Test(priority = 1, description = "Verify POST /posts creates a new resource and returns 201")
    @Story("Create Post")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Validates that creating a new post returns HTTP 201 and echoes the sent payload.")
    public void testCreatePost_Returns201WithCreatedPost() {
        Map newPost = TestData.getMap("testdata/posts.json", "newPost");
        String newPostTitle = (String) newPost.get("title");
        String newPostBody = (String) newPost.get("body");
        int newPostUserId = ((Number) newPost.get("userId")).intValue();

        log.info("Testing POST /posts with payload: {}", newPost);

        Response response = given()
            .spec(requestSpec)
            .body(newPost)
                .when()
                .post("/posts")
                .then()
                .statusCode(CREATED)
                .body("id", notNullValue())
                .body("title", equalTo(newPostTitle))
                .body("body", equalTo(newPostBody))
                .body("userId", equalTo(newPostUserId))
                .extract().response();

        Integer createdId = response.jsonPath().getInt("id");
        String createdTitle = response.jsonPath().getString("title");
        assertThat(createdId).isNotNull();
        assertThat(createdTitle).isEqualTo(newPostTitle);
        log.info("Post created successfully with ID: {}", createdId);
    }

    /**
     * Attempts to create a post using the minimal set of fields required by the
     * service. Demonstrates that optional or default fields are handled
     * gracefully. Only the status code and presence of an ID are asserted here.
     */
    @Test(priority = 2, description = "Verify POST /posts with minimal payload returns 201")
    @Story("Create Post - Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates API behaviour when creating a post with only required fields.")
    public void testCreatePost_WithMinimalPayload_Returns201() {
        Map minimalPost = TestData.getMap("testdata/posts.json", "minimalPost");

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

    /**
     * Asserts that the response from creating a post includes the proper
     * Content-Type header. Useful for confirming that server metadata is
     * correctly configured, not just the body content.
     */
    @Test(priority = 3, description = "Verify POST /posts response headers contain Content-Type")
    @Story("Create Post - Headers")
    @Severity(SeverityLevel.MINOR)
    @Description("Validates that the response headers from a POST include the expected Content-Type.")
    public void testCreatePost_ResponseHeaders() {
        Map headerPost = TestData.getMap("testdata/posts.json", "headerPost");

        log.info("Testing POST /posts - validating response headers");

        given()
            .spec(requestSpec)
            .body(headerPost)
            .when()
            .post("/posts")
            .then()
            .statusCode(CREATED)
            .header("Content-Type", containsString("application/json"));
    }

    /**
     * Sends a raw JSON string instead of a POJO to ensure the endpoint can
     * parse free-form JSON payloads. This is important when tests need to
     * construct dynamic bodies or exercise edge cases that are difficult to
     * represent with typed objects.
     */
    @Test(priority = 4, description = "Verify POST /posts with raw JSON string body")
    @Story("Create Post - Raw JSON")
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that the API accepts a raw JSON string as the request body.")
    public void testCreatePost_WithRawJsonBody() {
        String rawJson = TestData.getString("testdata/posts.json", "rawJson");

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
