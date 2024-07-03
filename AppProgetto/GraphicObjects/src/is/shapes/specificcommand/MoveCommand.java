package is.shapes.specificcommand;

import is.command.Command;
import is.shapes.model.GraphicObject;
import java.awt.geom.Point2D;

public class MoveCommand implements Command {

	private final Point2D oldPos;
	private final Point2D newPos;
	private final GraphicObject object;

	// Costruttore per spostare l'oggetto a una nuova posizione
	public MoveCommand(GraphicObject go, Point2D pos) {

		oldPos = go.getPosition();
		newPos = pos;
		this.object = go;
	}

	// Costruttore per spostare l'oggetto di un offset
	public MoveCommand(GraphicObject go, double offsetX, double offsetY) {

		oldPos = go.getPosition();
		newPos = new Point2D.Double(oldPos.getX() + offsetX, oldPos.getY() + offsetY);
		this.object = go;
	}

	@Override
	public boolean doIt() {
		object.moveTo(newPos);
		return true;
	}

	@Override
	public boolean undoIt() {

		object.moveTo(oldPos);
		return true;
	}
}

