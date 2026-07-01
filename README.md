# SendByte Java SDK

The Java SDK for the [SendByte](https://www.sendbyte.africa) transactional email API. Send emails, track their lifecycle, and verify webhooks from any JVM application.

- **Java 8+**
- Dependencies: [OkHttp](https://square.github.io/okhttp/) (HTTP) and [Jackson](https://github.com/FasterXML/jackson) (JSON)
- Synchronous and asynchronous (`CompletableFuture`) methods
- Typed exceptions mapped from the API's machine-readable error codes
- Built-in webhook signature verification

> Full API coverage: **Emails** (send, retrieve, list), **Domains** (register, retrieve, list, verify), **Templates** (create, retrieve, list, update, delete, render, preview), **Webhooks** (manage endpoints, deliveries, replay) with signature verification, and **API keys** (create, list, revoke).

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

## Domains

Register a sending domain, publish the returned DNS records at your provider, then verify to go live.

```java
import africa.sendbyte.domains.Domain;
import africa.sendbyte.domains.DnsRecord;
import africa.sendbyte.domains.DomainStatus;

// 1. Register — returns the SPF, DKIM, and DMARC records to publish
Domain domain = client.domains().create("paylink.ng");
for (DnsRecord record : domain.getDnsRecords()) {
    System.out.printf("%s  %s  %s%n", record.getType(), record.getHost(), record.getValue());
}

// 2. After publishing the records, trigger a live DNS check
Domain checked = client.domains().verify(domain.getId());
if (checked.status() == DomainStatus.VERIFIED) {
    System.out.println("Ready for live sends.");
}

// List and fetch
client.domains().list().getData().forEach(d -> System.out.println(d.getDomain()));
Domain one = client.domains().get(domain.getId());
```

DNS propagation can take up to an hour; records that haven't propagated report `pass = false` in `verify(...).getChecks()`. SPF + DKIM passing is sufficient to reach `verified` (DMARC is recommended but optional).

## Templates

Create reusable server-side templates (Handlebars, or MJML compiled to responsive HTML), then send them by passing the template id or name as `templateId` on a send request.

```java
import africa.sendbyte.templates.Template;
import africa.sendbyte.templates.TemplateRequest;
import africa.sendbyte.templates.RenderedTemplate;
import africa.sendbyte.templates.RenderRequest;
import java.util.Collections;

// Create
Template tpl = client.templates().create(TemplateRequest.builder()
        .name("welcome")
        .subject("Welcome to PayLink, {{first_name}}!")
        .html("<p>Hi {{first_name}}, your account is ready.</p>")
        .text("Hi {{first_name}}, your account is ready.")
        .build());

// Preview a saved template against sample variables (nothing is sent)
RenderedTemplate preview = client.templates().preview(tpl.getId(),
        Collections.singletonMap("first_name", "Amaka"));
System.out.println(preview.getSubject());   // "Welcome to PayLink, Amaka!"

// Or render an unsaved body inline (e.g. a live editor preview)
RenderedTemplate rendered = client.templates().render(RenderRequest.builder()
        .subject("Hi {{first_name}}")
        .html("<p>Hi {{first_name}}.</p>")
        .variable("first_name", "Amaka")
        .build());

// Manage
client.templates().list().getData().forEach(t -> System.out.println(t.getName()));
client.templates().update(tpl.getId(), TemplateRequest.builder()
        .name("welcome").subject("Welcome!").html("<p>Updated.</p>").build());
client.templates().delete(tpl.getId());
```

Then send with it:

```java
client.emails().send(SendEmailRequest.builder()
        .from("PayLink <receipts@paylink.ng>")
        .to("amaka@halo.ng")
        .templateId("welcome")               // id or name
        .variable("first_name", "Amaka")
        .build());
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

## API keys

Create scoped keys, list active keys, and revoke them. These operations require a `full_access` key. The full key value is returned **once** on create.

```java
import africa.sendbyte.apikeys.ApiKey;
import africa.sendbyte.apikeys.KeyScope;
import africa.sendbyte.apikeys.KeyMode;

ApiKey key = client.apiKeys().create("Production backend", KeyScope.SEND_ONLY, KeyMode.LIVE);
System.out.println(key.getKey());   // sk_live_... — shown once, store it now

client.apiKeys().list().getData()
        .forEach(k -> System.out.printf("%s  %s  %s%n", k.getPrefix(), k.scope(), k.mode()));

// A key cannot revoke itself — use a separate full_access key.
client.apiKeys().revoke("key_...");
```

## Webhooks

### Manage endpoints

Register endpoints, inspect delivery history, and replay past deliveries. The signing secret is returned **once** on create — store it to verify incoming requests.

```java
import africa.sendbyte.webhooks.WebhookEndpoint;
import africa.sendbyte.webhooks.WebhookDelivery;
import java.util.Arrays;

// Subscribe to specific events (omit the list to receive all)
WebhookEndpoint endpoint = client.webhooks().create(
        "https://your-app.example.com/webhooks/sendbyte",
        Arrays.asList("email.delivered", "email.bounced"));
String secret = endpoint.getSecret();   // whsec_... — shown once, store it now

// Inspect and recover deliveries
client.webhooks().deliveries(endpoint.getId()).getData()
        .forEach(d -> System.out.println(d.getEventType() + " -> " + d.getStatusCode()));
WebhookDelivery replay = client.webhooks().replay("del_...");

// List and disable
client.webhooks().list().getData().forEach(e -> System.out.println(e.getUrl()));
client.webhooks().disable(endpoint.getId());
```

### Verify signatures

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
