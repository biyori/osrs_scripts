package AIODraynorLumJack.src;

import org.osbot.rs07.api.Bank;
import org.osbot.rs07.api.Widgets;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

/**
 * Created by Kyle on 2016/12/30.
 */
class BankClass {
    MethodProvider api;

    BankClass(MethodProvider ex) {
        this.api = ex;
    }

    /**
     * A simple function to interact with a bank booth
     *
     * @param booth       the booth to interact with
     * @param interaction the action to the booth
     */
    void bankWithBooth(RS2Object booth, String interaction) {
        if (booth.interact(interaction)) {
            //Force a delay between actions (this afk might be unnecessary)
            afk(777, 1111);

            //If the interaction was successful, call the openBank function
            openBank();
        }
    }

    /**
     * A simple function to interact with a bank booth
     *
     * @param banker      the banker to interact with
     * @param interaction the action to the banker
     */
    void bankWithBanker(NPC banker, String interaction) {
        if (banker.interact(interaction)) {
            //Force a delay between actions (same as above this afk might be unnecessary)
            afk(777, 1111);

            //If the interaction was successful, call the openBank function
            openBank();
        }
    }

    /**
     * Private function to handle opening the bank
     * <p>
     * Detects bank dialogues, bank pin windows, and handles collecting completed purchases from the ExchangeTask
     */
    private void openBank() {

        /*
         * Create one widget object
         */
        Widgets widget = api.getWidgets();

        api.log("Attempting to bank");

        /*
         * NPC banker dialogue is visible
         */
        if (widget.isVisible(231)) {
            api.log("Bank chat is visible!");
            new ConditionalSleep(5_000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return api.dialogues.inDialogue();
                }
            }.sleep();

            /*
             * Handle the banker dialogue
             */
            bankerTalk();
        }

