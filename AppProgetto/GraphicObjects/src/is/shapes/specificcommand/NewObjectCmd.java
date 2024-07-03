package is.shapes.specificcommand;

import is.command.Command;
import is.interpreter.singleton.ObjectRegister;
import is.shapes.model.GraphicObject;
import is.shapes.view.GraphicObjectPanel;

public class NewObjectCmd implements Command {

	private final GraphicObjectPanel panel;
	private final GraphicObject go;
	private final Double x;
	private final Double y;

	// Costruttore per specificare la posizione
	public NewObjectCmd(GraphicObjectPanel panel, GraphicObject go, Double x, Double y) {
		this.panel = panel;
		this.go = go;
		this.x = x;
		this.y = y;
	}

	// Costruttore con posizione di default
	public NewObjectCmd(GraphicObjectPanel panel, GraphicObject go) {
		this(panel, go, 10.0, 10.0);
	}

	@Override
	public boolean doIt() {
		panel.add(go);
		return true;
	}

	@Override
	public boolean undoIt() {
		System.out.println("Rimuovo l'oggetto: " + go.getID());
		ObjectRegister.getInstance().removeObject(go.getID()); // Rimuovi dal registro
		panel.remove(go);
		return true;
	}
}
