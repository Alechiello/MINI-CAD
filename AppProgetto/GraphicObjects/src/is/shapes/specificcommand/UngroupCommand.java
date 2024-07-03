package is.shapes.specificcommand;

import is.command.Command;
import is.shapes.model.GraphicObject;
import is.shapes.model.GraphicObjectGroup;
import is.interpreter.singleton.ObjectRegister;

public class UngroupCommand implements Command {
    private final int groupId;
    private GraphicObjectGroup group;

    public UngroupCommand(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean doIt() {
        GraphicObject object = ObjectRegister.getInstance().getObject(groupId);
        if (object instanceof GraphicObjectGroup) {
            group = (GraphicObjectGroup) object;
            ObjectRegister.getInstance().removeObject(groupId);
            System.out.println("Sciolgo il gruppo con ID: " + groupId);
            return true;
        } else {
            System.out.println("Il gruppo con ID " + groupId + " non è stato trovato o non è un gruppo.");
            return false;
        }
    }

    @Override
    public boolean undoIt() {
        if (group != null) {
            ObjectRegister.getInstance().addObject(group);
            System.out.println("Ho riaggruppato il gruppo con ID: " + groupId);
            return true;
        }
        return false;
    }
}
