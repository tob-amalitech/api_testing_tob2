package com.apitest.tests;

import com.apitest.config.BaseTest;
import io.qameta.allure.*;
import com.apitest.utils.TestData;
import java.util.List;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*; //provide reusable, declarative methods that let you write clear, readable assertions in tests

/**
 * Contains tests for removal of posts using DELETE. Covers basic success
 * scenarios, response body verification, header checks, and iterating over
 * multiple identifiers to validate consistent behaviour.
 */
@Epic("JSONPlaceholder API Tests")
@Feature("DELETE Requests")
public class DeletePostsTest extends BaseTest {

    @Test(priority = 1, description = "Verify DELETE /posts/{id} returns 200")
    @Story("Delete Post")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Validates that deleting an existing post returns HTTP 200.")
    /**
     * Deletes a single post by ID and asserts that the HTTP status code is 200.
     * This is the simplest happy‑path deletion test.
     */
    public void testDeletePost_Returns200() {
        int postId = TestData.getInt("testdata/posts.json", "defaultPostId");
        log.info("Testing DELETE /posts/{}", postId);

        given()
                .spec(requestSpec)
                .pathParam("id", postId)
                .when()
                .delete("/posts/{id}")
                .then()
                .statusCode(OK);
    }

    @Test(priority = 2, description = "Verify DELETE /posts/{id} returns empty response body")
    @Story("Delete Post - Response Body")
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that the response body for a DELETE request is an empty JSON object.")
    /**
     * After deletion the JSONPlaceholder API returns an empty object ("{}").
     * This test extracts the body as a string and asserts that it exactly
     * matches the expected value, demonstrating how to inspect responses that
     * do not map cleanly to POJOs.
     */
    public void testDeletePost_ResponseBodyIsEmpty() {
        int postId = TestData.getInt("testdata/posts.json", "defaultDeleteId");
        log.info("Testing DELETE /posts/{} - validating empty response body", postId);

        Response response = given()
                .spec(requestSpec)
                .pathParam("id", postId)
                .when()
                .delete("/posts/{id}")
                .then()
                .statusCode(OK)
                .extract().response();

        String body = response.getBody().asString().trim();
        assertThat(body).isEqualTo("{}");
        log.info("Delete response body: {}", body);
    }

    @Test(priority = 3, description = "Verify DELETE /posts/{id} for different post IDs")
    @Story("Delete Post - Multiple IDs")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies delete succeeds for multiple different post IDs.")
    /**
     * Loop-based test iterating across several post IDs to validate that the
     * deletion endpoint behaves consistently. The loop is simple but shows how
     * to parameterize requests programmatically without a data provider.
     */
    public void testDeletePost_MultipleIds() {
        List postIds = TestData.getList("testdata/posts.json", "deleteIds");

        for (Object idObj : postIds) {
            int postId = ((Number) idObj).intValue();
            log.info("Testing DELETE /posts/{}", postId);
            given()
                .spec(requestSpec)
                .pathParam("id", postId)
                .when()
                .delete("/posts/{id}")
                .then()
                .statusCode(OK);
        }
        log.info("All delete requests returned 200 successfully");
    }

    @Test(priority = 4, description = "Verify DELETE /posts/{id} response headers")
    @Story("Delete Post - Headers")
    @Severity(SeverityLevel.MINOR)
    @Description("Validates that Content-Type header is present in DELETE response.")
    /**
     * Ensures that the DELETE operation returns the correct HTTP headers
     * (Content-Type in this case). Useful when tests aim to assert not only the
     * body but also metadata sent by the server.
     */
    public void testDeletePost_ValidatesResponseHeaders() {
        int postId = TestData.getInt("testdata/posts.json", "headerDeleteId");
        log.info("Testing DELETE /posts/{} - validating response headers", postId);

        given()
                .spec(requestSpec)
                .pathParam("id", postId)
                .when()
                .delete("/posts/{id}")
                .then()
                .statusCode(OK)
                .header("Content-Type", containsString("application/json"));
    }
}
