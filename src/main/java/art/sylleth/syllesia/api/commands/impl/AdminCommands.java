package art.sylleth.syllesia.api.commands.impl;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.api.commands.CommandHook;
import art.sylleth.syllesia.api.configs.Placeholder;
import art.sylleth.syllesia.api.conversation.Conversation;
import art.sylleth.syllesia.api.conversation.Dialogue;
import art.sylleth.syllesia.api.world.Block;
import art.sylleth.syllesia.entities.Player;
import art.sylleth.syllesia.misc.Timespan;
import art.sylleth.syllesia.platform.textures.Texture;

/**
 * Admin commands used for debugging during testing.
 */
public class AdminCommands {

    @CommandHook(name = "setTarget")
    public void setTarget(Player player, Texture texture) {
        Block block = player.getTargetBlock();
        block.setTexture(texture);
    }

    @CommandHook(name = "setCoin")
    public void setCoin(Player player, Double amount) {
        player.setCoins(amount);
        player.sendTitle("You have set your coin count to\n" + amount + " coins.", Timespan.of("2 seconds"));
    }

    @CommandHook(name = "giveCoin")
    public void giveCoin(Player player, Double amount) {
        player.addCoins(amount);
        player.sendTitle("You have given yourself\n" + amount + " coins.", Timespan.of("2 seconds"));
    }

    @CommandHook(name = "dockCoin")
    public void removeCoin(Player player, Double amount) {
        player.removeCoins(amount);
        player.sendTitle("You have docked yourself\n" + amount + " coins.", Timespan.of("2 seconds"));
    }

    @CommandHook(name = "setupConversation")
    public void setupConversation() {
        Syllesia.getInstance().getConversationHandler().registerConversation(
                new Conversation("TEST_CONVERSATION",
                        new Dialogue("TEST_GREETING")
                                .setTitle("Greetings")
                                .setContent("Hello " + Placeholder.of(Placeholder.PLAYER_NAME) + ", how are you doing? I'm feeling very good and super ridiculously cool in my humble opinion, i'm just adding text to test if my overflow works.")
                                .addChoice("Where am I?", () -> "TEST_LOCATION")
                                .addChoice("Who are you?", () -> "TEST_NAME_RESPONSE")
                                .addChoice("I don't have time for this actually.", () -> Dialogue.EXIT_POINTER)
                                .validate(),
                        new Dialogue("TEST_LOCATION")
                                .setTitle("Greetings")
                                .setContent("You are in the Syllesia ruins... it got bad around here.")
                                .addChoice("Oh... ok bye.", () -> Dialogue.EXIT_POINTER)
                                .validate(),
                        new Dialogue("TEST_NAME_RESPONSE")
                                .setTitle("Greetings")
                                .setContent("I'm the voices you hear in your head.")
                                .addChoice("Cool!!! Always wanted to meet you, ciao.", () -> Dialogue.EXIT_POINTER)
                                .validate()));
    }

    @CommandHook(name = "startConversation")
    public void startConversation(Player player) {
        Conversation conversation = Syllesia.getInstance().getConversationHandler().getConversation("TEST_CONVERSATION");
        if (conversation == null) {
            player.sendTitle("Run /setupConversation", Timespan.of("2 seconds"));
            return;
        }
        player.openDialogue(conversation, conversation.getStartDialogue());
    }

    @CommandHook(name = "stopConversation")
    public void stopConversation(Player player) {
        player.openDialogue(null, null);
    }

}
