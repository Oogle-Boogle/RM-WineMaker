package kor.bots.winemaker;

import com.runemate.game.api.client.ClientUI;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;
import com.runemate.ui.DefaultUI;

import lombok.extern.log4j.Log4j2;

@Log4j2(topic = "BankState")
public class BankState implements StateAction {

    private final Main bot;

    public BankState(Main bot) {
        this.bot = bot;
    }

    @Override
    public void execute() {
        DefaultUI.setStatus("Banking...");

        Player local = Players.getLocal();
        if (local == null) return;

        if (!Bank.isOpen() && Bank.open()) {
            Execution.delayUntil(Bank::isOpen, 2800);
        } else if (Bank.isOpen()) {
            if (!Bank.contains("Jug of water") || !Bank.contains("Grapes")) {
                String message = "You've ran out of supplies!";
                ClientUI.showAlert(ClientUI.AlertLevel.WARN, message);
                bot.stop(message);
                return;
            }

            if (Inventory.contains("Unfermented wine") || Inventory.contains("Jug of wine")) {
                if (Bank.deposit("Unfermented wine", 0) || Bank.deposit("Jug of wine", 0)) {
                    log.info("Depositing wine...");
                    Execution.delayWhile(() -> true, 1200);
                    return;
                }
            }

            if (!Inventory.contains("Jug of water") && Bank.withdraw("Jug of water", 14)) {
                log.info("Withdrawing jug of water...");
                Execution.delayUntil(() -> Inventory.contains("Jug of water"), 800);
                return;
            }

            if (!Inventory.contains("Grapes") && Bank.withdraw("Grapes", 14)) {
                log.info("Withdrawing Grapes...");
                Execution.delayUntil(() -> Inventory.contains("Grapes"), 800);
                return;
            }

            log.info("Closing bank!");
            Bank.close(true);
            bot.setState(new WineState(bot));
        }
    }
}
