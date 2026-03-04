package com.apitest.tests;

import com.apitest.config.BaseTest;
import io.qameta.allure.*;
import com.apitest.utils.TestData;
import java.util.Map;
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
/**
 * Test suite that covers various GET operations against the Posts endpoint.
 * <p>The class demonstrates detailed response validation, use of query
 * parameters/path parameters, JSON schema validation, and extracting data into
 * domain models for assertion. All tests inherit configuration from
 * {@link BaseTest}.</p>
 */
@Epic("JSONPlaceholder API Tests")
@Feature("GET Requests")
public class GetPostsTest extends BaseTest {

    // ─── GET All Posts ────────────────────────────────────────────────────

    @Test(priority = 1, description = "Verify GET /posts returns 200 and non-empty list")
    @Story("Get All Posts")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Validates that retrieving all posts returns HTTP 200 and a non-empty array.")
    /**
     * Retrieves all posts and verifies:
     * <ul>
     *   <li>Status code is 200</li>
     *   <li>Response array is non-empty and contains 100 elements (known from
     *       JSONPlaceholder behaviour)</li>
     *   <li>First element has non-null id and title</li>
     * </ul>
     * <p>The response is deserialized into a {@link Post} list to illustrate
     * extracting complex payloads and performing AssertJ assertions on the
     * resulting objects.</p>
     */
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

        List<Map> posts = response.jsonPath().getList(".", Map.class);
        assertThat(posts).isNotEmpty();
        assertThat(posts).hasSize(100); // JSONPlaceholder always returns 100 posts
        log.info("Retrieved {} posts successfully", posts.size());
    }

    @Test(priority = 2, description = "Validate response headers for GET /posts")
    @Story("Get All Posts")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies that the Content-Type header is application/json.")
    /**
     * Simple header validation to make sure the GET /posts endpoint returns
     * JSON content. This can catch misconfigured servers that respond with
     * HTML or other unexpected types.
     */
    public void testGetAllPosts_ValidatesResponseHeaders() {

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
    /**
     * Fetches a single post by ID and confirms that the fields in the body
     * match the requested ID and that essential attributes are non-empty. This
     * demonstrates using path parameters and extracting a single POJO from the
     * response.
     */
    public void testGetPostById_ReturnsCorrectPost() {
        int postId = TestData.getInt("testdata/posts.json", "defaultPostId");
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

        Map<String, Object> post = response.as(Map.class);
        assertThat(((Number) post.get("id")).intValue()).isEqualTo(postId);
        assertThat((String) post.get("title")).isNotBlank();
        log.info("Post retrieved: ID={}, Title={}", post.get("id"), post.get("title"));
    }

    @Test(priority = 4, description = "Verify GET /posts/{id} for non-existent ID returns 404")
    @Story("Get Single Post - Negative")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies that requesting a non-existent post returns HTTP 404.")
    /**
     * Negative test verifying that requesting a post ID that does not exist
     * yields a 404 response. This checks error-handling behaviour of the API.
     */
    public void testGetPostById_NonExistentId_Returns404() {
        int invalidId = TestData.getInt("testdata/posts.json", "invalidId");
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
    /**
     * Demonstrates use of query parameters to filter results. In this case we
     * request posts belonging to a particular userId and then assert that every
     * returned {@link Post} has the expected userId value.
     */
    public void testGetPostsByUserId_ReturnsFilteredPosts() {
        int userId = TestData.getInt("testdata/posts.json", "userIdFilter");
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

        List<Map> posts = response.jsonPath().getList(".", Map.class);
        assertThat(posts).isNotEmpty();
        assertThat(posts).allMatch(p -> ((Number) p.get("userId")).intValue() == userId);
        log.info("Filtered posts count: {}", posts.size());
    }

    @Test(priority = 6, description = "Validate JSON schema for GET /posts/{id}")
    @Story("Schema Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Validates the response body structure against the expected JSON schema.")
    /**
     * Uses Rest Assured's JSON schema validator to compare the response body
     * against a pre-defined schema file located in the classpath. This is a
     * good way to detect structural regressions in the API contract.
     */
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
    /**
     * Retrieves comments associated with a post and validates several aspects:
     * <ul>
     *   <li>HTTP status is 200</li>
     *   <li>List is non-empty</li>
     *   <li>All comments have the expected postId</li>
     *   <li>The first comment's email matches a basic email regex</li>
     * </ul>
     * <p>This method illustrates deeper response body assertions including
     * regex matching.</p>
     */
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