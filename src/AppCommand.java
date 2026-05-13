public interface AppCommand {
    void execute();

    void undo();

    String getName();
}