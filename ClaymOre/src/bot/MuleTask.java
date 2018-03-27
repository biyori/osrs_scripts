package bot;

import org.osbot.rs07.api.Bank;
import org.osbot.rs07.api.map.constants.Banks;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

/**
 * Created by Kyle on 2016/12/18.
 */
public class MuleTask extends Task {

    private boolean foundMule = false;
    private boolean triedBank = false;
    private boolean hasLoot = false;
    private String[] itemsToTrade = {"Oak logs", "Logs", "Yew logs", "Bronze axe", "Iron axe", "Steel axe", "Black axe", "Mithril axe", "Adamant axe", "Rune axe",
            "Bronze pickaxe", "Iron pickaxe", "Steel pickaxe", "Black pickaxe", "Mithril pickaxe", "Adamant pickaxe", "Rune pickaxe",
            "Uncut sapphire", "Uncut emerald", "Uncut ruby", "Uncut Diamond",
            "Iron ore", "Clay", "Coins"};//gems

    MuleTask(MethodProvider api) {
        super(api);
    }

    @Override
    public boolean canProcess() {
        return Global.timeToTradeMule();
    }

    @Override
    public void process() {
        api.log("Top of MuleTask");
        if (Banks.GRAND_EXCHANGE.contains(api.myPlayer()) && !hasLoot) {

            RS2Object bankBooth = api.objects.closest(Banks.GRAND_EXCHANGE, "Bank booth");
            NPC banker = api.npcs.closest(Banks.GRAND_EXCHANGE, "Banker");
            if (bankBooth != null) {
                if (bankBooth.isVisible()) {
                    if (!api.bank.isOpen() && !triedBank) {
                        triedBank = true;
                        int iterations = 0;
                        switch (random(0, 3)) {
                            case 1:
                                api.log("Banking case 1");
                                if (banker.interact("Bank")) {//WTF WHILE LOOP??
                                    while (!api.bank.isOpen() && !api.widgets.isVisible(213)) {
                                        api.log("Attempting to bank #" + iterations);
                                        bankerTalk();
                                        afk(200, 500);

                                        //Testing if this fixes afk in bank
                                        iterations++;
                                        if (iterations > 15) {
                                            triedBank = false;
                                            break;
                                        }
                                    }
                                    api.log("Took " + iterations + " attempts to bank");
                                    tryBank();
                                }
                                break;
                            case 2:
                                api.log("Banking case 2");
                                if (bankBooth.interact("Bank")) {
                                    while (!api.bank.isOpen() && !api.widgets.isVisible(213)) {
                                        api.log("Attempting to bank #" + iterations);
                                        bankerTalk();
                                        afk(200, 500);

                                        //Testing if this fixes afk in bank
                                        iterations++;
                                        if (iterations > 15) {
                                            triedBank = false;
                                            break;
                                        }
                                    }
                                    api.log("Took " + iterations + " attempts to bank");
                                    tryBank();
                                }
                                break;
                            case 3:
                                api.log("Banking case 3");
                                if (banker.interact("Talk-to")) {
                                    while (!api.bank.isOpen() && !api.widgets.isVisible(213)) {
                                        api.log("Attempting to bank #" + iterations);
                                        bankerTalk();
                                        afk(200, 500);

                                        //Testing if this fixes afk in bank
                                        iterations++;
                                        if (iterations > 15) {
                                            triedBank = false;
                                            break;
                                        }
                                    }
                                    api.log("Took " + iterations + " attempts to bank");
                                    tryBank();
                                }
                                break;
                            default:
                                api.log("Banking case (Default)");
                                if (bankBooth.interact("Bank")) {
                                    while (!api.bank.isOpen() && !api.widgets.isVisible(213)) {
                                        api.log("Attempting to bank #" + iterations);
                                        bankerTalk();
                                        afk(200, 500);

                                        //Testing if this fixes afk in bank
                                        iterations++;
                                        if (iterations > 15) {
                                            triedBank = false;
                                            break;
                                        }
                                    }
                                    api.log("Took " + iterations + " attempts to bank");
                                    tryBank();
                                }
                                break;
                        }
                        afk(200, 400);
                    } else if (api.bank.isOpen())
                        tryBank();
                } else {
                    api.camera.toEntity(bankBooth);
                }
            }
        } else {
            api.log("Need to walk to GE");
            WebWalkEvent walktoGE = new WebWalkEvent(Banks.GRAND_EXCHANGE);
            walktoGE.setEnergyThreshold(15);
            walktoGE.useSimplePath();
            api.execute(walktoGE);
        }
        if (hasLoot) {
            if (!foundMule) {
                findMule();

                if (foundMule)
                    tradeMule();
            }
        }
        api.log("Bottom of MuleTask");
    }

