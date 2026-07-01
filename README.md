# SendByte Java SDK

The Java SDK for the [SendByte](https://www.sendbyte.africa) transactional email API. Send emails, track their lifecycle, and verify webhooks from any JVM application.

- **Java 8+**
- Dependencies: [OkHttp](https://square.github.io/okhttp/) (HTTP) and [Jackson](https://github.com/FasterXML/jackson) (JSON)
- Synchronous and asynchronous (`CompletableFuture`) methods
- Typed exceptions mapped from the API's machine-readable error codes
- Built-in webhook signature verification

> This first release covers the **Emails** resource (send, retrieve, list) plus webhook signature verification. Domains, API keys, templates, and webhook-endpoint management are structured to drop in as additional resources.

## Installation

### Maven

```xml
<dependency>
    <groupId>africa.sendbyte</groupId>
    <artifactId>sendbyte-java</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'africa.sendbyte:sendbyte-java:0.1.0'
```

## Quickstart

Grab a sandbox key (`sk_test_...`) from the [dashboard](https://app.sendbyte.africa) — sandbox keys simulate the full pipeline with no domain setup required.

```java
import africa.sendbyte.SendByteClient;
import africa.sendbyte.emails.Email;
import africa.sendbyte.emails.SendEmailRequest;

try (SendByteClient client = new SendByteClient(System.getenv("SENDBYTE_API_KEY"))) {
    Email email = client.emails().send(SendEmailRequest.builder()
            .from("PayLink <receipts@paylink.ng>")
            .to("amaka@halo.ng")
            .subject("Receipt for ₦45,000")
            .html("<p>Hi Amaka, your payment of <strong>₦45,000</strong> was received.</p>")
            .text("Hi Amaka, your payment of ₦45,000 was received.")
            .tags("receipt", "payment")
            .build());

    System.out.println("Queued: " + email.getId());
}
```

Store your key in an environment variable or secrets manager — never hard-code it.

## Configuration

Pass an API key directly, or build an options object to override the base URL (useful for testing) and timeouts.

```java
import java.time.Duration;
import africa.sendbyte.SendByteClient;
import africa.sendbyte.SendByteClientOptions;

SendByteClient client = new SendByteClient(SendByteClientOptions.builder()
        .apiKey(System.getenv("SENDBYTE_API_KEY"))
        .readTimeout(Duration.ofSeconds(20))
        .build());
```

The client is thread-safe — create one and reuse it. Call `close()` (or use try-with-resources) on shutdown to release the connection pool.

## Sending email

`SendEmailRequest` uses a fluent builder; most fields are optional. At minimum, provide `from`, at least one `to`, and one of `html`, `text`, or `templateId`.

```java
SendEmailRequest request = SendEmailRequest.builder()
        .from("PayLink <receipts@paylink.ng>")
        .to("amaka@halo.ng", "team@paylink.ng")   // up to 50 recipients
        .cc("audit@paylink.ng")
        .replyTo("support@paylink.ng")
        .subject("Your receipt")
        .html("<p>Payment received.</p>")
        .text("Payment received.")
        .header("X-Campaign-Id", "q3-receipts")
        .tags("receipt")
        .idempotencyKey("order-4421-receipt")     // safe retries
        .build();

Email email = client.emails().send(request);
```

### Templates, attachments, scheduling

```java
import africa.sendbyte.emails.Attachment;
import java.time.Instant;
import java.nio.file.Files;
import java.nio.file.Paths;

SendEmailRequest request = SendEmailRequest.builder()
        .from("PayLink <receipts@paylink.ng>")
        .to("amaka@halo.ng")
        .templateId("welcome")                     // UUID or template name
        .variable("first_name", "Amaka")
        .variable("amount", "₦45,000")
        .addAttachment(Attachment.of("receipt.pdf", "application/pdf",
                Files.readAllBytes(Paths.get("receipt.pdf"))))
        .scheduledAt(Instant.parse("2026-07-01T09:00:00Z"))
        .build();

Email email = client.emails().send(request);
```

### Asynchronous sends

Every method has an `...Async` sibling returning a `CompletableFuture`.

```java
client.emails().sendAsync(request)
        .thenAccept(e -> System.out.println("Queued: " + e.getId()))
        .exceptionally(err -> { err.printStackTrace(); return null; });
```

## Retrieving and listing

```java
import africa.sendbyte.emails.Email;
import africa.sendbyte.emails.EmailEvent;

Email email = client.emails().get("em_01j...");
System.out.println(email.status());               // typed EmailStatus enum
for (EmailEvent event : email.getEvents()) {
    System.out.println(event.getType() + " @ " + event.getCreatedAt());
}
```

Listing uses cursor pagination. Take the `id` of the last item and pass it as `after` while `isHasMore()` is true.

```java
import africa.sendbyte.emails.EmailList;
import africa.sendbyte.emails.EmailStatus;
import africa.sendbyte.emails.ListEmailsParams;

String cursor = null;
do {
    EmailList page = client.emails().list(ListEmailsParams.builder()
            .limit(100)
            .status(EmailStatus.DELIVERED)
            .after(cursor)
            .build());

    page.getData().forEach(e -> System.out.println(e.getId()));

    cursor = page.isHasMore()
            ? page.getData().get(page.getData().size() - 1).getId()
            : null;
} while (cursor != null);
```

## Error handling

Every non-2xx response is thrown as a `SendByteException` (or a specific subclass) carrying the machine-readable `code`, HTTP `status`, `docsUrl`, and the `x-request-id` for support.

```java
import africa.sendbyte.exceptions.*;

try {
    client.emails().send(request);
} catch (RateLimitException e) {
    Integer retryAfter = e.getRetryAfterSeconds();   // from the Retry-After header
    // back off and retry
} catch (DomainNotVerifiedException e) {
    // prompt the user to verify their sending domain
} catch (SendByteException e) {
    System.err.printf("code=%s status=%d docs=%s request_id=%s%n",
            e.getCode(), e.getStatus(), e.getDocsUrl(), e.getRequestId());
    throw e;
}
```

| Exception | Code | HTTP |
| --- | --- | --- |
| `ValidationException` | `validation_error` | 422 |
| `AuthenticationException` | `authentication_error` | 401 |
| `AuthorizationException` | `authorization_error` | 403 |
| `DomainNotVerifiedException` | `domain_not_verified` | 403 |
| `SuppressedRecipientException` | `suppressed_recipient` | 422 |
| `NotFoundException` | `not_found` | 404 |
| `IdempotencyConflictException` | `idempotency_conflict` | 409 |
| `RateLimitException` | `rate_limit_exceeded` | 429 |
| `InternalServerException` | `internal_error` | 500 |
| `SendByteConnectionException` | — | (transport failure) |

## Webhooks

Verify the `sendbyte-signature` header against the **raw** request body — always before parsing JSON. The default 5-minute timestamp tolerance guards against replay attacks.

```java
import africa.sendbyte.webhooks.WebhookSignature;

// Servlet example
byte[] rawBody = readRawBody(request);   // do not parse to JSON first
String signature = request.getHeader(WebhookSignature.SIGNATURE_HEADER);

if (!WebhookSignature.verify(webhookSecret, signature, rawBody)) {
    response.setStatus(401);
    return;
}

// safe to parse and handle the event now
response.setStatus(200);
```

## Building from source

```bash
mvn clean test      # compile and run the unit tests (no network required)
mvn package         # build the jar
```

## License

MIT
