package patterns.behavioral.strategy;

public class FastestRouteStrategy implements RouteStrategy {
    @Override
    public void calculateRoute(String origin, String destination) {
        System.out.println("   [FastestRouteStrategy] Calculando ruta más rápida de [" 
                + origin + "] a [" + destination + "] considerando tráfico en tiempo real y autopistas...");
    }
}
