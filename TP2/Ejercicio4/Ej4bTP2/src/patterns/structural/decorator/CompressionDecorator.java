package patterns.structural.decorator;

// Decorador concreto adicional: Compresión
public class CompressionDecorator extends DataSourceDecorator {
    public CompressionDecorator(DataSource source) {
        super(source);
    }

    @Override
    public void writeData(String data) {
        String compressed = "[COMPRESSED]" + data;
        System.out.println("   [CompressionDecorator] Aplicando capa de compresión sobre los datos.");
        super.writeData(compressed);
    }
}
