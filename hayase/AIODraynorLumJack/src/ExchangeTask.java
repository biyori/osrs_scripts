package hayase.AIODraynorLumJack.src;

import org.osbot.rs07.api.GrandExchange;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

/**
 * Created by Kyle on 2017/01/01.
 */
public class ExchangeTask extends Task {
    private final BankClass bonk;

    ExchangeTask(MethodProvider api) {
        super(api);
        bonk = new BankClass(api);
    }

    /**
     * ExchangeTask is for using the Grand Exchange to sell logs and purchase axe upgrades
     *
     * @return true if the bank has no axe upgrades, the player is using progressive mode,
     * and sell logs with GE is enabled
     */
    @Override
    public boolean canProcess() {
        return Constants.needToBuyAxes() && Constants.sellLogsAtGE() && Constants.progressiveMode();
    }

    @Override
    public void process() {
        api.log("Time to use the Grand Exchange!");

        /*
         * If the player is not at the Grand Exchange--walk to the GE
         */
        if (!Banks.GRAND_EXCHANGE.contains(api.myPlayer())) {
            api.log("Walking to GE");
            WebWalkEvent webEvent = new WebWalkEvent(Banks.GRAND_EXCHANGE);
            webEvent.setEnergyThreshold(5);
            webEvent.useSimplePath();
            api.execute(webEvent);
        }

        /*
         * Once at the GE, store the NPCs in relative objects
         */
        NPC banker = api.npcs.closest(Banks.GRAND_EXCHANGE, "Banker");
        NPC exchange = api.npcs.closest(Banks.GRAND_EXCHANGE, "Grand Exchange Clerk");

        /*
         * No logs in inventory, open bank and take out logs in noted form
         */
        if (!api.inventory.contains("Logs")) {
            if (banker != null) {
                if (banker.isVisible()) {
                    if (!api.bank.isOpen()) {
                        bonk.bankWithBanker(banker, "Bank");
                        bonk.getLogs();
                    } else {
                        api.log("Bank is open");
                        bonk.bankWithBanker(banker, "Bank");
                        bonk.getLogs();
                    }
                } else {
                    api.camera.toEntity(banker);
                }
            }
        }

        if (exchange != null) {
            if (exchange.isVisible()) {

                /*
                 * If the exchange window is not open, interact with the NPC to open it up
                 */
                if (!api.grandExchange.isOpen()) {
                    api.log("Attempting to open Grand Exchange");
                    if (exchange.interact("Exchange")) {
                        new ConditionalSleep(5_000) {
                            @Override
                            public boolean condition() throws InterruptedException {
                                return api.grandExchange.isOpen();
                            }
                        }.sleep();
                    }

                    /*
                     * Grand Exchange is open, continue
                     */
                    if (api.grandExchange.isOpen()) {

                        /*
                         * Detect if there is coins in the players inventory
                         *
                         * If there is coins--we do not need to sell more logs
                         */
                        Item geepees = api.inventory.getItem("Coins");

                        /*
                         * There is no coins in our inventory so we must sell logs
                         */
                        if (geepees == null) {
                            api.log("Ready to sell some logs");

                            /*
                             * If we have the quest points to sell oak logs, sell them first
                             */
                            sellItem("Oak logs", 1522, 1, 100);

                            /*
                             * Sell logs
                             */
                            sellItem("Logs", 1512, 1, 100);
                        }

                        /*
                         * We have coins in our inventory, continue to purchase axes
                         */
                        if (geepees != null) {

                            /*
                             * For whatever reason if our logs didn't sell like they should have
                             */
                            if (api.inventory.getItem(995).getAmount() > 70) {

                                /*
                                 * Purchase all axes up to Adamant (excluding black axe because it's not cost efficient)
                                 */
                                buyItem("Steel axe", 1353, 200, 1);
                                buyItem("Mithril axe", 1355, 300, 1);
                                buyItem("Adamant axe", 1357, 1500, 1);

                                /*
                                 * We just bought axes
                                 */
                                Constants.needToBuyAxes(false);

                            } else {
                                /*
                                 * Realistically this should NEVER happen, but in case it ever does
                                 * downgrade purchase to a single steel axe
                                 */
                                api.log("Not enough money to purchase axes! Downgrading options...");
                                buyItem("Steel axe", 1353, 70, 1);

                                /*
                                 * We just bought axes
                                 */
                                Constants.needToBuyAxes(false);
                            }
                        }
                        api.grandExchange.close();
                    }
                }
            } else {
                api.camera.toEntity(exchange);
            }
        }

        /*
         * After purchasing axes deposit everything and get back to work
         */
        if (banker != null && !Constants.needToBuyAxes()) {
            if (banker.isVisible()) {
                if (!api.bank.isOpen()) {
                    bonk.bankWithBanker(banker, "Bank");
                    bonk.depositAll();
                } else {
                    api.log("Bank is open");
                    bonk.bankWithBanker(banker, "Bank");
                    bonk.depositAll();
                }
            } else {
                api.camera.toEntity(banker);
            }
        }
    }

