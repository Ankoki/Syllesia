package art.sylleth.syllesia.api.conversation;

import art.sylleth.syllesia.config.Placeholder;
import org.junit.jupiter.api.Test;

public class ConversationTest {

    @Test
    public void test() {
        Conversation conversation =
                new Conversation("Hello " + Placeholder.of(Placeholder.PLAYER_NAME) + "!")
                        .responseChoice(0, "Hi " + Placeholder.of(Placeholder.ENTITY_NAME) + "!")
                        .reply(0, "Welcome to the land of this that this that !! Enjoy your stay.")
                        .responseChoice(1, "What do you want?")
                        .responseChoice(2, "Where am I?")
                ;

        Conversation cnv = new Conversation()
                .initiate("Hello " + Placeholder.of(Placeholder.PLAYER_NAME) + "!")
                .addChoice("Hi..?")
                .addChoice("Who are you?")
                .addChoice("Where am I?")
                .respond(0, "")
                .addChoice("")
                .respond(1, "")
                .respond(2, "");

    }

}
