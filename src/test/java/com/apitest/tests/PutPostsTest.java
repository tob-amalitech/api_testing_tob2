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
@Feature("PUT Requests")
public class PutPostsTest extends BaseTest {

    @Test(priority = 1, description = "Verify PUT /posts/{id} fully updates a resource and returns 200")
    @Story("Update Post - Full Update")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Validates that a PUT request fully replaces a resource and returns HTTP 200.")
    public void testUpdatePost_FullUpdate_Returns200() {
        int postId = 1;

        Post updatedPost = new Post();
        updatedPost.setId(postId);
        updatedPost.setUserId(1);
        updatedPost.setTitle("Updated Post Title");
        updatedPost.setBody("This is the fully updated body of post 1.");

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
                .body("title", equalTo(updatedPost.getTitle()))
                .body("body", equalTo(updatedPost.getBody()))
                .body("userId", equalTo(updatedPost.getUserId()))
                .extract().response();

        Post returned = response.as(Post.class);
        assertThat(returned.getId()).isEqualTo(postId);
        assertThat(returned.getTitle()).isEqualTo(updatedPost.getTitle());
        log.info("Post {} updated successfully", postId);
    }

    @Test(priority = 2, description = "Verify PUT /posts/{id} response body reflects changes")
    @Story("Update Post - Verify Response")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that the response body of a PUT request reflects the updated values.")
    public void testUpdatePost_ResponseReflectsChanges() {
        int postId = 5;
        String newTitle = "Completely Replaced Title for Post 5";

        Post updatedPost = new Post();
        updatedPost.setId(postId);
        updatedPost.setUserId(2);
        updatedPost.setTitle(newTitle);
        updatedPost.setBody("New body text for post 5 via PUT.");

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
    public void testUpdatePost_ValidatesResponseHeaders() {
        int postId = 2;
        Post updatedPost = new Post();
        updatedPost.setId(postId);
        updatedPost.setUserId(1);
        updatedPost.setTitle("Header Check Update");
        updatedPost.setBody("Testing response headers on PUT");

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
