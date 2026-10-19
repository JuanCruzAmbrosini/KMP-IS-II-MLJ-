package patterns.behavioral.strategy;

public class ScenicRouteStrategy implements RouteStrategy {
    @Override
    public void calculateRoute(String origin, String destination) {
        System.out.println("   [ScenicRouteStrategy] Calculando ruta panorámica turística de [" 
                + origin + "] a [" + destination + "] priorizando paisajes, miradores y caminos costeros...");
    }
}
