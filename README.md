# REST Assured API Test Automation

[![API Tests](https://github.com/YOUR_USERNAME/rest-assured-api-testing/actions/workflows/api-tests.yml/badge.svg)](https://github.com/YOUR_USERNAME/rest-assured-api-testing/actions/workflows/api-tests.yml)

Automated API test suite targeting [JSONPlaceholder](https://jsonplaceholder.typicode.com/) built with **REST Assured**, **TestNG**, and **Allure Reports**.

---

## 📦 Tech Stack

| Tool | Purpose |
|---|---|
| Java 11 | Language |
| Maven | Build & Dependency Management |
| REST Assured 5.4 | API Test Framework |
| TestNG 7.9 | Test Runner |
| Allure 2.25 | Test Reporting |
| Docker | Containerisation |
| GitHub Actions | CI/CD |

---

## 🗂️ Project Structure

```
src/test/java/com/apitest/
├── config/
│   └── BaseTest.java          # REST Assured config + Allure filter
├── models/
│   ├── Post.java              # Post POJO
│   └── Comment.java           # Comment POJO
└── tests/
    ├── GetPostsTest.java       # 7 GET test cases
    ├── PostPostsTest.java      # 4 POST test cases
    ├── PutPostsTest.java       # 4 PUT/PATCH test cases
    └── DeletePostsTest.java    # 4 DELETE test cases

src/test/resources/
├── schemas/post-schema.json   # JSON Schema for validation
├── testng.xml                 # TestNG suite definition
└── logback-test.xml           # Logging configuration
```

---

## 🚀 Running Locally

### Prerequisites
- Java 11+
- Maven 3.8+

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/rest-assured-api-testing.git
cd rest-assured-api-testing

# Run all tests
mvn test

# Generate Allure report
mvn allure:report

# Open report in browser
mvn allure:serve
```

---

## 🐳 Docker

```bash
# Build and run all tests in Docker
docker-compose up --build

# View report at http://localhost:8080

# Run tests only
docker build --target builder -t api-tests .
docker run api-tests mvn test
```

---

## 🧪 Test Coverage

| Endpoint | Method | Test Cases |
|---|---|---|
| `/posts` | GET | All posts, headers, filter by userId |
| `/posts/{id}` | GET | By ID, 404 for missing, JSON schema |
| `/posts/{id}/comments` | GET | Comments association |
| `/posts` | POST | Create, minimal payload, headers, raw JSON |
| `/posts/{id}` | PUT | Full update, response validation, headers |
| `/posts/{id}` | PATCH | Partial update |
| `/posts/{id}` | DELETE | 200 response, empty body, multiple IDs |

---

## 📊 Reports

Allure reports are auto-generated on every CI run and published to GitHub Pages:

```
https://YOUR_USERNAME.github.io/rest-assured-api-testing/allure-report/
```

---

## 🔧 CI/CD Pipeline

The GitHub Actions pipeline (`.github/workflows/api-tests.yml`) runs:

1. **Build** — Compile & validate project
2. **API Tests** — Execute full test suite
3. **Allure Report** — Generate and publish to GitHub Pages
4. **Docker** — Build and push image to Docker Hub (main branch only)

---

## 🔑 Required GitHub Secrets

| Secret | Description |
|---|---|
| `DOCKERHUB_USERNAME` | Docker Hub username |
| `DOCKERHUB_TOKEN` | Docker Hub access token |
