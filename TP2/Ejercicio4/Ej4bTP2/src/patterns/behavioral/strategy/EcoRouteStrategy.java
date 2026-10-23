package patterns.behavioral.strategy;

public class EcoRouteStrategy implements RouteStrategy {
    @Override
    public void calculateRoute(String origin, String destination) {
        System.out.println("   [EcoRouteStrategy] Calculando ruta ecológica de [" 
                + origin + "] a [" + destination + "] minimizando consumo de combustible y huella de carbono...");
    }
}
