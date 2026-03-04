package com.apitest.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.Map;

/**
 * Simple test data loader for reading JSON files from test resources.
 */
public final class TestData {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private TestData() {}

    /**
     * Load a named object from a JSON resource file and return it as a Map.
     * @param resourcePath classpath-relative resource (e.g. "testdata/posts.json")
     * @param key top-level key inside the JSON file
     */
    @SuppressWarnings("unchecked")
    public static Map getMap(String resourcePath, String key) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) throw new IllegalArgumentException("Resource not found: " + resourcePath);
            Map root = MAPPER.readValue(is, Map.class);
            Object node = root.get(key);
            if (node instanceof Map) return (Map) node;
            throw new IllegalArgumentException("Key is not a JSON object: " + key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load a named value from a JSON resource and return it as a String.
     * If the value is not a string it will be serialized to JSON.
     */
    public static String getString(String resourcePath, String key) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) throw new IllegalArgumentException("Resource not found: " + resourcePath);
            Map root = MAPPER.readValue(is, Map.class);
            Object node = root.get(key);
            if (node == null) return null;
            if (node instanceof String) return (String) node;
            return MAPPER.writeValueAsString(node);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read a numeric value from the JSON test data and return as int.
     */
    public static int getInt(String resourcePath, String key) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) throw new IllegalArgumentException("Resource not found: " + resourcePath);
            Map root = MAPPER.readValue(is, Map.class);
            Object node = root.get(key);
            if (node instanceof Number) return ((Number) node).intValue();
            throw new IllegalArgumentException("Key is not numeric: " + key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read a JSON array from the test data and return it as a List.
     */
    @SuppressWarnings("unchecked")
    public static java.util.List getList(String resourcePath, String key) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) throw new IllegalArgumentException("Resource not found: " + resourcePath);
            Map root = MAPPER.readValue(is, Map.class);
            Object node = root.get(key);
            if (node instanceof java.util.List) return (java.util.List) node;
            throw new IllegalArgumentException("Key is not a JSON array: " + key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
