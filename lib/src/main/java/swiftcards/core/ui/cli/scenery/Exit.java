package swiftcards.core.ui.cli.scenery;

public class Exit implements Stage<Void> {

    @Override
    public void setStageData(Void stageData) { }

    @Override
    public Stage<?> goToStage() {
        System.exit(0);
        return null;
    }
}
