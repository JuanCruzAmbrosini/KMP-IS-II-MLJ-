package patterns.structural.decorator;

public class FileDataSource implements DataSource {
    @Override
    public void writeData(String data) {
        System.out.println("   [FileDataSource] Escribiendo al archivo: " + data);
    }
}
