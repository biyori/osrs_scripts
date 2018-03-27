import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

/**
 * Created by Kyle on 6/15/2017.
 */
class CollectBoners {
    private MethodProvider api;
    private int bonerCount;

    CollectBoners(MethodProvider mp) {
        this.api = mp;
    }

    void pickupBones() {
        GroundItem bones = api.groundItems.closest("Bones");
        if (bones != null) {
            bones.interact("Take");
            new ConditionalSleep(10_000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return !bones.exists();
                }
            }.sleep();
            if (!bones.exists())
                bonerCount++;
        }
    }

    int getBonerCount() {
        return bonerCount;
    }
}
