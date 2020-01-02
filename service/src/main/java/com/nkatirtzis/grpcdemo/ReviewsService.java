package com.nkatirtzis.grpcdemo;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;

/**
 * gRPC service for reviews.
 */
@Component
public class ReviewsService extends ReviewsServiceGrpc.ReviewsServiceImplBase {

    @Override
    public void getReviews(Reviews.Request request, StreamObserver<Reviews.Response> responseObserver) {
        Reviews.Response reviews = Reviews.Response.newBuilder().setText("review " + request.getId() + " text").build();
        responseObserver.onNext(reviews);
        responseObserver.onCompleted();
    }
}
