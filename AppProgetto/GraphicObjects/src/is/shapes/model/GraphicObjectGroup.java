package is.shapes.model;

import is.interpreter.singleton.IDGenerator;
import is.interpreter.singleton.ObjectRegister;
import is.visitor.GraphicObjectVisitor;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

public class GraphicObjectGroup extends AbstractGraphicObject {
    private final Set<GraphicObject> objects = new HashSet<>();
    private Point2D position;
    private int id;

    public GraphicObjectGroup() {
        this.id = IDGenerator.getInstance().generateID();
        ObjectRegister.getInstance().addObject(this); // Aggiungi al registro
        this.position = new Point2D.Double(0, 0);
    }

    public void addObject(GraphicObject object) {
        objects.add(object);
        notifyListeners(new GraphicEvent(this));
    }

    public Set<GraphicObject> getObjects() {
        return objects;
    }

    @Override
    public String getType() {
        return "Group";
    }

    @Override
    public void moveTo(Point2D newPosition) {
        for (GraphicObject obj : objects) {
            Point2D objPos = obj.getPosition();
            obj.moveTo(new Point2D.Double(objPos.getX() + (newPosition.getX() - position.getX()),
                    objPos.getY() + (newPosition.getY() - position.getY())));
        }
        this.position.setLocation(newPosition);
        notifyListeners(new GraphicEvent(this));
    }

    @Override
    public Point2D getPosition() {
        return position;
    }

    @Override
    public void scale(double factor) {
        for (GraphicObject obj : objects) {
            obj.scale(factor);
        }
        notifyListeners(new GraphicEvent(this));
    }

    @Override
    public Dimension2D getDimension() {
        if (objects.isEmpty()) {
            return new Dimension2D() {
                @Override
                public void setSize(double width, double height) {
                }

                @Override
                public double getWidth() {
                    return 0;
                }

                @Override
                public double getHeight() {
                    return 0;
                }
            };
        }
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
        for (GraphicObject obj : objects) {
            Point2D pos = obj.getPosition();
            Dimension2D dim = obj.getDimension();
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            maxX = Math.max(maxX, pos.getX() + dim.getWidth());
            maxY = Math.max(maxY, pos.getY() + dim.getHeight());
        }
        final double finalMinX = minX;
        final double finalMinY = minY;
        final double finalMaxX = maxX;
        final double finalMaxY = maxY;
        return new Dimension2D() {
            @Override
            public void setSize(double width, double height) {
            }

            @Override
            public double getWidth() {
                return finalMaxX - finalMinX;
            }

            @Override
            public double getHeight() {
                return finalMaxY - finalMinY;
            }
        };
    }

    @Override
    public boolean contains(Point2D point) {
        for (GraphicObject obj : objects) {
            if (obj.contains(point)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addGraphicObjectListener(GraphicObjectListener l) {
        for (GraphicObject obj : objects) {
            obj.addGraphicObjectListener(l);
        }
        super.addGraphicObjectListener(l);
    }

    @Override
    public void removeGraphicObjectListener(GraphicObjectListener l) {
        for (GraphicObject obj : objects) {
            obj.removeGraphicObjectListener(l);
        }
        super.removeGraphicObjectListener(l);
    }

    @Override
    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    @Override
    public void accept(GraphicObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GraphicObjectGroup{id=").append(getID()).append(", objects=[");
        for (GraphicObject obj : objects) {
            sb.append(obj.toString()).append(", ");
        }
        sb.append("]}");
        return sb.toString();
    }

}

