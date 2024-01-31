package swiftcards.core.ui.cli.scenery;

public interface Stage<T> {
    void setStageData(T stageData);
    Stage<?> goToStage();
}
