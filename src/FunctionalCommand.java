public class FunctionalCommand implements AppCommand {
    private final String name;
    private final Runnable action;
    private final Runnable undoAction;

    public FunctionalCommand(String name, Runnable action, Runnable undoAction) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Command name cannot be empty");
        }

        if (action == null) {
            throw new IllegalArgumentException("Command action cannot be null");
        }

        if (undoAction == null) {
            throw new IllegalArgumentException("Undo action cannot be null");
        }

        this.name = name;
        this.action = action;
        this.undoAction = undoAction;
    }

    @Override
    public void execute() {
        action.run();
    }

    @Override
    public void undo() {
        undoAction.run();
    }

    @Override
    public String getName() {
        return name;
    }
}