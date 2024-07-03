package is.shapes.specificcommand;

import is.command.Command;
import is.shapes.model.GraphicObject;
import is.shapes.model.GraphicObjectGroup;
import is.interpreter.singleton.ObjectRegister;
import is.interpreter.singleton.IDGenerator;

import java.util.List;

public class CreateGroupCommand implements Command {
    private final List<Integer> objectIds;
    private GraphicObjectGroup group;

    public CreateGroupCommand(List<Integer> objectIds) {
        this.objectIds = objectIds;
    }

    @Override
    public boolean doIt() {
        group = new GraphicObjectGroup();
        for (int id : objectIds) {
            GraphicObject obj = ObjectRegister.getInstance().getObject(id);
            if (obj != null) {
                group.addObject(obj);
            } else {
                System.out.println("Oggetto con ID " + id + " non trovato.");
            }
        }
        System.out.println("Gruppo creato con ID: " + group.getID());
        return true;
    }

    @Override
    public boolean undoIt() {
        if (group != null) {
            ObjectRegister.getInstance().removeObject(group.getID());
            System.out.println("Removed group with ID: " + group.getID());
            return true;
        }
        return false;
    }
}
