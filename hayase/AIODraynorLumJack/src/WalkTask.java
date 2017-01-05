package hayase.AIODraynorLumJack.src;

import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.Condition;

/**
 * Created by Kyle on 2016/12/12.
 */
public class WalkTask extends Task {

    WalkTask(MethodProvider api) {
        super(api);
    }

    /**
     * WalkTask is responsible for making sure that the player stays within the working area
     *
     * @return if the player is not at the working area, inventory is not full, and there is no need to shop at the GE
     */
    @Override
    public boolean canProcess() {
        return !Constants.draynorWorkingArea.contains(api.myPlayer()) && !api.inventory.isFull() && !Constants.needToBuyAxes();
    }

    @Override
    public void process() {
        api.log("Walking to" + (Constants.getSelectedTree().equals("Tree") ? "" : " " + Constants.getSelectedTree()) + " trees");

        /*
         * WebWalkEvent to tree area
         */
        WebWalkEvent webEvent = new WebWalkEvent(Constants.getAreaForTree());
        webEvent.setEnergyThreshold(5);
        webEvent.useSimplePath();

        /*
         * Stop walking after we reach the working area
         */
        webEvent.setBreakCondition(new Condition() {
            @Override
            public boolean evaluate() {
                return Constants.draynorWorkingArea.contains(api.myPlayer());
            }
        });
        api.execute(webEvent);
    }
}