    private void tryBank() {
        api.log("Trying to bank");
        if (api.widgets.isVisible(213)) {
            api.log("Bank PIN window open. Waiting for RandomSolver...");
        } else {
            if (api.bank.isOpen()) {
                api.log("Getting all ores and goodies");
                afk(200, 500);
                api.bank.enableMode(Bank.BankMode.WITHDRAW_NOTE);
                afk(200, 500);
                for (String item : itemsToTrade) {
                    if (api.bank.contains(item)) {
                        api.bank.withdrawAll(item);
                        afk(200, 500);
                    }
                }
                hasLoot = true;
                //Global.setTotalOres(api.bank.getItem("Oak logs", "Logs", "Willow logs", "Yew logs").getAmount());
                api.bank.close();//close bank
            }
        }
    }

    private void bankerTalk() {
        if (api.dialogues.isPendingContinuation()) {
            api.log("Dialog open! Clicking continue...");
            api.dialogues.clickContinue();
            afk(300, 500);
        }
        if (api.dialogues.isPendingOption()) {
            api.log("Dialog open! Selecting option one to open bank.");
            api.dialogues.selectOption(1); //First choice "...access bank account please..."
            afk(300, 500);
        }
    }

    private void findMule() {
        Player mule = api.getPlayers().closest(o -> o.getName().replaceAll("\\u00a0", " ").equalsIgnoreCase(Global.getMuleUsername()));
        if (mule != null) {
            if (mule.exists()) {
                api.log("Found Mule! " + mule.getName());
                foundMule = true;
            } else
                api.log("Waiting for mule...");
        } else {
            api.log("Mule is not online");
        }
    }

    private void tradeMule() {
        Player mule = api.getPlayers().closest(o -> o.getName().replaceAll("\\u00a0", " ").equalsIgnoreCase(Global.getMuleUsername()));
        if (mule != null) {
            if (mule.isVisible() || (mule.getPosition().getX() == api.myPlayer().getX() && mule.getPosition().getY() == api.myPlayer().getY())) {
                if (!api.trade.isCurrentlyTrading()) {
                    api.log("Trying to trade...");
                    if (mule.interact("Trade with")) {
                        api.log("Waiting for other player to accept trade");
                        new ConditionalSleep(2500) {
                            @Override
                            public boolean condition() throws InterruptedException {
                                return api.trade.isFirstInterfaceOpen();
                            }
                        }.sleep();
                    } else {
                        api.log("Trade failed!");
                    }
                }
                if (api.trade.isCurrentlyTrading() && !api.trade.getOtherPlayer().replaceAll("\\u00a0", " ").equalsIgnoreCase(Global.getMuleUsername())) {
                    api.trade.declineTrade();
                }
                if (api.trade.isFirstInterfaceOpen()) {
                    api.log("Other player accepted the trade! Trading over loot...");
                    for (String currentItem : itemsToTrade) {
                        if (api.inventory.contains(currentItem)) {
                            api.trade.offer(currentItem, api.inventory.getItem(currentItem).getAmount());
                            afk(200, 500);
                        }
                    }
                    afk(200, 500);
                    api.trade.acceptTrade();
                    new ConditionalSleep(2500) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return api.trade.isSecondInterfaceOpen();
                        }
                    }.sleep();
                }
                if (api.trade.isSecondInterfaceOpen()) {
                    api.log("Finalizing trade");
                    api.trade.acceptTrade();
                    new ConditionalSleep(2500) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return !api.trade.isCurrentlyTrading();
                        }
                    }.sleep();
                }
                if ((api.inventory.getItem("Clay") == null || api.inventory.getItem("Iron ore") == null) && !api.trade.isCurrentlyTrading()) {
                    foundMule = false;
                    api.log("Trade complete! Now get back to work...");
                    Global.shouldMule(false);
                }
            } else {
                api.camera.toEntity(mule);
            }
        } else {
            api.log("Cannot find mule in MuleTask");
            afk(200, 500);
        }

    }

    private void afk(int min, int max) {
        try {
            sleep(random(min, max));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