        /*
         * Bank PIN widget is visible
         */
        if (widget.isVisible(213)) {
            api.log("Bank PIN window open. Waiting for RandomSolver...");

            /*
             * If the widget is visible, sleep until the RandomSolver enters the PIN
             */
            new ConditionalSleep(5_000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return api.bank.isOpen();
                }
            }.sleep();
        }

        /*
         * Bank PIN widget is not visible, so just sleep until the bank is open
         */
        new ConditionalSleep(5_000) {
            @Override
            public boolean condition() throws InterruptedException {
                return api.bank.isOpen();
            }
        }.sleep();
    }

    /**
     * A banker interaction function to open the collection window
     *
     * @param banker the banker to interact with
     */
    void openCollectWindow(NPC banker) {

        /*
         * Interact with the banker and open the collect widget
         */
        if (banker.interact("Collect")) {

            /*
             * Sleep until the collection widget is visible
             */
            new ConditionalSleep(5_000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return api.getWidgets().isVisible(402);
                }
            }.sleep();
        }
    }

    /**
     * The function tries to deposit logs, and then check the bank for a better axe to use
     */
    void depositLogs() {
        if (api.getBank() != null) {
            if (api.bank.isOpen()) {
                afk(200, 500);
                api.log("Depositing our logs");
                depositItems();
                //Tell ExchangeTask to go buy some axes
                if (Constants.progressiveMode() && Constants.sellLogsAtGE())
                    needToBuyAxes();
                upgradeAxe();
            } else {
                api.log("Failed to bank");
            }
        }
    }

    /**
     * The function tries to find an axe to use
     */
    void getAxe() {
        if (api.getBank() != null) {
            if (api.bank.isOpen()) {
                afk(200, 500);
                api.log("Searching for an axe to use");
                //Tell ExchangeTask to go buy some axes
                if (Constants.progressiveMode() && Constants.sellLogsAtGE())
                    needToBuyAxes();
                upgradeAxe();
            } else {
                api.log("Failed to bank");
            }
        }
    }

    /**
     * The function will set the bank mode to withdraw notes, and proceed to take out logs, and oak logs if there is enough
     * quest points to trade them
     */
    void getLogs() {
        if (api.getBank() != null) {
            if (api.bank.isOpen()) {
                afk(200, 500);
                api.bank.enableMode(Bank.BankMode.WITHDRAW_NOTE);
                api.log("Searching logs...");
                if (api.bank.withdrawAll("Logs")) {
                    api.log("Found logs");
                    new ConditionalSleep(5_000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return api.inventory.contains("Logs");
                        }
                    }.sleep();
                }
                if (api.quests.getQuestPoints() > 6 && api.bank.withdrawAll("Oak logs")) {
                    api.log("Found oak logs");
                    new ConditionalSleep(5_000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return api.inventory.contains("Oak logs");
                        }
                    }.sleep();
                }
                api.bank.close();
            } else {
                api.log("Failed to bank.");
            }
        }
    }


    /**
     * Deposit all items except our woodcutting axes
     * <p>
     * This function also counts the total logs in the bank to display on the paint
     */
    private void depositItems() {
        api.bank.depositAllExcept(Constants.woodAxes);
        new ConditionalSleep(5_000) {
            @Override
            public boolean condition() throws InterruptedException {
                return api.inventory.isEmptyExcept(Constants.woodAxes);
            }
        }.sleep();
        Constants.setTotalLogs(api.bank.getItem(Constants.treeLogType()).getAmount());
    }

    /**
     * Function to calculate if an axe can be upgraded or not
     * <p>
     * Check if we are holding an axe, or have an axe in our inventory. If we do have an axe--run some checks to see
     * what the best axe to use is. If we have the best axe in the bank--withdraw that axe if we don't have the best axe
     * downgrade our criteria until the axe in bank is better than our current axe in use
     * <p>
     * If we do not have any axes to take out then activate needToBuyAxes() function
     */
    private void upgradeAxe() {
        api.log("Upgrading...");
        int level = api.skills.getStatic(Skill.WOODCUTTING);
        if (!((!api.equipment.contains(Constants.woodAxes) && api.inventory.contains(Constants.woodAxes)) ||
                (!api.inventory.contains(Constants.woodAxes) && api.equipment.contains(Constants.woodAxes)) ||
                (api.inventory.contains(Constants.woodAxes) && api.equipment.contains(Constants.woodAxes)))) {
            api.log("No axes found in inventory or equipment, upgrading to next best...");
            for (int i = Constants.woodAxes.length - 1; i > -1; i--) {
                if (Constants.canUseAxe(Constants.woodAxes[i], level)) {
                    if (api.bank.withdraw(Constants.woodAxes[i], 1)) {
                        api.log("Next best axe to use is " + Constants.woodAxes[i]);
                        api.bank.depositAllExcept(Constants.woodAxes[i]);
                        api.bank.close();
                        if (api.inventory.contains(Constants.woodAxes[i])) {
                            api.getEquipment().equip(EquipmentSlot.WEAPON, Constants.woodAxes[i]);
                        }
                        break;
                    }
                }
            }
            return;
        }

        Item item = api.inventory.getItem(Constants.woodAxes);
        if (api.equipment.getItem(Constants.woodAxes) != null) {
            if (item != null) {
                if (Constants.axeLevelReq(api.equipment.getItem(Constants.woodAxes).getName()) < Constants.axeLevelReq(item.getName()))
                    item = api.inventory.getItem(Constants.woodAxes);
                else
                    item = api.equipment.getItem(Constants.woodAxes);
            } else {
                item = api.equipment.getItem(Constants.woodAxes);
            }
        }

        if (Constants.axeLevelReq(item.getName()) < Constants.axeLevelReq(Constants.bestAxeToUse(level))) {
            if (api.bank.withdraw(Constants.bestAxeToUse(level), 1)) {
                api.log("Found " + Constants.bestAxeToUse(level) + " which is the best axe we can use");
                api.bank.depositAllExcept(Constants.bestAxeToUse(level));
            } else {
                api.log("We do not have a " + Constants.bestAxeToUse(level) + " to withdraw. Downgrading options...");

                for (int i = Constants.woodAxes.length - 1; i > -1; i--) {
                    if (Constants.canUseAxe(Constants.woodAxes[i], level) && Constants.axeLevelReq(item.getName()) < Constants.axeLevelReq(Constants.woodAxes[i])) {
                        if (api.bank.withdraw(Constants.woodAxes[i], 1)) {
                            api.log("Next best axe to use is a " + Constants.woodAxes[i]);
                            api.bank.depositAllExcept(Constants.woodAxes[i]);
                            break;
                        }
                    }
                }
            }
        }
    }


    /**
     * If our bank does not have axe upgrades, calculate our logs value to see if we have enough to sell for upgrades
     * <p>
     * Activate the ExchangeTask once the condition is met.
     */
    private void needToBuyAxes() {
        if (!api.bank.contains("Steel axe", "Mithril axe", "Adamant axe")) {
            Item logs = api.bank.getItem("Logs");
            if (logs != null) {
                //We need 2000 gold to purchases all axes up to adamant (Excluding black axes).
                //TODO: In the future possibly just bid on the current axe price from the OSBuddy Exchange and just +5% 2-3x to instantly buy the axe
                if (logs.getAmount() * Constants.getLogPrice() >= 2000) {
                    api.log("We have enough logs to purchase axes!");
                    Constants.needToBuyAxes(true);
                } else {
                    api.log("We need " + Math.ceil((2000 - (logs.getAmount() * Constants.getLogPrice())) / Constants.getLogPrice()) + " more logs until we can purchase upgrades.");
                }
            }
        } else {
            Constants.pauseProgressiveMode(false);
        }
    }

    /**
     * This function detects bank dialogues and if dialog is open--process the options
     */
    private void bankerTalk() {
        if (api.dialogues.isPendingContinuation()) {
            api.log("Dialog open! Clicking continue...");
            afk(300, 1000);
            api.dialogues.clickContinue();
            new ConditionalSleep(5_000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return api.dialogues.isPendingOption();
                }
            }.sleep();
        }
        if (api.dialogues.isPendingOption()) {
            api.log("Dialog open! Selecting option one to open bank.");
            afk(300, 1000);
            api.dialogues.selectOption(1); //First choice "...access bank account please..."
            new ConditionalSleep(5_000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return api.bank.isOpen();
                }
            }.sleep();
        }
    }

    /**
     * A function to simulate AFK with some constraints
     *
     * @param min minimum about of time to afk in milliseconds
     * @param max maximum about of time to afk in milliseconds
     */
    private void afk(int min, int max) {
        try {
            MethodProvider.sleep(MethodProvider.random(min, max));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * A function to handle collecting previously set offers for bought axes. If the ExchangeTask places an order but the
     * axe we purchased was not completed--this function will collect the axe at the next bank session
     */
    void collectAxes() {
        Widgets widget = api.getWidgets();
        if (widget.isVisible(402)) {
            new ConditionalSleep(5_000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return widget.isVisible(402);
                }
            }.sleep();
            if (widget.containingActions(402, "Collect to bank").get(0).interact()) {
                api.log("Successfully collected axes");

                /*
                 * Tell the script we have collected axes
                 */
                Constants.collectAxeAtBank(false);
            }
        }
    }

    /**
     * Function to deposit everything including equipment
     */
    void depositAll() {
        api.bank.depositAll();
        new ConditionalSleep(5_000) {
            @Override
            public boolean condition() throws InterruptedException {
                return api.inventory.isEmpty();
            }
        }.sleep();
        api.bank.depositWornItems();
        new ConditionalSleep(5_000) {
            @Override
            public boolean condition() throws InterruptedException {
                return api.equipment.isEmpty();
            }
        }.sleep();
    }
}