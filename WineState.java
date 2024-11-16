package kor.bots.winemaker;

import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.osrs.local.hud.interfaces.MakeAllInterface;
import com.runemate.game.api.script.Execution;

import com.runemate.ui.DefaultUI;
import kor.framework.Animation;
import lombok.extern.log4j.Log4j2;

@Log4j2(topic = "WineState")
public class WineState implements StateAction {

    private final Main bot;

    public WineState(Main bot) {
        this.bot = bot;
    }

    @Override
    public void execute() {
        Player local = Players.getLocal();
        if (local == null) return;

        if (Animation.playerIsAnimating()) {
            log.info("We're animating, let's wait...");
            return;
        }

        if (!Inventory.contains("Jug of water") || !Inventory.contains("Grapes")) {
            bot.setState(new BankState(bot));
            return;
        }

        if (MakeAllInterface.isOpen() && MakeAllInterface.selectItem("Unfermented wine", true)) {
            Execution.delayUntil(() -> !MakeAllInterface.isOpen(), 1200);
            return;
        }

        if (Inventory.containsAllOf("Jug of water", "Grapes") && !Animation.playerIsAnimating()) {
            SpriteItem jugOfWater = Inventory.newQuery().names("Jug of water").results().sortByIndex().last();
            SpriteItem grapes = Inventory.newQuery().names("Grapes").results().sortByIndex().first();

            if (jugOfWater != null && grapes != null) {
                if (jugOfWater.interact("Use") && grapes.interact("Use", "Jug of water" + " -> " + "Grapes")) {
                    DefaultUI.setStatus("Making wine!");
                    log.info("We're making wine");
                    Execution.delayUntil(MakeAllInterface::isOpen, 1200);
                }
            }
        }
    }
}
