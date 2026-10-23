package patterns;

public interface PatternRunner {
    PatternCategory getCategory();
    String getName();
    String getDescription();
    void run();
}
