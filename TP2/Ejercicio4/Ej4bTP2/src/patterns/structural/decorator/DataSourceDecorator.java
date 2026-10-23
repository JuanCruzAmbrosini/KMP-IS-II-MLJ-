package patterns.structural.decorator;

// Decorador base (Base Decorator)
public abstract class DataSourceDecorator implements DataSource {
    protected final DataSource wrappee;

    public DataSourceDecorator(DataSource source) {
        this.wrappee = source;
    }

    @Override
    public void writeData(String data) {
        wrappee.writeData(data);
    }
}
