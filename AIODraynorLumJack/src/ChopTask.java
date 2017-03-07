package AIODraynorLumJack.src;

/**
 * Created by Kyle on 2016/12/11.
 */

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.Condition;
import org.osbot.rs07.utility.ConditionalSleep;

import java.security.SecureRandom;
import java.util.List;

public class ChopTask extends Task {

    ChopTask(MethodProvider api) {
        super(api);
    }

    @Override
    public boolean canProcess() {
        return !api.myPlayer().isAnimating() && Constants.draynorWorkingArea.contains(api.myPlayer()) && !api.inventory.isFull();
    }

    @Override
    public void process() {

        /*
         * Get the closest tree in our working area
         */
        RS2Object tree = api.objects.closest(Constants.draynorWorkingArea, Constants.getSelectedTree());

        /*
         * Check if the tree exists and continue if so
         */
        if (tree != null) {

            /*
             * If the tree is visible, continue
             */
            if (tree.isVisible()) {

                /*
                 * Work around for the quest tree (id 2409) in Lumbridge that cannot be chopped
                 */
                if (tree.getId() == 2409) {
                    api.log("Found fake tree! Finding a new tree...");
                    tree = api.objects.closest(t -> t != null && t.getName().equals("Tree") && t.getId() != 2409 && Constants.draynorWorkingArea.contains(t));
                }

                /*
                 * If the level up widget is visible, interact with it
                 */
                if (api.widgets.isVisible(233)) {
                    if (!api.widgets.containingText(233, "Click here to continue").isEmpty()) {
                        api.widgets.containingText(233, "Click here to continue").get(0).interact();
                    }
                }

                if (tree.interact("Chop down")) {

                    /*
                     * Console aesthetics
                     */
                    api.log("Chopping" + (Constants.getSelectedTree().equals("Tree") ? "" : " " + Constants.getSelectedTree()) + " tree");

                    /*
                     * After interacting with a tree sleep for 10 seconds or until we are chopping, under attack, or a level up widget has appeared
                     */
                    boolean treeExists = tree.exists();

                    new ConditionalSleep(10_000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return api.myPlayer().isAnimating() || api.myPlayer().isUnderAttack() || api.widgets.isVisible(233) || !treeExists;
                        }
                    }.sleep();

                    /*
                     * The last block of the chop method checks if the world hop constraints are met and if so--to hop
                     */
                    //api.log("There are currently " + playersInArea(api.myPlayer().getArea(5)) + "/" + api.players.getAll().size() + " players in our radius of 5.");
                    shouldWeHop(playersInArea(api.myPlayer().getArea(5)), Constants.getHopPlayerLim(), Constants.getHopTimeMin());
                }
            } else {

                /*
                 * Occasionally if a tree is nearby (isVisible) but too far away to click the camera will bug out.
                 * This is a hotfix to handle the bug
                 */
                if (!api.myPlayer().getArea(5).contains(tree)) {
                    api.log("Walking closer to tree");
                    WalkingEvent webEvent = new WalkingEvent(tree);
                    webEvent.setEnergyThreshold(5);
                    RS2Object finalTree = tree;
                    webEvent.setBreakCondition(new Condition() {
                        @Override
                        public boolean evaluate() {
                            return finalTree.isVisible() && api.myPlayer().getArea(5).contains(finalTree);
                        }
                    });
                    api.execute(webEvent);
                } else {

                    /*
                     * If the tree is not visible, adjust camera to compensate
                     */
                    if (api.camera.toEntity(tree)) {
                        api.log("Adjusting camera to tree");

                        /*
                         * If the camera just fails to find our tree, just walk to it
                         */
                        if (!tree.isVisible()) {
                            api.log("Couldn't adjust camera properly. Walking to tree");
                            api.execute(new WalkingEvent(tree));
                        }
                    }
                }
            }
        } else {

            /*
             * The script cannot find any trees in our current area--this walks to known locations with those tree types
             */
            walkToKnownLocation();
        }

