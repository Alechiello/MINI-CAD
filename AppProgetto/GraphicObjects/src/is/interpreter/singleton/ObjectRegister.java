package is.interpreter.singleton;

import is.shapes.model.GraphicObject;
import java.util.HashMap;
import java.util.Map;

public class ObjectRegister {
    private static ObjectRegister instance = null;
    private Map<Integer, GraphicObject> objectRegistry;

    private ObjectRegister() {
        objectRegistry = new HashMap<>();
    }

    public static synchronized ObjectRegister getInstance() {
        if (instance == null) {
            instance = new ObjectRegister();
        }
        return instance;
    }

    public void addObject(GraphicObject go) {
        objectRegistry.put(go.getID(), go);
    }

    public void removeObject(int id) {
        objectRegistry.remove(id);
    }

    public GraphicObject getObject(int id) {
        return objectRegistry.get(id);
    }

    public Map<Integer, GraphicObject> getRegistry() {
        return objectRegistry;
    }

    public void reset() {
        objectRegistry.clear();
        IDGenerator.getInstance().reset();
    }

    public int getMaxId() {
        return IDGenerator.getInstance().getCounter();
    }
}
