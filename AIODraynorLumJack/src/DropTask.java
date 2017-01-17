package AIODraynorLumJack.src;

import org.osbot.rs07.script.MethodProvider;

/**
 * Created by Kyle on 2016/12/28.
 */
public class DropTask extends Task {

    DropTask(MethodProvider api) {
        super(api);
    }

    /**
     * DropTask activates when the players inventory is full
     *
     * @return true if the inventory is full
     */
    @Override
    public boolean canProcess() {
        return api.inventory.isFull();
    }

    @Override
    public void process() {
        api.log("Inventory is full. Dropping logs now!");

        /*
         * Drop everything except the necessary woodcut axes
         * TODO: Implement different drop patterns
         */
        api.getInventory().dropAllExcept(Constants.woodAxes);
    }
}