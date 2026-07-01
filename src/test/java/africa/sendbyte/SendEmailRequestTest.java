package africa.sendbyte;

import africa.sendbyte.emails.SendEmailRequest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SendEmailRequestTest {

    @Test
    void requiresFrom() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> SendEmailRequest.builder().to("a@b.ng").html("<p>x</p>").build());
        assertEquals("'from' is required.", ex.getMessage());
    }

    @Test
    void requiresAtLeastOneRecipient() {
        assertThrows(IllegalArgumentException.class,
                () -> SendEmailRequest.builder().from("a@b.ng").html("<p>x</p>").build());
    }

    @Test
    void requiresBodyOrTemplate() {
        assertThrows(IllegalArgumentException.class,
                () -> SendEmailRequest.builder().from("a@b.ng").to("c@d.ng").build());
    }

    @Test
    void templateAloneSatisfiesBodyRequirement() {
        SendEmailRequest req = SendEmailRequest.builder()
                .from("a@b.ng").to("c@d.ng").templateId("welcome").variable("name", "Amaka").build();
        assertEquals("welcome", req.getTemplateId());
    }

    @Test
    void rejectsMoreThanFiftyRecipients() {
        List<String> many = new ArrayList<>();
        for (int i = 0; i < 51; i++) {
            many.add("user" + i + "@example.ng");
        }
        assertThrows(IllegalArgumentException.class,
                () -> SendEmailRequest.builder().from("a@b.ng").to(many).html("<p>x</p>").build());
    }
}
