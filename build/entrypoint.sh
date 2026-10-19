#!/bin/bash
# Starts every module's jar as its own background process inside one container,
# and exits (propagating the failing exit code) if any of them dies - so a
# crashed service doesn't leave the container silently running the rest.
set -e

pids=()

for jar in /app/*.jar; do
    name="$(basename "$jar" .jar)"
    echo "Starting $name"
    java -jar "$jar" &
    pids+=("$!")
done

terminate() {
    echo "Stopping all services..."
    kill "${pids[@]}" 2>/dev/null || true
    wait
    exit 0
}
trap terminate TERM INT

wait -n
exit_code=$?
echo "A service exited with code $exit_code; stopping the rest"
kill "${pids[@]}" 2>/dev/null || true
wait
exit "$exit_code"