    /**
     * Function sellItem is for handling sales at the Grand Exchange.
     * Handles null checks and all Grand Exchange box slots
     *
     * @param item   the item to sell
     * @param id     the item id of the item
     * @param price  sell the item for this much
     * @param amount the quantity to sell
     */
    private void sellItem(String item, int id, int price, int amount) {

        /*
         * If our inventory has the item, continue
         */
        if (api.inventory.contains(item)) {
            GrandExchange.Box[] boxIndexAt = GrandExchange.Box.values();
            int index = -1;

            /*
             * Loop through all the exchange box slots
             */
            for (GrandExchange.Box boxSlot : boxIndexAt) {
                //start loop with zero
                index++;

                /*
                 * Loop until selling an item returns true
                 */
                if (api.grandExchange.sellItems(boxSlot)) {

                    /*
                     * List the item in the Exchange
                     */
                    api.grandExchange.sellItem(id, price, amount);

                    /*
                     * Confirm offer
                     */
                    api.grandExchange.confirm();
                    break;
                }
            }

            int finalIndex = index;

            /*
             * Sleep until our offer returns finished
             */
            new ConditionalSleep(10_000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return api.grandExchange.getStatus(boxIndexAt[finalIndex]).equals(GrandExchange.Status.FINISHED_SALE);
                }
            }.sleep();

            /*
             * If the sale is finished, collect it
             */
            if (api.grandExchange.getStatus(boxIndexAt[finalIndex]).equals(GrandExchange.Status.FINISHED_SALE)) {
                api.grandExchange.collect();
            }
        }
    }

    /**
     * Function buyItem is for handling purchases at the Grand Exchange
     * Handles null checks and also all of the Grand Exchange box slots
     *
     * @param item   the item to buy
     * @param id     the item id of the item
     * @param price  the price to pay for the item
     * @param amount the quantity to buy
     */
    private void buyItem(String item, int id, int price, int amount) {

        /*
         * If our inventory does not have the item, buy it!
         * We do not need to buy another
         */
        if (!api.inventory.contains(item)) {
            GrandExchange.Box[] boxIndexAt = GrandExchange.Box.values();
            int index = -1;

            /*
             * Loop through all the exchange box slots
             */
            for (GrandExchange.Box boxSlot : boxIndexAt) {
                //start loop with zero
                index++;

                /*
                 * Loop until buying an item returns true
                 */
                if (api.grandExchange.buyItems(boxSlot)) {

                    /*
                     * Place order in the exchange
                     */
                    api.grandExchange.buyItem(id, item.toLowerCase(), price, amount);

                    /*
                     * Confirm offer
                     */
                    api.grandExchange.confirm();
                    break;
                }
            }

            int finalIndex = index;

            /*
             * Sleep until our offer returns finished
             */
            new ConditionalSleep(10_000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return api.grandExchange.getStatus(boxIndexAt[finalIndex]).equals(GrandExchange.Status.FINISHED_BUY);
                }
            }.sleep();

            /*
             * If the order is finished, collect it
             */
            if (api.grandExchange.getStatus(boxIndexAt[finalIndex]).equals(GrandExchange.Status.FINISHED_BUY)) {
                api.grandExchange.collect();
            }
        }
    }
}
