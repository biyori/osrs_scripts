package hayase.AIODraynorLumJack.src;

import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.MethodProvider;

import java.util.List;

/**
 * Created by Kyle on 2016/12/11.
 */
public class RandomTask extends Task {

    RandomTask(MethodProvider api) {
        super(api);
    }

    /**
     * RandomTask is responsible for replicating normal human behavior
     *
     * @return true if the player is in the working area, and the player is animating (chopping a tree)
     */
    @Override
    public boolean canProcess() {
        return Constants.draynorWorkingArea.contains(api.myPlayer()) && api.myPlayer().isAnimating();
    }

    @Override
    public void process() {

        /*
         * Switch statement to handle the different anti-ban cases
         */
        switch (random(0, 2468)) { //TODO: Allow user input to change the anti-ban frequency
            case 1:
                api.log("[Anti-Ban]: Examining nearby object");
                RS2Object examineThis = api.objects.closest(n -> n != null && n.exists() && n.isVisible() && !n.getName().equals("Oak") && !n.getName().equals("Tree") && n.hasAction("Examine"));
                if (examineThis != null) {
                    examineThis.hover();
                    afk(100, 200);
                    api.mouse.click(true);
                    afk(300, 500);
                    examineThis.interact("examine");
                }
                api.mouse.moveRandomly();
                break;
            case 2:
                api.log("[Anti-Ban]: Hovering WC XP");
                try {
                    api.tabs.getSkills().hoverSkill(Skill.WOODCUTTING);
                    afk(500, 1250);
                    api.getTabs().open(Tab.INVENTORY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                api.log("[Anti-Ban]: Examining Tree");
                RS2Object tree = api.objects.closest(Constants.getSelectedTree());
                if (tree != null) {
                    tree.hover();
                    afk(100, 200);
                    api.mouse.click(true);
                    afk(300, 500);
                    tree.interact("Examine");
                    api.mouse.moveRandomly();
                }
                break;
            case 5:
                api.log("[Anti-Ban]: Right clicking a local player");
                List<Player> localPlayer = api.players.getAll();
                localPlayer.remove(api.myPlayer());//can't examine our player nor should we
                if (localPlayer.size() > 0) {
                    localPlayer.get(0).hover();
                    afk(100, 200);
                    api.mouse.click(true);
                    afk(1500, 2000);
                    api.mouse.moveRandomly();
                }
                break;
            case 6:
                api.log("[Anti-Ban]: Move slightly");
                try {
                    api.mouse.moveSlightly(random(250, 2000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case 7:
                api.log("[Anti-Ban]: Move VERY slightly");
                api.mouse.moveVerySlightly();
                afk(50, 250);
                break;
            case 8:
                api.log("[Anti-Ban]: Move mouse out of screen");
                api.mouse.moveOutsideScreen();
                afk(1000, 10000);
                break;
            default:
                //TODO: Fix examining any object
//RS2Object examineThis2 = api.objects.closest(n -> n != null && n.exists() && n.isVisible()); /*&& !n.getName().equals("Oak") && !n.getName().equals("Tree")*/// && n.hasAction("Examine"));
//RS2Object examineDis = api.getObjects().closest(n -> n != null && n.isVisible() && n.interact("Examine"));
//   RS2Object examineDis2 = api.getObjects().closest(n -> n != null && n.isVisible() && n.hasAction("Examine"));
// if (examineDis2 != null)
//     api.log("Local object to examine: " + examineDis.getName());
//      List<RS2Object> examineThis3 = api.objects.getAll();
// examineThis3.removeIf(ob -> !ob.hasAction("Examine"));
/*     for (RS2Object obj : api.objects.getAll()) {
if (obj != null && obj.isVisible()) {
if (obj.interact("Examine"))
api.log("Object: " + obj.getName());
}
}*/
                break;
        }
    }

    /**
     * Function to simulate a player going AFK
     *
     * @param min minimum amount of time in milliseconds to afk
     * @param max maximum amount of time in milliseconds to afk
     */
    private void afk(int min, int max) {
        try {
            sleep(random(min, max));
        } catch (InterruptedException e) {
            api.log("Error in afk function: " + e.getMessage());
        }
    }
}