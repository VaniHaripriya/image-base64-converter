# Quarkus Image Converter

This is a simple Quarkus application designed to convert image files to Base64 format and make calls to SonataFlow. It is capable of receiving responses from SonataFlow in Base64 format and converting them back into images.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Introduction

This Quarkus application serves as a utility for converting image files to Base64 format and interacting with SonataFlow for further processing. It provides an efficient way to handle image conversions and integrate with SonataFlow's functionality.

## Features

- Image-to-Base64 Conversion
- Integration with SonataFlow
- Base64-to-Image Conversion

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Sending a request

To perform image conversion in `src/main/resources/images/coco_image.jpg`, run the following request:

```shell
curl -X 'POST' \
  'http://localhost:8080/convert' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "imagePath": "src/main/resources/images/coco_image.jpg" }'
```