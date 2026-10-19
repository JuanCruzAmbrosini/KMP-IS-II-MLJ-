package patterns;

public enum PatternCategory {
    CREACIONAL("Patrones Creacionales", 
            "Mecanismos de creación de objetos que aumentan la flexibilidad y reutilización del código existente."),
    ESTRUCTURAL("Patrones Estructurales", 
            "Explican cómo ensamblar objetos y clases en estructuras más grandes, a la vez que mantienen estas estructuras flexibles y eficientes."),
    COMPORTAMIENTO("Patrones de Comportamiento", 
            "Se encargan de una comunicación efectiva y la asignación de responsabilidades entre objetos.");

    private final String displayName;
    private final String description;

    PatternCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
