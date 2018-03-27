import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.script.MethodProvider;

import static org.osbot.rs07.script.MethodProvider.random;

/**
 * Created by Kyle on 6/15/2017.
 */
class WalkToCows {
    private MethodProvider api;
    private Area cowArea1 = new Area(3255, 3261, 3259, 3294);
    private Area cowArea2 = new Area(3195, 3285, 3203, 3295);

    WalkToCows(MethodProvider mp) {
        this.api = mp;
    }

    void walkToCows() throws InterruptedException {
        NPC cows = api.npcs.closest("Cow");
        if (cows == null) {
            int cowSpot = random(1, 2);
            if (cowSpot == 1) {
                WebWalkEvent webEvent = new WebWalkEvent(cowArea1.getRandomPosition());
                webEvent.useSimplePath();
                api.execute(webEvent);
            } else {
                WebWalkEvent webEvent = new WebWalkEvent(cowArea2.getRandomPosition());
                webEvent.useSimplePath();
                api.execute(webEvent);
            }
        }
    }

    boolean cowsNearby() {
        NPC cow = api.npcs.closest("Cow");
        if (cow != null) {
            if (cow.getPosition().distance(api.myPosition()) < 10 || cow.isVisible())
                return true;
        }
        return false;
    }
}
