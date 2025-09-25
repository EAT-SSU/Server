package ssu.eatssu.global.log.event;

public record LogEvent(String message) {
    public static LogEvent of(String message) {
        return new LogEvent(message);
    }
}
