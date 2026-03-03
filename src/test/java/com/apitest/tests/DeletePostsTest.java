package com.apitest.tests;

import com.apitest.config.BaseTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("JSONPlaceholder API Tests")
@Feature("DELETE Requests")
public class DeletePostsTest extends BaseTest {

    @Test(priority = 1, description = "Verify DELETE /posts/{id} returns 200")
    @Story("Delete Post")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Validates that deleting an existing post returns HTTP 200.")
    public void testDeletePost_Returns200() {
        int postId = 1;
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
    public void testDeletePost_ResponseBodyIsEmpty() {
        int postId = 2;
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
    public void testDeletePost_MultipleIds() {
        int[] postIds = {3, 10, 50, 100};

        for (int postId : postIds) {
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
    public void testDeletePost_ValidatesResponseHeaders() {
        int postId = 5;
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
