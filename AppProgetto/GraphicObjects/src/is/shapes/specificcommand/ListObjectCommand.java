package is.shapes.specificcommand;

import is.command.Command;
import is.shapes.model.GraphicObject;
import is.shapes.model.GraphicObjectGroup;
import is.interpreter.singleton.ObjectRegister;

import java.util.Map;

public class ListObjectCommand implements Command {

    public enum ListType {
        SINGLE,
        TYPE,
        ALL,
        GROUPS
    }

    private final ListType listType;
    private final String parameter;

    public ListObjectCommand(ListType listType, String parameter) {
        this.listType = listType;
        this.parameter = parameter;
    }

    @Override
    public boolean doIt() {
        switch (listType) {
            case SINGLE:
                listSingleObject();
                break;
            case TYPE:
                listObjectsByType();
                break;
            case ALL:
                listAllObjects();
                break;
            case GROUPS:
                listAllGroups();
                break;
            default:
                System.out.println("Comando non riconosciuto.");
                return false;
        }
        return true;
    }

    @Override
    public boolean undoIt() {
        // Questo comando non ha un'azione di undo
        return false;
    }

    private void listSingleObject() {
        try {
            int id = Integer.parseInt(parameter);
            GraphicObject object = ObjectRegister.getInstance().getObject(id);
            if (object != null) {
                System.out.println("Proprietà dell'oggetto " + id + ": " + object);
            } else {
                System.out.println("L'oggetto con id " + id + " non è stato trovato.");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID non valido.");
        }
    }

    private void listObjectsByType() {
        String type = parameter;
        Map<Integer, GraphicObject> registry = ObjectRegister.getInstance().getRegistry();
        System.out.println("Elenco degli oggetti di tipo " + type + ":");
        for (GraphicObject go : registry.values()) {
            if (go.getType().equalsIgnoreCase(type)) {
                System.out.println(go);
            }
        }
    }

    private void listAllObjects() {
        Map<Integer, GraphicObject> registry = ObjectRegister.getInstance().getRegistry();
        System.out.println("Elenco di tutti gli oggetti:");
        for (GraphicObject go : registry.values()) {
            System.out.println(go+"\n");
        }
    }

    private void listAllGroups() {
        Map<Integer, GraphicObject> registry = ObjectRegister.getInstance().getRegistry();
        System.out.println("Elenco di tutti i gruppi:");
        for (GraphicObject go : registry.values()) {
            if (go instanceof GraphicObjectGroup) {
                GraphicObjectGroup group = (GraphicObjectGroup) go;
                System.out.println("Group ID: " + group.getID() + ", numero di oggetti: " + group.getObjects().size());
                for (GraphicObject obj : group.getObjects()) {
                    System.out.println(" - Oggetto " + obj.getID() + " di tipo: " + obj.getType());
                }
                System.out.println("\n");
            }
        }
    }
}
