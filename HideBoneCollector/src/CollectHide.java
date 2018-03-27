import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

/**
 * Created by Kyle on 6/15/2017.
 */
class CollectHide {
    private MethodProvider api;
    private int hideCount;

    CollectHide(MethodProvider mp) {
        this.api = mp;
    }

    void collectHides() {
        GroundItem hide = api.groundItems.closest("Cowhide");
        if (hide != null) {
            hide.interact("Take");
            new ConditionalSleep(10_000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return !hide.exists();
                }
            }.sleep();
            if (!hide.exists())
                hideCount++;
        }
    }

    int getHideCount() {
        return hideCount;
    }
}
