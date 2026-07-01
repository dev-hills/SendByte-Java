# Changelog

All notable changes to the SendByte Java SDK are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.0] - 2026-07-01

Initial release. Full coverage of the SendByte API with synchronous and
asynchronous (`CompletableFuture`) methods. Java 8+, depending only on OkHttp and
Jackson.

### Added

- **Client** — `SendByteClient` entry point (thread-safe, `AutoCloseable`) with a
  `SendByteClientOptions` builder for API key, base-URL override, timeouts, and
  User-Agent. Pluggable `HttpTransport` (OkHttp-backed by default) so the HTTP layer
  can be swapped or stubbed in tests.
- **Emails** (`client.emails()`) — `send`, `get`, and `list` with cursor pagination.
  Fluent `SendEmailRequest` builder covering recipients (to/cc/bcc), reply-to, custom
  headers, attachments, tags, scheduling, templates, and idempotency keys.
- **Domains** (`client.domains()`) — `create`, `get`, `list`, and `verify`, exposing
  the returned SPF/DKIM/DMARC DNS records and per-record verification checks.
- **Templates** (`client.templates()`) — `create`, `get`, `list`, `update`, `delete`,
  plus inline `render` and saved-template `preview` (Handlebars/MJML).
- **Webhooks** (`client.webhooks()`) — endpoint management (`create`, `list`,
  `disable`), delivery history (`deliveries`), and `replay`.
- **API keys** (`client.apiKeys()`) — `create`, `list`, and `revoke`, with typed
  `KeyScope` and `KeyMode` enums.
- **Webhook signature verification** — `WebhookSignature.verify(...)` implementing the
  `sendbyte-signature` HMAC-SHA256 scheme with a configurable replay-tolerance window
  and constant-time comparison.
- **Typed errors** — a `SendByteException` hierarchy mapped from the API's
  machine-readable error codes (validation, authentication, authorization,
  domain-not-verified, suppressed-recipient, not-found, idempotency-conflict,
  rate-limit, internal), each exposing `code`, `status`, `docsUrl`, and `requestId`.
  `RateLimitException` additionally exposes the `Retry-After` value.
- **Continuous integration** — GitHub Actions workflow running the test suite on
  JDK 8, 11, and 17.

[Unreleased]: https://github.com/dev-hills/SendByte-Java/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/dev-hills/SendByte-Java/releases/tag/v0.1.0
