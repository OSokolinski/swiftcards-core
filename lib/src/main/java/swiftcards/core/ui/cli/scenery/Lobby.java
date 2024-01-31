package swiftcards.core.ui.cli.scenery;

import swiftcards.core.game.lobby.GameSettings;
import swiftcards.core.game.lobby.GuestLobby;
import swiftcards.core.game.lobby.HostLobby;
import swiftcards.core.networking.event.lobby.SettingsUpdated;
import swiftcards.core.ui.CLI;
import swiftcards.core.util.ConfigService;
import swiftcards.core.util.Freezable;
import swiftcards.core.util.Subscriber;
import swiftcards.core.util.TextService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Lobby extends Freezable implements Stage<swiftcards.core.game.lobby.Lobby> {

    private swiftcards.core.game.lobby.Lobby lobby = null;
    private Future<?> cliPromptTask = null;
    private Stage<?> referringStage = null;

    private final String[] optionsHostNotReady;
    private final String[] optionsHostReady;
    private final String[] optionsGuestNotReady;
    private final String[] optionsGuestReady;

    private String[] options = new String[] { };

    public Lobby() {
        optionsHostNotReady = new String[] {
            TextService.getInstance().getTranslatedText("lobby-disconnect"),
            TextService.getInstance().getTranslatedText("lobby-addaiplayer"),
            TextService.getInstance().getTranslatedText("lobby-kickplayer")
        };
        optionsHostReady = new String[] {
            TextService.getInstance().getTranslatedText("lobby-disconnect"),
            TextService.getInstance().getTranslatedText("lobby-addaiplayer"),
            TextService.getInstance().getTranslatedText("lobby-kickplayer"),
            TextService.getInstance().getTranslatedText("lobby-startgame")
        };
        optionsGuestNotReady = new String[] {
            TextService.getInstance().getTranslatedText("lobby-disconnect"),
            TextService.getInstance().getTranslatedText("lobby-ready")
        };
        optionsGuestReady = new String[] {
            TextService.getInstance().getTranslatedText("lobby-disconnect"),
            TextService.getInstance().getTranslatedText("lobby-notready")
        };
    }

    @Override
    public void setStageData(swiftcards.core.game.lobby.Lobby derivedLobby) {
        lobby = derivedLobby;
        options = (derivedLobby instanceof HostLobby) ? optionsHostNotReady : optionsGuestNotReady;

    }

    @Override
    public Stage<?> goToStage() {

        lobby.getEventBus()
            .on(SettingsUpdated.class, new Subscriber<>(this::onSettingsUpdated));
        lobby.prepare();


        ExecutorService a = Executors.newCachedThreadPool();
        cliPromptTask = a.submit(this::consolePromptLoop);

        freeze();

        return referringStage;
    }

    private void onSettingsUpdated(GameSettings settings) {
        CLI.getInstance().drawCurrentLobby(settings, null);
        CLI.getInstance().printOptions(options, false);

        if (settings.status == GameSettings.LobbyStatus.READY) {
            cliPromptTask.cancel(true);
            resume();
        }
    }

    private void consolePromptLoop() {
        while (true) {
            int option = CLI.getInstance().promptSelection(options);
            if (option == 0) {
                referringStage = new Exit();
                resume();
                break;
            }

        }
    }
}
