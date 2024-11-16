package kor.bots.winemaker;

import com.runemate.game.api.client.*;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.region.*;
import com.runemate.game.api.osrs.local.hud.interfaces.MakeAllInterface;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.listeners.*;
import com.runemate.game.api.script.framework.listeners.events.*;
import com.runemate.pathfinder.Pathfinder;
import com.runemate.ui.*;
import com.runemate.ui.setting.annotation.open.*;
import lombok.*;
import lombok.extern.log4j.*;
import kor.framework.*;
import kor.framework.breaking.*;

@Log4j2(topic = "Example")
public class Main extends BreakingLoop implements Breaking, EngineListener, SettingsListener {

    @Setter
    private StateAction state;
    private boolean started = false;

    @Getter
    @SettingsProvider(updatable = true)
    private BreakConfig breakConfig;

    @Override
    public void loop() {
        if (isPaused() || breakConfig == null || !started) return;

        if (state == null) {
            if (Bank.isOpen()) {
                state = new BankState(this);
            } else if (Inventory.contains("Jug of water") && Inventory.contains("Grapes")) {
                state = new WineState(this);
            } else {
                state = new BankState(this);
            }
        }

        state.execute();
    }

    @Override
    public void start() {
        getEventDispatcher().addListener(this);
        DefaultUI.setItemEventListening(this, false);
        ClientUI.showAlert(ClientUI.AlertLevel.INFO,
                "You can join KOR Discord to find out more information - <a href=\"https://discord.gg/5NGdx3BSsb\">Click here</a>");
    }

    @Override
    public BreakConfig breakConfig() {
        return breakConfig;
    }

    @Override
    public Break.Behavior onBreakRequested(final Break requested) {
        if (Region.isInstanced()) {
            return Break.Behavior.DEFER;
        }
        return Breaking.defaultBehavior(requested);
    }

    @Override
    public Stop.Behavior onStopRequested(final Stop requested) {
        if (Region.isInstanced()) {
            return Stop.Behavior.DEFER;
        }
        return Stop.Behavior.STOP;
    }

    @Override
    public void onSettingChanged(final SettingChangedEvent settingChangedEvent) {}

    @Override
    public void onSettingsConfirmed() {
        started = true;
    }
}