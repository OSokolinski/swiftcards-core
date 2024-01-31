package swiftcards.core;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import swiftcards.core.game.lobby.*;
import swiftcards.core.networking.PlayerCredentials;
import swiftcards.core.networking.event.lobby.*;
import swiftcards.core.util.ConsoleLogger;
import swiftcards.core.util.Freezable;
import swiftcards.core.util.Subscriber;
import swiftcards.core.util.Subscriber.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class LobbyTest extends Freezable {

    @Test
    public void NetworkPlayer_CanJoinAndLeave_Lobby() {
        ConsoleLogger.keepLogging = false;

        PlayerCredentials guestCredentials_1 = new PlayerCredentials("Player one");
        PlayerCredentials guestCredentials_2 = new PlayerCredentials("Player two");

        HostLobby hostLobby = new HostLobby(Lobby.LobbyType.LAN, 5667);
        GuestLobby guestLobby_1 = new GuestLobby(guestCredentials_1, "127.0.0.1", 5667);
        GuestLobby guestLobby_2 = new GuestLobby(guestCredentials_2, "127.0.0.1", 5667);

        AtomicBoolean allStepsCompleted = new AtomicBoolean(false);

        AtomicBoolean firstPlayerJoining = new AtomicBoolean(false);
        AtomicBoolean secondPlayerJoining = new AtomicBoolean(false);
        AtomicBoolean firstPlayerLeaving = new AtomicBoolean(false);

        AtomicBoolean noneAssertionInThreadFailed = new AtomicBoolean(true);

        BasicConsumer<GameSettings> onSettingsUpdated = gameSettings -> {

            try {
                for (int i = 0; i < gameSettings.getPlayerAmountLimit(); i++) {
                    if (i == 0) {
                        assertEquals(PlayerSlot.PlayerSlotStatus.HUMAN, gameSettings.players.get(i).status);
                        assertEquals(new PlayerSlot(PlayerSlot.PlayerSlotStatus.HUMAN).playerName, gameSettings.players.get(i).playerName);
                    }
                    else if (i == 1 && firstPlayerJoining.get() && !firstPlayerLeaving.get()) {
                        assertEquals(PlayerSlot.PlayerSlotStatus.NETWORK_USED, gameSettings.players.get(i).status);
                        assertEquals(guestCredentials_1.getDisplayName(), gameSettings.players.get(i).playerName);
                    }
                    else if (i == 1 && firstPlayerLeaving.get()) {
                        assertEquals(PlayerSlot.PlayerSlotStatus.NETWORK_OPEN, gameSettings.players.get(i).status);
                        assertEquals(new PlayerSlot(PlayerSlot.PlayerSlotStatus.NETWORK_OPEN).playerName, gameSettings.players.get(i).playerName);
                    }
                    else if (i == 2 && secondPlayerJoining.get()) {
                        assertEquals(PlayerSlot.PlayerSlotStatus.NETWORK_USED, gameSettings.players.get(i).status);
                        assertEquals(guestCredentials_2.getDisplayName(), gameSettings.players.get(i).playerName);
                    }
                    else if (i > 2 && i < gameSettings.getPlayerAmount()) {
                        assertEquals(PlayerSlot.PlayerSlotStatus.NETWORK_OPEN, gameSettings.players.get(i).status);
                        assertEquals(new PlayerSlot(PlayerSlot.PlayerSlotStatus.NETWORK_OPEN).playerName, gameSettings.players.get(i).playerName);
                    }
                    else if (i > 2) {
                        assertEquals(PlayerSlot.PlayerSlotStatus.CLOSED, gameSettings.players.get(i).status);
                        assertEquals(new PlayerSlot(PlayerSlot.PlayerSlotStatus.CLOSED).playerName, gameSettings.players.get(i).playerName);
                    }
                }
            }
            catch (AssertionError a) {
                noneAssertionInThreadFailed.set(false);
                throw a;
            }

        };

        hostLobby.getEventBus().on(SettingsUpdated.class, new Subscriber<>(onSettingsUpdated));
        guestLobby_1.getEventBus().on(SettingsUpdated.class, new Subscriber<>(gameSettings -> {
            onSettingsUpdated.accept(gameSettings);
            if (!secondPlayerJoining.get()) {
                secondPlayerJoining.set(true);
                guestLobby_2.prepare();
            }
        }));
        guestLobby_2.getEventBus().on(SettingsUpdated.class, new Subscriber<>(gameSettings -> {
            onSettingsUpdated.accept(gameSettings);
            if (!firstPlayerLeaving.get()) {
                firstPlayerLeaving.set(true);
                guestLobby_1.disconnect();
            }
            else {
                allStepsCompleted.set(true);
                resume();
            }
        }));

        hostLobby.getEventBus().on(LobbyPrepared.class, new Subscriber<>((c) -> {
            firstPlayerJoining.set(true);
            guestLobby_1.prepare();
        }));

        hostLobby.prepare();

        CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS).execute(this::resume);

        freeze();
        assertTrue(allStepsCompleted.get());
        assertTrue(noneAssertionInThreadFailed.get());
    }

    @Test
    public void NetworkPlayers_CanToggle_ReadinessStatus() {
        ConsoleLogger.keepLogging = false;

        PlayerCredentials guestCredentials_1 = new PlayerCredentials("Player one");
        PlayerCredentials guestCredentials_2 = new PlayerCredentials("Player two");

        HostLobby hostLobby = new HostLobby(Lobby.LobbyType.LAN, 5668);
        GuestLobby guestLobby_1 = new GuestLobby(guestCredentials_1, "127.0.0.1", 5668);
        GuestLobby guestLobby_2 = new GuestLobby(guestCredentials_2, "127.0.0.1", 5668);

        AtomicBoolean allStepsCompleted = new AtomicBoolean(false);

        AtomicBoolean firstPlayerJoined = new AtomicBoolean(false);
        AtomicBoolean secondPlayerJoined = new AtomicBoolean(false);

        AtomicBoolean firstPlayerHitReady = new AtomicBoolean(false);
        AtomicBoolean firstPlayerHitNotReady = new AtomicBoolean(false);
        AtomicBoolean secondPlayerHitReady = new AtomicBoolean(false);

        AtomicBoolean noneAssertionFailed = new AtomicBoolean(true);

        Subscriber.BasicConsumer<GameSettings> testingHandler = gameSettings -> {
            if (!secondPlayerJoined.get()) {
                return;
            }

            try {
                for (int i = 0; i < gameSettings.getPlayerAmountLimit(); i++) {
                    if (i == 0) {
                        assertTrue(gameSettings.players.get(i).isReady());
                    }
                    else if (i == 1 && firstPlayerHitNotReady.get()) {
                        assertFalse(gameSettings.players.get(i).isReady());
                    }
                    else if (i == 1 && firstPlayerHitReady.get()) {
                        assertTrue(gameSettings.players.get(i).isReady());
                    }
                    else if (i == 2 && secondPlayerHitReady.get()) {
                        assertTrue(gameSettings.players.get(i).isReady());
                    }
                    else {
                        assertFalse(gameSettings.players.get(i).isReady());
                    }
                }
            }
            catch (AssertionError a) {
                noneAssertionFailed.set(false);
                throw a;
            }

            if (allStepsCompleted.get()) {
                resume();
            }
        };

        AtomicInteger iteration = new AtomicInteger(0);
        Subscriber.BasicConsumer<GameSettings> managingHandler = gameSettings -> {
            // Player 1 joined; preparing Player 2 to join
            int i = iteration.getAndIncrement();
            if (i == 0) {
                firstPlayerJoined.set(true);
                guestLobby_2.prepare();
            }
            // Player 2 joined; preparing Player 1 to hit ready
            else if (i == 1) {
                secondPlayerJoined.set(true);
                guestLobby_1.toggleIsReady();
            }
            // Player 1 hit ready; preparing Player 2 to hit ready
            else if (i == 2) {
                firstPlayerHitReady.set(true);
                guestLobby_2.toggleIsReady();
            }
            // Player 2 hit ready; preparing Player 1 to hit no ready
            else if (i == 3) {
                secondPlayerHitReady.set(true);
                guestLobby_1.toggleIsReady();
            }
            // Player 1 hit not ready
            else if (i == 4) {
                firstPlayerHitNotReady.set(true);
                allStepsCompleted.set(true);
            }
            testingHandler.accept(gameSettings);
        };

        hostLobby.getEventBus().on(SettingsUpdated.class, new Subscriber<>((s) -> {
            synchronized (managingHandler) {
                managingHandler.accept(s);
            }
        }));

        hostLobby.getEventBus().on(LobbyPrepared.class, new Subscriber<>((s) -> guestLobby_1.prepare()));
        hostLobby.prepare();

        CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS).execute(this::resume);

        freeze();
        assertTrue(noneAssertionFailed.get());
        assertTrue(allStepsCompleted.get());
    }

    @Test
    public void Host_CanStart_WhenEachPlayerReady() {
        ConsoleLogger.keepLogging = false;
        CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS).execute(this::resume);

        PlayerCredentials guestCredentials_1 = new PlayerCredentials("Player one");
        PlayerCredentials guestCredentials_2 = new PlayerCredentials("Player two");

        HostLobby hostLobby = new HostLobby(Lobby.LobbyType.LAN, 5669);
        GuestLobby guestLobby_1 = new GuestLobby(guestCredentials_1, "127.0.0.1", 5669);
        GuestLobby guestLobby_2 = new GuestLobby(guestCredentials_2, "127.0.0.1",  5669);

        AtomicBoolean statusPlayer_1HittingReady = new AtomicBoolean(false);
        AtomicBoolean statusPlayer_2HittingReady = new AtomicBoolean(false);
        AtomicBoolean statusPlayer_1HittingNotReady = new AtomicBoolean(false);

        AtomicBoolean lobbyReadinessOkAfterPlayer_2HitReady = new AtomicBoolean(false);
        AtomicBoolean lobbyReadinessOkAfterPlayer_1HitNotReady = new AtomicBoolean(false);
        AtomicBoolean lobbyReadinessChangedUnexpectedly = new AtomicBoolean(false);

        hostLobby.getEventBus().on(LobbyReadinessChanged.class, (BasicConsumer<Boolean>) (data) -> {

            if (statusPlayer_1HittingNotReady.get()) {
                lobbyReadinessOkAfterPlayer_1HitNotReady.set(!data);
            }
            else if (statusPlayer_1HittingReady.get() && statusPlayer_2HittingReady.get() && !statusPlayer_1HittingNotReady.get()) {
                lobbyReadinessOkAfterPlayer_2HitReady.set(data);
            }
            else {
                lobbyReadinessChangedUnexpectedly.set(true);
            }

            resume();
        });

        // Lobby is getting prepared and players are joining

        hostLobby.getEventBus().on(LobbyPrepared.class, new Subscriber<>((r) -> resume()));
        hostLobby.prepare();
        freeze();

        guestLobby_1.getEventBus().on(SettingsUpdated.class, (ContextConsumer<GameSettings>) (data, subscriberCtx) -> {
            guestLobby_1.getEventBus().unsubscribe(SettingsUpdated.class, subscriberCtx);
            resume();
        });
        guestLobby_1.prepare();
        freeze();

        guestLobby_2.getEventBus().on(SettingsUpdated.class, (ContextConsumer<GameSettings>) (data, subscriberCtx) -> {
            guestLobby_2.getEventBus().unsubscribe(SettingsUpdated.class, subscriberCtx);
            resume();
        });
        guestLobby_2.prepare();
        freeze();

        // Player 1 is getting ready
        statusPlayer_1HittingReady.set(true);
        guestLobby_1.getEventBus().on(SettingsUpdated.class, (ContextConsumer<GameSettings>) (data, subscriberCtx) -> {
            guestLobby_1.getEventBus().unsubscribe(SettingsUpdated.class, subscriberCtx);
            resume();
        });
        guestLobby_1.toggleIsReady();
        freeze();

        // Player 2 is getting ready
        statusPlayer_2HittingReady.set(true);
        guestLobby_2.getEventBus().on(SettingsUpdated.class, (ContextConsumer<GameSettings>) (data, subscriberCtx) -> {
            guestLobby_2.getEventBus().unsubscribe(SettingsUpdated.class, subscriberCtx);
            //resume();
        });
        guestLobby_2.toggleIsReady();
        freeze();

        // Player 1 is hitting not ready
        statusPlayer_1HittingNotReady.set(true);
        guestLobby_1.getEventBus().on(SettingsUpdated.class, (ContextConsumer<GameSettings>) (data, subscriberCtx) -> {
            guestLobby_1.getEventBus().unsubscribe(SettingsUpdated.class, subscriberCtx);
            //resume();
        });
        guestLobby_1.toggleIsReady();
        freeze();

        assertTrue(lobbyReadinessOkAfterPlayer_2HitReady.get());
        assertTrue(lobbyReadinessOkAfterPlayer_1HitNotReady.get());
        assertFalse(lobbyReadinessChangedUnexpectedly.get());
    }
}
