package patterns.behavioral.strategy;

// Contexto
public class NavigatorContext {
    private RouteStrategy strategy;

    public NavigatorContext() {
    }

    public NavigatorContext(RouteStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(RouteStrategy strategy) {
        this.strategy = strategy;
    }

    public RouteStrategy getStrategy() {
        return strategy;
    }

    public void buildRoute(String origin, String destination) {
        if (this.strategy == null) {
            System.out.println("   [NavigatorContext] Error: no se ha configurado ninguna estrategia de ruta.");
            return;
        }
        this.strategy.calculateRoute(origin, destination);
    }
}
