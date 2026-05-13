import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class CommandInvoker {
    private final Deque<AppCommand> undoStack;
    private final Deque<AppCommand> redoStack;

    public CommandInvoker() {
        this.undoStack = new ArrayDeque<>();
        this.redoStack = new ArrayDeque<>();
    }

    public void executeCommand(AppCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    public String undo() {
        if (undoStack.isEmpty()) {
            return null;
        }

        AppCommand command = undoStack.pop();
        command.undo();
        redoStack.push(command);

        return command.getName();
    }

    public String redo() {
        if (redoStack.isEmpty()) {
            return null;
        }

        AppCommand command = redoStack.pop();
        command.execute();
        undoStack.push(command);

        return command.getName();
    }

    public List<String> getCommandHistoryNames() {
        List<String> names = new ArrayList<>();

        for (AppCommand command : undoStack) {
            names.add(command.getName());
        }

        return names;
    }

    public int getHistorySize() {
        return undoStack.size();
    }

    public int getRedoSize() {
        return redoStack.size();
    }
}