package africa.sendbyte.templates;

import africa.sendbyte.http.RequestExecutor;
import africa.sendbyte.http.SendByteHttpRequest;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * The Templates resource: manage reusable server-side Handlebars/MJML templates and
 * preview their rendered output.
 *
 * <p>Obtain an instance via {@code client.templates()}. Every method has a blocking
 * form and an {@code ...Async} form returning a {@link CompletableFuture}. Send with a
 * saved template by passing its id or name as {@code templateId} on a send request.
 */
public final class Templates {

    private final RequestExecutor executor;

    public Templates(RequestExecutor executor) {
        this.executor = executor;
    }

    /** Create a new template. Invalid Handlebars/MJML is rejected at save time. */
    public Template create(TemplateRequest request) {
        return executor.execute(buildCreateRequest(request), Template.class);
    }

    /** Asynchronous {@link #create(TemplateRequest)}. */
    public CompletableFuture<Template> createAsync(TemplateRequest request) {
        return executor.executeAsync(buildCreateRequest(request), Template.class);
    }

    /** Retrieve a template by id, including its stored HTML and MJML source. */
    public Template get(String id) {
        return executor.execute(buildGetRequest(id), Template.class);
    }

    /** Asynchronous {@link #get(String)}. */
    public CompletableFuture<Template> getAsync(String id) {
        return executor.executeAsync(buildGetRequest(id), Template.class);
    }

    /** List all templates in the project. */
    public TemplateList list() {
        return executor.execute(buildListRequest(), TemplateList.class);
    }

    /** Asynchronous {@link #list()}. */
    public CompletableFuture<TemplateList> listAsync() {
        return executor.executeAsync(buildListRequest(), TemplateList.class);
    }

    /** Replace a template's definition, bumping its version. Already-sent emails are unaffected. */
    public Template update(String id, TemplateRequest request) {
        return executor.execute(buildUpdateRequest(id, request), Template.class);
    }

    /** Asynchronous {@link #update(String, TemplateRequest)}. */
    public CompletableFuture<Template> updateAsync(String id, TemplateRequest request) {
        return executor.executeAsync(buildUpdateRequest(id, request), Template.class);
    }

    /** Permanently delete a template. Future sends referencing it will fail with {@code not_found}. */
    public void delete(String id) {
        executor.execute(buildDeleteRequest(id), Void.class);
    }

    /** Asynchronous {@link #delete(String)}. */
    public CompletableFuture<Void> deleteAsync(String id) {
        return executor.executeAsync(buildDeleteRequest(id), Void.class);
    }

    /** Render a template body with variables inline, without saving anything. */
    public RenderedTemplate render(RenderRequest request) {
        return executor.execute(buildRenderRequest(request), RenderedTemplate.class);
    }

    /** Asynchronous {@link #render(RenderRequest)}. */
    public CompletableFuture<RenderedTemplate> renderAsync(RenderRequest request) {
        return executor.executeAsync(buildRenderRequest(request), RenderedTemplate.class);
    }

    /** Render a previously saved template against a set of variables, without sending. */
    public RenderedTemplate preview(String id, Map<String, Object> variables) {
        return executor.execute(buildPreviewRequest(id, variables), RenderedTemplate.class);
    }

    /** Asynchronous {@link #preview(String, Map)}. */
    public CompletableFuture<RenderedTemplate> previewAsync(String id, Map<String, Object> variables) {
        return executor.executeAsync(buildPreviewRequest(id, variables), RenderedTemplate.class);
    }

    private SendByteHttpRequest buildCreateRequest(TemplateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null.");
        }
        return SendByteHttpRequest.builder("POST", "/templates").body(request).build();
    }

    private SendByteHttpRequest buildGetRequest(String id) {
        return SendByteHttpRequest.builder("GET", "/templates/" + requireId(id)).build();
    }

    private SendByteHttpRequest buildListRequest() {
        return SendByteHttpRequest.builder("GET", "/templates").build();
    }

    private SendByteHttpRequest buildUpdateRequest(String id, TemplateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null.");
        }
        return SendByteHttpRequest.builder("PUT", "/templates/" + requireId(id)).body(request).build();
    }

    private SendByteHttpRequest buildDeleteRequest(String id) {
        return SendByteHttpRequest.builder("DELETE", "/templates/" + requireId(id)).build();
    }

    private SendByteHttpRequest buildRenderRequest(RenderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null.");
        }
        return SendByteHttpRequest.builder("POST", "/templates/render").body(request).build();
    }

    private SendByteHttpRequest buildPreviewRequest(String id, Map<String, Object> variables) {
        return SendByteHttpRequest.builder("POST", "/templates/" + requireId(id) + "/preview")
                .body(new PreviewRequest(variables))
                .build();
    }

    private static String requireId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("template id must not be null or empty.");
        }
        return id;
    }
}
