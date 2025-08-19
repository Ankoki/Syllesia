package art.sylleth.syllesia.listeners;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.api.Defaults;
import art.sylleth.syllesia.api.conversation.Conversation;
import art.sylleth.syllesia.api.events.PlayerInteractEvent;
import art.sylleth.syllesia.api.events.PlayerJoinEvent;
import art.sylleth.syllesia.api.quest.Quest;
import art.sylleth.syllesia.api.world.Location;
import art.sylleth.syllesia.entities.Player;
import art.sylleth.syllesia.event.EventManager;
import art.sylleth.syllesia.event.Listener;
import art.sylleth.syllesia.misc.Timespan;
import art.sylleth.syllesia.platform.textures.Texture;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameListener implements EventManager {

    private final Map<int[], Integer> goldCounter = new HashMap<>();
    private final Random random = new Random();

    @Listener
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getUserdata().getQuest(Defaults.Quest.HUMBLE_BEGINNINGS) == null && !player.getUserdata().hasCompletedQuest(Defaults.Quest.HUMBLE_BEGINNINGS)) {
            Conversation conversation = Syllesia.getInstance().getConversationHandler().getConversation(Defaults.Conversation.FIRST_JOIN);
            player.openDialogue(conversation, conversation.getStartDialogue());
        }
    }

    @Listener
    private void humbleBeginningsQuest(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Location location = event.getLocation();
        if (location.getTexture() == Texture.GOLD_BLOCK) {
            player.addCoins(1);
            player.sendTitle("You have received a gold coin.", Timespan.of("1 second"));
            synchronized (goldCounter) {
                int[] coords = new int[]{(int) location.getX(), (int) location.getY()};
                int hit = 0;
                for (Map.Entry<int[], Integer> entry : this.goldCounter.entrySet()) {
                    if (entry.getKey()[0] == coords[0] && entry.getKey()[1] == coords[1]) {
                        coords = entry.getKey();
                        hit = entry.getValue() + 1;
                    }
                }
                if (hit >= random.nextInt(8, 12)) {
                    this.goldCounter.remove(coords);
                    location.getMap().getBlockAt(location).setTexture(Texture.DEPLETED_GOLD_BLOCK);
                } else
                    this.goldCounter.put(coords, hit);
            }
            Quest quest = player.getUserdata().getQuest(Defaults.Quest.HUMBLE_BEGINNINGS);
            if (quest != null)
                if (quest.checkCompletion(player))
                    player.getUserdata().completeQuest(quest);
        }
    }

}
