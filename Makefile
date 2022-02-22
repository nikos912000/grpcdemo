IMAGE ?= docker.io/grpcdemo:latest

# Build and run the unit tests
build:
	./mvnw clean verify

# Run the integration tests
integration-tests:
	./mvnw clean verify -Dskip.unitTests=true -Pfunctional-tests

# Build the Docker image
docker: build
	docker build -t ${IMAGE} .

# Build the Docker image and load it into minikube
minikube-build: docker
	minikube image load --daemon=false --overwrite=true ${IMAGE}
