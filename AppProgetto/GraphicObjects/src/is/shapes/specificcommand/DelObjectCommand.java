package is.shapes.specificcommand;

import is.command.Command;
import is.shapes.model.GraphicObject;
import is.shapes.view.GraphicObjectPanel;
import is.interpreter.singleton.ObjectRegister;

public class DelObjectCommand implements Command {
    private final GraphicObjectPanel panel;
    private final int id;
    private GraphicObject go;

    public DelObjectCommand(GraphicObjectPanel panel, int id) {
        this.panel = panel;
        this.id = id;
    }

    @Override
    public boolean doIt() {
        go = ObjectRegister.getInstance().getObject(id);
        if (go != null) {
            panel.remove(go);
            ObjectRegister.getInstance().removeObject(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean undoIt() {
        if (go != null) {
            panel.add(go);
            ObjectRegister.getInstance().addObject(go);
            return true;
        }
        return false;
    }
}

