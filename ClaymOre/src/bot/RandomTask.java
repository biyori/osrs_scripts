package bot;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.MethodProvider;

import java.security.SecureRandom;
import java.util.List;

/**
 * Created by Kyle on 2016/12/11.
 */
public class RandomTask extends Task {
    private Area dwarvenMines = new Area(3025, 9807, 3033, 9831);//dwarven mine area with iron + clay prospects
    private Area DM_westClay = new Area(3027, 9807, 3031, 9811);
    private Area DM_eastClay = new Area(3052, 9817, 3054, 9820);
    private Area DM_westIron = new Area(3031, 9825, 3033, 9827);
    private Area DM_eastIron = new Area(3052, 9821, 3054, 9828);
    private SecureRandom antiban = new SecureRandom();

    RandomTask(MethodProvider api) {
        super(api);
    }

    @Override
    public boolean canProcess() {
        return getArea(Global.guiSelectOre()).contains(api.myPlayer());
    }

    @Override
    public void process() {

        /*
         * Setup the range for the switch statement using SecureRandom()
         */
        int min = 0, max = 200 - 1;
        int random = antiban.nextInt(max - min + 1) + min;
        api.log("Anti Ban is running!");

        /*
         * Switch statement to handle the different anti-ban cases
         */
        //  switch (random(0, 2468)) { //TODO: Allow user input to change the anti-ban frequency
        switch (random) {
            case 1:
                api.log("[Anti-Ban]: Examining random object");
                /*
                 * Create a list for editing
                 */
                List<RS2Object> examineAbles = api.objects.getAll();

                /*
                 * This isn't very efficient but it's not called that often that I do not believe this is a performance burden.
                 *
                 * Loop through another object set and remove the objects we do not want to mess with
                 */
                for (RS2Object obj : api.objects.getAll()) {
                    if (obj == null || !obj.isVisible() || obj.getName().equals("null")) {
                        examineAbles.remove(obj);
                    }
                }

                /*
                 * If our list still had contents left, we definitely can work with it
                 */
                if (examineAbles.size() > 0) {
                    /*
                     * Using another SecureRandom object to try and be more unpredictable than just random();
                     */
                    SecureRandom secure = new SecureRandom();

                    /*
                     * Easy min, max initialization to make it more readable
                     */
                    int minimum = 0, maximum = examineAbles.size() - 1;
                    int range = maximum - minimum + 1;
                    int randomIndex = secure.nextInt(range) + minimum;

                    /*
                     * Hover over the object then right click it, while the menu is open click examine
                     */
                    examineAbles.get(randomIndex).hover();
                    afk(111, 555);
                    api.mouse.click(true);
                    afk(555, 2222);
                    if (api.menu.isOpen()) {
                        api.menu.selectAction("Examine");
                    }
                }
                break;
            case 2:
                api.log("[Anti-Ban]: Hovering Mining EXP");
                try {
                    api.tabs.getSkills().hoverSkill(Skill.MINING);
                    afk(500, 1250);
                    api.getTabs().open(Tab.INVENTORY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                api.log("[Anti-Ban]: Examining Tree");
                RS2Object rock = api.objects.closest("Rocks");
                if (rock != null) {
                    rock.hover();
                    afk(111, 555);
                    api.mouse.click(true);
                    afk(555, 2222);
                    if (api.menu.isOpen()) {
                        api.menu.selectAction("Examine");
                    }
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
                }
                break;
            case 8:
                api.log("[Anti-Ban]: Move mouse out of screen");
                api.mouse.moveOutsideScreen();
                afk(1000, 10000);
                break;
            case 9:
                api.log("[Anti-Ban]: Randomly moving camera");
                int currPitch = api.camera.getPitchAngle();
                int currYaw = api.camera.getYawAngle();
                api.camera.movePitch(currPitch + random(1, 20));
                api.camera.moveYaw(currYaw + random(1, 50));
            default:
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

    private Area getArea(String area) {
        switch (area) {
            case "Clay (west)":
                return DM_westClay;
            case "Clay (east)":
                return DM_eastClay;
            case "Iron (west)":
                return DM_westIron;
            case "Iron (east)":
                return DM_eastIron;
            default:
                return dwarvenMines;
        }
    }
}