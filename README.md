# gRPC Spring Boot Demo

[![Build Status](https://github.com/nikos912000/grpcdemo/workflows/Build/badge.svg)](https://github.com/nikos912000/grpcdemo/actions?query=workflow:"Build")
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Overview

Demo application for a Spring Boot service which exposes a gRPC endpoint. Built using Maven.

## Installation

### How to build

Make sure you have Java 13+ installed.

Compile and run unit tests:
    
    ./mvnw clean verify

Run integration tests:

    ./mvnw clean verify -Dskip.unitTests=true -Pfunctional-tests

### How to run

#### Run on Intellij

You can run the app (i.e. *com.nkatirtzis.grpcdemo.Application*) on Intellij.

#### Run as a Spring Boot application

    ./mvnw spring-boot:run

#### Run the jar file

To run the jar file run the following command:

    java -jar service/target/service-0.0.1-SNAPSHOT-exec.jar

### Properties

| Property                            | Default value          | Description                                                                                      |
|:------------------------------------|:-----------------------|:-------------------------------------------------------------------------------------------------|
| `grpc.port`                         | 6565                   | Port where gRPC server is running                                                                |
| `spring.zipkin.enabled`             | true                   | Enables Zipkin for traces                                                                        |
| `spring.zipkin.baseUrl`             | http://127.0.0.1:9411/ | Host where trace data will be sent to. Need to be set if `spring.zipkin.enabled` is set to true. |
| `spring.sleuth.sampler.probability` | 1                      | Configures the probability of spans exported. Value should be in range \[0.0,1.0\]               |

## Usage

This app comes with [server reflection](https://github.com/grpc/grpc-java/blob/master/documentation/server-reflection-tutorial.md) enabled.

You can use a gRPC cmd tool such as [grpc_cli](https://github.com/grpc/grpc/blob/master/doc/command_line_tool.md) or [grpcurl](https://github.com/fullstorydev/grpcurl) to send requests to the app.

### Health check

Check the server’s health status using grpc_cli:

    grpc_cli call localhost:6565 Check ''
    
or using grpcurl:
    
    grpcurl -plaintext -d '' localhost:6565 grpc.health.v1.Health/Check
    
### Main endpoint

Hit the main endpoint using grpc_cli:

    grpc_cli call localhost:6565 Reviews 'id:1'
    
or using grpcurl:

    grpcurl -plaintext -d '{"id": 1}' localhost:6565 ReviewsService/GetReviews

### Metrics

The app exposes an endpoint under `\metrics` where you can see a list of available metrics.

### Traces

It has also been instrumented to report traces to [Zipkin](https://zipkin.io/). To run Zipkin locally:
    
    docker run -p 9411:9411 openzipkin/zipkin
    
Once you make gRPC request(s) to the app traces will be available on `http://localhost:9411/zipkin/`.
