package is.shapes.model;

import is.interpreter.singleton.IDGenerator;
import is.interpreter.singleton.ObjectRegister;
import is.visitor.GraphicObjectVisitor;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.UUID;

import javax.swing.ImageIcon;

public final class ImageObject extends AbstractGraphicObject {

	private int id;

	private double factor = 1.0;

	private final Image image;

	private Point2D position;

	public Image getImage() {
		return image;
	}

	public ImageObject(ImageIcon img, Point2D pos) {
		this.id= IDGenerator.getInstance().generateID();
		ObjectRegister.getInstance().addObject(this); // Aggiungi al registro
		position = new Point2D.Double(pos.getX(), pos.getY());
		image = img.getImage();
	}

	@Override
	public boolean contains(Point2D p) {
		double w = (factor * image.getWidth(null)) / 2;
		double h = (factor * image.getHeight(null)) / 2;
		double dx = Math.abs(p.getX() - position.getX());
		double dy = Math.abs(p.getY() - position.getY());
		return dx <= w && dy <= h;
	}

	@Override
	public void moveTo(Point2D p) {
		position.setLocation(p);
		notifyListeners(new GraphicEvent(this));
	}

	@Override
	public ImageObject clone() {
		ImageObject cloned = (ImageObject) super.clone();
		cloned.position = (Point2D) position.clone();
		return cloned;

	}

	@Override
	public Point2D getPosition() {

		return new Point2D.Double(position.getX(), position.getY());
	}

	@Override
	public void scale(double factor) {
		if (factor <= 0)
			throw new IllegalArgumentException();
		this.factor *= factor;
		notifyListeners(new GraphicEvent(this));
	}

	@Override
	public Dimension2D getDimension() {
		Dimension dim = new Dimension();
		dim.setSize(factor * image.getWidth(null),
				factor * image.getHeight(null));
		return dim;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see is.shapes.GraphicObject#getType()
	 */
	@Override
	public String getType() {

		return "Image";
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public void accept(GraphicObjectVisitor visitor) {
		visitor.visit(this);
	}
	@Override
	public String toString() {
		return "ImageObject{" +
				"id=" + getID() +
				", position=" + getPosition() +
				", width=" + getDimension().getWidth() +
				", height" + getDimension().getHeight()+
				'}';
	}

}
