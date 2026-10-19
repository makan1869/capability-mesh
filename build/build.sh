#!/usr/bin/env bash
# Builds every module with Maven, then packages all of them into one Docker
# image (see Dockerfile / entrypoint.sh in this same directory).
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

IMAGE_NAME="${IMAGE_NAME:-capability-mesh}"
IMAGE_TAG="${IMAGE_TAG:-latest}"

echo "==> Building all modules with Maven"
mvn -f "$PROJECT_ROOT/pom.xml" clean package

echo "==> Building Docker image ${IMAGE_NAME}:${IMAGE_TAG}"
docker build -t "${IMAGE_NAME}:${IMAGE_TAG}" -f "$SCRIPT_DIR/Dockerfile" "$PROJECT_ROOT"

echo "==> Done. Run with:"
echo "    docker run --rm \\"
echo "      -p 8081:8081 -p 8082:8082 -p 8083:8083 \\"
echo "      -p 8084:8084 -p 8085:8085 -p 8090:8090 \\"
echo "      ${IMAGE_NAME}:${IMAGE_TAG}"
