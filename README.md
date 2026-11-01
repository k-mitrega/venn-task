# Venn
Load attempt processing service (daily/weekly limits per customer). Spring Boot, H2, Flyway.

## Run

```bash
./gradlew bootRun
```

App on `http://localhost:8080`.

### File mode

Run with a file argument (NDJSON, same format as batch) to process it and exit — no web server is started:

```bash
./gradlew bootRun --args="input.txt"
```

Response is written to `output.txt` in the working directory;


## Test

```bash
./gradlew check    # unit + integration tests
```

## API

### Single load attempt

```bash
curl -X POST http://localhost:8080/api/v1/load-attempts \
  -H 'Content-Type: application/json' \
  -d '{"id":"load-1","customer_id":"customer-1","load_amount":"$123.45","time":"2018-01-03T12:00:00Z"}'
```

- `200` accepted/declined, body: `{"id":"load-1","customer_id":"customer-1","accepted":true}`
- `409` duplicate (problem+json)
- `400` invalid input (problem+json)

### Batch (NDJSON file)

```bash
curl -X POST http://localhost:8080/api/v1/load-attempts/batch \
  -F 'file=@input.txt'
```
