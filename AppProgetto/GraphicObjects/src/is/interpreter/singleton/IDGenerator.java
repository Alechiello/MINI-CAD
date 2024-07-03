package is.interpreter.singleton;

public class IDGenerator {
    private static IDGenerator instance = null;
    private int counter = 0;

    private IDGenerator() {
        // Costruttore privato per prevenire l'istanza esterna
    }

    public static synchronized IDGenerator getInstance() {
        if (instance == null) {
            instance = new IDGenerator();
        }
        return instance;
    }

    public synchronized int generateID() {
        return ++counter;
    }

    public synchronized void reset() {
        counter = 0;
    }

    public int getCounter() {
        return counter;
    }
}
