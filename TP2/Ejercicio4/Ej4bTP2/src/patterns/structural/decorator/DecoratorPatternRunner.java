package patterns.structural.decorator;

import patterns.PatternCategory;
import patterns.PatternRunner;

public class DecoratorPatternRunner implements PatternRunner {

    @Override
    public PatternCategory getCategory() {
        return PatternCategory.ESTRUCTURAL;
    }

    @Override
    public String getName() {
        return "Decorator";
    }

    @Override
    public String getDescription() {
        return "Añade responsabilidades y comportamientos adicionales a un objeto dinámicamente envolviéndolo sin modificar su estructura original.";
    }

    @Override
    public void run() {
        System.out.println("==========================================================");
        System.out.println(" [CATEGORÍA: " + getCategory().getDisplayName().toUpperCase() + "]");
        System.out.println(" Patrón: " + getName());
        System.out.println(" Propósito: " + getDescription());
        System.out.println("==========================================================");
        System.out.println(">> Naturaleza del patrón en acción:");
        System.out.println("   A través de la composición recursiva ('envoltorios' o wrappers),");
        System.out.println("   podemos combinar múltiples funcionalidades (cifrado + compresión)");
        System.out.println("   en tiempo de ejecución sobre el mismo componente base 'DataSource'.\n");

        String sensitiveData = "Saldo: $500,000 USD | Token: ABC-9988";

        System.out.println("1. Caso Base: Escritura directa en archivo plano");
        DataSource plainSource = new FileDataSource();
        plainSource.writeData(sensitiveData);

        System.out.println("\n2. Decorando con Encriptación:");
        DataSource encryptedSource = new EncryptionDecorator(new FileDataSource());
        encryptedSource.writeData(sensitiveData);

        System.out.println("\n3. Encadenando múltiples decoradores (Compresión + Encriptación):");
        DataSource fullyDecorated = new CompressionDecorator(
                                        new EncryptionDecorator(
                                            new FileDataSource()
                                        )
                                    );
        fullyDecorated.writeData(sensitiveData);

        System.out.println("==========================================================");
    }
}
