/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.myapp.imageconversion;


import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/convert")
public class ImageBase64Converter {

    @Inject
    @ConfigProperty(name = "workflowUrl")
    private String workflowUrl;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ImageResponse convertImageToBase64(ImageRequest imageRequest) {
        try {
            String imagePath = imageRequest.getImagePath();

            byte[] imageBytes = Files.readAllBytes(java.nio.file.Path.of(imagePath));

            byte[] base64ImageBytes = Base64.getEncoder().encode(imageBytes);

            ImageResponse response = new ImageResponse(base64ImageBytes);

            BufferedImage workflowResponseImage = invokeWorkflow(response);

            String filePath = "src/main/resources/images/output_image.jpg";

            File imageFile = new File(filePath);
            ImageIO.write(workflowResponseImage, "jpg", imageFile);

            response.setResponseImageFilePath(filePath);

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            throw new WebApplicationException("Error reading or converting the image", 500);
        }
    }

    private BufferedImage invokeWorkflow(ImageResponse imageResponse) {

        Client client = ClientBuilder.newClient();

        try {
            Response response = client.target(workflowUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(imageResponse, MediaType.APPLICATION_JSON));

            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                throw new WebApplicationException("Workflow invocation failed with status code: " + response.getStatus());
            }

            byte[] responseBytes = response.readEntity(byte[].class);
            byte[] decodedBytes = Base64.getDecoder().decode(responseBytes);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes);
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new WebApplicationException("Error processing the workflow response", 500);
        } finally {
            client.close();
        }
    }
}
