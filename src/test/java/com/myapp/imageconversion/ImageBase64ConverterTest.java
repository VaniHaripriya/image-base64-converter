package com.myapp.imageconversion;

import io.restassured.http.ContentType;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import java.io.File;
import static org.hamcrest.MatcherAssert.assertThat;
import static io.restassured.RestAssured.given;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ImageBase64ConverterTest {

    @Test
    public void testImageConversionEndpoint() {

        ImageRequest request = new ImageRequest("src/test/resources/test_image.jpg");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/convert")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("base64Image", CoreMatchers.notNullValue())
                .body("responseImageFilePath", CoreMatchers.notNullValue());

        ImageResponse response = given()
                .contentType(ContentType.TEXT)
                .body(request)
                .when().post("/convert")
                .then()
                .extract().as(ImageResponse.class);

        assertThat(response.getResponseImageFilePath(), CoreMatchers.notNullValue());
        assertThat(new File(response.getResponseImageFilePath()).exists(), CoreMatchers.is(true));

    }
}
