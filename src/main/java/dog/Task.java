package dog;


public class Task {
    public static final String TYPE_TODO = "T";
    public static final String TYPE_DEADLINE = "D";
    public static final String TYPE_EVENT = "E";

    private final String description;
    private final String type;
    private final String by;
    private final String from;
    private final String to;
    private boolean isDone;

    public Task(String description) {
        this(description, TYPE_TODO, "", "", "");
    }

    public Task(String description, String type, String by, String from, String to) {
        this.description = description;
        this.type = type;
        this.by = by;
        this.from = from;
        this.to = to;
        this.isDone = false;
    }


    public void markAsDone() {
        isDone = true;
    }


    public void markAsNotDone() {
        isDone = false;
    }

    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    @Override
    public String toString() {
        String result = "[" + type + "][" + getStatusIcon() + "] " + description;
        if (TYPE_DEADLINE.equals(type)) {
            return result + " (by: " + by + ")";
        }
        if (TYPE_EVENT.equals(type)) {
            return result + " (from: " + from + " to: " + to + ")";
        }
        return result;
    }
}
