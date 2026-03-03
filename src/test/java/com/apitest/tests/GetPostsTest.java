package com.apitest.tests;

import com.apitest.config.BaseTest;
import com.apitest.models.Post;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test class covering all GET request scenarios for the Posts endpoint.
 */
@Epic("JSONPlaceholder API Tests")
@Feature("GET Requests")
public class GetPostsTest extends BaseTest {

    // ─── GET All Posts ────────────────────────────────────────────────────

    @Test(priority = 1, description = "Verify GET /posts returns 200 and non-empty list")
    @Story("Get All Posts")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Validates that retrieving all posts returns HTTP 200 and a non-empty array.")
    public void testGetAllPosts_Returns200AndNonEmptyList() {
        log.info("Testing GET /posts - all posts");

        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/posts")
                .then()
                .statusCode(OK)
                .body("size()", greaterThan(0))
                .body("[0].id", notNullValue())
                .body("[0].title", notNullValue())
                .extract().response();

        List<Post> posts = response.jsonPath().getList(".", Post.class);
        assertThat(posts).isNotEmpty();
        assertThat(posts).hasSize(100); // JSONPlaceholder always returns 100 posts
        log.info("Retrieved {} posts successfully", posts.size());
    }

    @Test(priority = 2, description = "Validate response headers for GET /posts")
    @Story("Get All Posts")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies that the Content-Type header is application/json.")
    public void testGetAllPosts_ValidatesResponseHeaders() {
        log.info("Testing GET /posts - headers validation");

        given()
                .spec(requestSpec)
                .when()
                .get("/posts")
                .then()
                .statusCode(OK)
                .header("Content-Type", containsString("application/json"));
    }

    // ─── GET Single Post ──────────────────────────────────────────────────

    @Test(priority = 3, description = "Verify GET /posts/{id} returns correct post")
    @Story("Get Single Post")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Validates that fetching a specific post by ID returns the correct resource.")
    public void testGetPostById_ReturnsCorrectPost() {
        int postId = 1;
        log.info("Testing GET /posts/{}", postId);

        Response response = given()
                .spec(requestSpec)
                .pathParam("id", postId)
                .when()
                .get("/posts/{id}")
                .then()
                .statusCode(OK)
                .body("id", equalTo(postId))
                .body("userId", notNullValue())
                .body("title", not(emptyOrNullString()))
                .body("body", not(emptyOrNullString()))
                .extract().response();

        Post post = response.as(Post.class);
        assertThat(post.getId()).isEqualTo(postId);
        assertThat(post.getTitle()).isNotBlank();
        log.info("Post retrieved: ID={}, Title={}", post.getId(), post.getTitle());
    }

    @Test(priority = 4, description = "Verify GET /posts/{id} for non-existent ID returns 404")
    @Story("Get Single Post - Negative")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies that requesting a non-existent post returns HTTP 404.")
    public void testGetPostById_NonExistentId_Returns404() {
        int invalidId = 9999;
        log.info("Testing GET /posts/{} - expecting 404", invalidId);

        given()
                .spec(requestSpec)
                .pathParam("id", invalidId)
                .when()
                .get("/posts/{id}")
                .then()
                .statusCode(NOT_FOUND);
    }

    @Test(priority = 5, description = "Verify GET /posts with userId filter returns filtered results")
    @Story("Get Posts with Query Params")
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that filtering posts by userId query parameter returns correct subset.")
    public void testGetPostsByUserId_ReturnsFilteredPosts() {
        int userId = 1;
        log.info("Testing GET /posts?userId={}", userId);

        Response response = given()
                .spec(requestSpec)
                .queryParam("userId", userId)
                .when()
                .get("/posts")
                .then()
                .statusCode(OK)
                .body("size()", greaterThan(0))
                .body("userId", everyItem(equalTo(userId)))
                .extract().response();

        List<Post> posts = response.jsonPath().getList(".", Post.class);
        assertThat(posts).isNotEmpty();
        assertThat(posts).allMatch(p -> p.getUserId().equals(userId));
        log.info("Filtered posts count: {}", posts.size());
    }

    @Test(priority = 6, description = "Validate JSON schema for GET /posts/{id}")
    @Story("Schema Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Validates the response body structure against the expected JSON schema.")
    public void testGetPostById_ValidatesJsonSchema() {
        log.info("Testing JSON schema validation for GET /posts/1");

        given()
                .spec(requestSpec)
                .pathParam("id", 1)
                .when()
                .get("/posts/{id}")
                .then()
                .statusCode(OK)
                .body(matchesJsonSchemaInClasspath("schemas/post-schema.json"));
    }

    // ─── GET Comments for a Post ──────────────────────────────────────────

    @Test(priority = 7, description = "Verify GET /posts/{id}/comments returns associated comments")
    @Story("Get Post Comments")
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that fetching comments for a post returns associated comment objects.")
    public void testGetCommentsByPostId_ReturnsComments() {
        int postId = 1;
        log.info("Testing GET /posts/{}/comments", postId);

        given()
                .spec(requestSpec)
                .pathParam("id", postId)
                .when()
                .get("/posts/{id}/comments")
                .then()
                .statusCode(OK)
                .body("size()", greaterThan(0))
                .body("postId", everyItem(equalTo(postId)))
                .body("[0].email", matchesPattern(".*@.*\\..*"));
    }
}