        /*
         * If the character is under attack while chopping trees--run away safely
         */
        if (api.myPlayer().isUnderAttack()) {
            api.log("Under attack! Running to a safe spot...");
            runAwayToSafeSpot();
        }
    }

    /*
     * WebWalk to known areas for all tree types--this is a backup if there are no specific trees in our current area
     */
    private void walkToKnownLocation() {
        if (!Constants.getAreaForTree().contains(api.myPlayer())) {
            WebWalkEvent webEvent = new WebWalkEvent(Constants.getAreaForTree());
            webEvent.useSimplePath();
            webEvent.setEnergyThreshold(5);
            api.log("Unable to detect any nearby" + (Constants.getSelectedTree().equals("Tree") ? "" : " " + Constants.getSelectedTree()) + " trees! Walking to a known location...");
            api.execute(webEvent);
        }
    }

    /*
     * Run away method
     *
     * Run to a random spot within the tree areas to avoid getting too far away from our trees
     */
    private void runAwayToSafeSpot() {
        if (api.myPlayer().isUnderAttack()) {
            WebWalkEvent webEvent = new WebWalkEvent(Constants.getAreaForTree().getRandomPosition());
            webEvent.setEnergyThreshold(1);
            api.execute(webEvent);
        }
    }

    /**
     * A simple hop conditional that checks if the current players in our area meets the limit
     * If the condition meets the limit, check if the script is able to perform a hop--as to avoid hopping every login
     *
     * @param players        the amount of players currently in our area
     * @param playerLim      the amount of players needed to perform a hop
     * @param minutesEachHop In minutes how often should the bot be able to hop
     */
    private void shouldWeHop(int players, int playerLim, int minutesEachHop) {

        /*
         * Param filtering from the GUI. If the user enters 0, do not hop
         */
        if (playerLim != 0 || minutesEachHop != 0) {

            /*
             * If the players meets the limit, continue the hop
             */
            if (players >= playerLim) {
                hopFreeWorlds(minutesEachHop);
            }
        }
    }

    /**
     * This private function checks the last hop timestamp and compares it to the upcoming timestamp
     * and if they meet--proceed to hop
     *
     * @param minutesEachHop In minutes how often should the bot be able to hop
     */
    private void hopFreeWorlds(int minutesEachHop) {
        if ((System.currentTimeMillis() - Constants.lastHopTime()) / 1000 > Constants.getNextHop()) {
            api.log("Our area is being overrun... Hopping to a new world!");

            /*
             * Create an array of the available F2P world list
             * TODO: Detect if a player is P2P, then hop to P2P worlds instead
             */
            int[] worlds = {301, 308, 316, 326, 335, 382, 383, 384, 393, 394};

            /*
             * Grab the current world to make sure we do not attempt to hop to it
             */
            int currentWorld = api.worlds.getCurrentWorld();

            /*
             * Initialize a new secure random object
             */
            SecureRandom secure = new SecureRandom();

            /*
             * Setup the limits
             */
            int minimum = 0, maximum = worlds.length - 1;
            int range = maximum - minimum + 1;

            /*
             * Generate a SR number with respect to our array length
             */
            int randomWorld = secure.nextInt(range) + minimum;

            /*
             * While the current world is equal to our new world, keep working
             */
            while (currentWorld == worlds[randomWorld]) {
                randomWorld = secure.nextInt(range) + minimum;
            }

            /*
             * Finally we have a world to hop to--let's hop!
             */
            if (api.worlds.hop(worlds[randomWorld])) {

                /*
                 * Hopping now, sleep until the client detects our axe
                 *
                 * Occasionally after hopping worlds, we lose information about what is inside our inventory and equipment--the client needs to finish loading
                 */
                new ConditionalSleep(10_000) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return !(api.client.getLoginStateValue() == 45 || api.client.getLoginStateValue() == 25);
                    }
                }.sleep();

                /*
                 * Save the current time as the last hop timestamp
                 */
                Constants.lastHopTime(System.currentTimeMillis());

                /*
                 * Calculating the next hop:
                 * Converts the input into seconds per minute and start a minimum hop of random(1, x) minutes
                 */
                Constants.nextHopTimer(random(60, 60 * minutesEachHop));
                api.log("Next hop will be available in " + Constants.getNextHop() / 60 + " minutes.");
            } else {
                api.log("Failed to hop! Trying again soon...");
            }
        }
    }

    /**
     * A simple way to calculate players in an area
     *
     * @param area the area that the players will be calculated
     * @return integer total players in the specific area
     */
    private int playersInArea(Area area) {
        int playersInArea = 0;
        List<Player> nearbyPlayers = api.players.getAll();
        //Remove our player from the list
        nearbyPlayers.remove(api.myPlayer());
        for (Player player : nearbyPlayers) {
            if (area.contains(player)) {
                playersInArea++;
            }
        }
        return playersInArea;
    }
}
