package patterns.structural.decorator;

// Decorador concreto: Encriptación
public class EncryptionDecorator extends DataSourceDecorator {
    public EncryptionDecorator(DataSource source) {
        super(source);
    }

    @Override
    public void writeData(String data) {
        String encrypted = "[ENCRYPTED]" + data;
        System.out.println("   [EncryptionDecorator] Aplicando capa de cifrado sobre los datos.");
        super.writeData(encrypted);
    }
}
