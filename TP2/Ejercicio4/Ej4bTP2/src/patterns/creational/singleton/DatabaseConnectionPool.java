package patterns.creational.singleton;

public final class DatabaseConnectionPool {
    private static volatile DatabaseConnectionPool instance;
    private int activeConnections;

    private DatabaseConnectionPool() {
        // Inicialización de conexiones
        this.activeConnections = 10;
        System.out.println("-> Inicializando el Pool de Conexiones a la Base de Datos...");
    }

    public static DatabaseConnectionPool getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnectionPool.class) {
                if (instance == null) {
                    instance = new DatabaseConnectionPool();
                }
            }
        }
        return instance;
    }

    public void executeQuery(String query) {
        System.out.println("Ejecutando consulta [" + query + "] usando la instancia " + System.identityHashCode(this));
    }

    public int getActiveConnections() {
        return activeConnections;
    }
}
