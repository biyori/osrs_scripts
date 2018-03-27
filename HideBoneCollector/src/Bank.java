import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

/**
 * Created by Kyle on 6/15/2017.
 */
class Bank {
    private MethodProvider api;

    Bank(MethodProvider mp) {
        this.api = mp;
    }

    void walkToBank() throws InterruptedException {
        NPC banker = api.npcs.closest("Banker");
        if (banker != null) {
            api.bank.open();
            new ConditionalSleep(10_000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return api.bank.isOpen();
                }
            }.sleep();

            if (api.bank.isOpen()) {
                api.bank.depositAll();
                new ConditionalSleep(10_000) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return api.inventory.isEmpty();
                    }
                }.sleep();

                if (api.inventory.isEmpty()) {
                    api.bank.close();
                }
            }

        } else {
            WebWalkEvent webEvent = new WebWalkEvent(Banks.LUMBRIDGE_UPPER);
            webEvent.useSimplePath();
            api.execute(webEvent);
        }
    }
}
