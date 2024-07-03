package is.shapes.model;

import is.interpreter.singleton.IDGenerator;
import is.interpreter.singleton.ObjectRegister;
import is.visitor.GraphicObjectVisitor;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.UUID;

public final  class CircleObject extends AbstractGraphicObject {

	private int id;

	private Point2D position;

	private double radius;

	public CircleObject(Point2D pos, double r) {
		if (r <= 0)
			throw new IllegalArgumentException("Il raggio deve essere positivo");
		this.id= IDGenerator.getInstance().generateID();
		ObjectRegister.getInstance().addObject(this); // Aggiungi al registro
		position = new Point2D.Double(pos.getX(), pos.getY());
		radius = r;
	}



	@Override
	public void moveTo(Point2D p) {
		position.setLocation(p);
		notifyListeners(new GraphicEvent(this));
	}





	@Override
	public Point2D getPosition() {

		return new Point2D.Double(position.getX(), position.getY());
	}

	@Override
	public void scale(double factor) {
		if (factor <= 0)
			throw new IllegalArgumentException();
		radius *= factor;
		notifyListeners(new GraphicEvent(this));
	}

	@Override
	public Dimension2D getDimension() {
		Dimension d = new Dimension();
		d.setSize(2 * radius, 2 * radius);

		return d;
	}

	@Override
	public boolean contains(Point2D p) {
        return position.distance(p) <= radius;
	}



	@Override
	public CircleObject clone() {
		CircleObject cloned = (CircleObject) super.clone();
		cloned.position = (Point2D) position.clone();
		return cloned;
	}

	@Override
	public String getType() {

		return "Circle";
	}

	@Override
	public int getID() {
		return id;
	}

	public double getRadius() {
		return radius;
	}

	@Override
	public void accept(GraphicObjectVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "CircleObject{" +
				"id=" + getID() +
				", position=" + getPosition() +
				", radius=" + radius +
				'}';
	}

}
