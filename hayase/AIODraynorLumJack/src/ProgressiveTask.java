package hayase.AIODraynorLumJack.src;

import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;


/**
 * Created by Kyle on 2016/12/29.
 */
public class ProgressiveTask extends Task {

    ProgressiveTask(MethodProvider api) {
        super(api);
    }

    /**
     * ProgressiveTask is responsible for changing the current players tree type to the highest grade tree that
     * it can currently chop
     *
     * @return if the player is currently chopping and the inventory is not full
     */
    @Override
    public boolean canProcess() {
        return api.myPlayer().isAnimating() && !api.inventory.isFull();
    }

    @Override
    public void process() {

        /*
         * If the current tree does not equal the players treeLimits()--change the tree to meet the limit
         */
        if (!Constants.getSelectedTree().equals(treeLimits())) {

            /*
             * If skipping willow trees is false, continue
             */
            if (!Constants.skipWillowTrees()) {
                api.log("It's time to chop" + (treeLimits().equals("Tree") ? "" : " " + treeLimits()) + " trees now!");
                Constants.setSelectedTree(treeLimits());
                Constants.setTotalLogs(0);
            } else {

                /*
                 * If skipping willow trees is true and the players treeLimit() does not equal a willow tree--change the limit
                 */
                if (!treeLimits().equals("Willow")) {
                    api.log("It's time to chop" + (treeLimits().equals("Tree") ? "" : " " + treeLimits()) + " trees now!");
                    Constants.setSelectedTree(treeLimits());
                    Constants.setTotalLogs(0);
                }
            }
        }
    }

    /**
     * Function to calculate a players tree limit or the highest grade tree that can currently be chopped
     *
     * @return string tree type
     */
    private String treeLimits() {
        int level = api.skills.getStatic(Skill.WOODCUTTING);
        if (level < 15)
            return "Tree";
        else if (level < 30 && level >= 15)
            return "Oak";
        else if (level < 60 && level >= 30)
            return "Willow";
        else
            return "Yew";
    }
}