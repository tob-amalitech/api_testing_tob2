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
 * Contains tests for updating existing resources using PUT (full replacement)
 * and PATCH (partial update) verbs. Includes header checks and response
 * validation to ensure updates are reflected correctly.
 */
@Epic("JSONPlaceholder API Tests")
@Feature("PUT Requests")
public class PutPostsTest extends BaseTest {

    @Test(priority = 1, description = "Verify PUT /posts/{id} fully updates a resource and returns 200")
    @Story("Update Post - Full Update")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Validates that a PUT request fully replaces a resource and returns HTTP 200.")
    /**
     * Executes a full replace of an existing post by sending all fields via a
     * PUT request. After receiving the response, the returned object is
     * deserialized and validated to ensure the replacement succeeded.
     */
    public void testUpdatePost_FullUpdate_Returns200() {
        int postId = 1;

        Map updatedPost = TestData.getMap("testdata/posts.json", "updatedPost");
        String updatedTitle = (String) updatedPost.get("title");

        log.info("Testing PUT /posts/{} with full update payload", postId);

        Response response = given()
            .spec(requestSpec)
            .pathParam("id", postId)
            .body(updatedPost)
            .when()
            .put("/posts/{id}")
            .then()
            .statusCode(OK)
            .body("id", equalTo(postId))
            .body("title", equalTo(updatedTitle))
            .body("body", equalTo("This is the fully updated body of post 1."))
            .body("userId", equalTo(updatedPost.get("userId")))
            .extract().response();

        Map returned = response.as(Map.class);
        assertThat(((Number) returned.get("id")).intValue()).isEqualTo(postId);
        assertThat((String) returned.get("title")).isEqualTo(updatedTitle);
        log.info("Post {} updated successfully", postId);
    }

    @Test(priority = 2, description = "Verify PUT /posts/{id} response body reflects changes")
    @Story("Update Post - Verify Response")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that the response body of a PUT request reflects the updated values.")
    /**
     * Verifies that the body of the response from a PUT request contains the
     * exact values that were sent. This guards against servers that pretend to
     * apply changes but silently ignore certain fields.
     */
    public void testUpdatePost_ResponseReflectsChanges() {
        int postId = 5;
        String newTitle = "Completely Replaced Title for Post 5";

        Map updatedPost = TestData.getMap("testdata/posts.json", "updatedPostFor5");

        log.info("Testing PUT /posts/{} - verifying response reflects updates", postId);

        given()
            .spec(requestSpec)
            .pathParam("id", postId)
            .body(updatedPost)
            .when()
            .put("/posts/{id}")
            .then()
            .statusCode(OK)
            .body("title", equalTo(newTitle));
    }

    @Test(priority = 3, description = "Verify PATCH /posts/{id} partially updates a resource")
    @Story("Update Post - Partial Update")
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that a PATCH request partially updates a resource (only changed fields).")
    /**
     * Demonstrates a PATCH request where only some fields are provided. The
     * API should merge the changes into the existing resource without altering
     * unspecified fields. Here we only change the title.
     */
    public void testPatchPost_PartialUpdate_Returns200() {
        int postId = 1;
        String partialJson = "{ \"title\": \"Patched Title Only\" }";

        log.info("Testing PATCH /posts/{} - partial update", postId);

        given()
                .spec(requestSpec)
                .pathParam("id", postId)
                .body(partialJson)
                .when()
                .patch("/posts/{id}")
                .then()
                .statusCode(OK)
                .body("id", equalTo(postId))
                .body("title", equalTo("Patched Title Only"));
    }

    @Test(priority = 4, description = "Verify PUT /posts/{id} response headers")
    @Story("Update Post - Headers")
    @Severity(SeverityLevel.MINOR)
    @Description("Validates Content-Type header returned from a PUT request.")
    /**
     * Simple header check similar to the POST tests but for an update operation.
     * Ensures that even on PUT the Content-Type header is correctly set.
     */
    public void testUpdatePost_ValidatesResponseHeaders() {
        int postId = 2;
        Map updatedPost = TestData.getMap("testdata/posts.json", "headerUpdate");

        log.info("Testing PUT /posts/{} - headers validation", postId);

        given()
                .spec(requestSpec)
                .pathParam("id", postId)
                .body(updatedPost)
                .when()
                .put("/posts/{id}")
                .then()
                .statusCode(OK)
                .header("Content-Type", containsString("application/json"));
    }
}
