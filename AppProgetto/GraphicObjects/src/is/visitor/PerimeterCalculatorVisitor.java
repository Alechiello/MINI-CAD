package is.visitor;

import is.shapes.model.*;

import java.util.HashSet;
import java.util.Set;

public class PerimeterCalculatorVisitor implements GraphicObjectVisitor {
    private double totalPerimeter;
    private final Set<Integer> visitedObjects;

    public PerimeterCalculatorVisitor() {
        this.totalPerimeter = 0;
        this.visitedObjects = new HashSet<>();
    }

    @Override
    public void visit(CircleObject circle) {
        if (!visitedObjects.contains(circle.getID())) {
            double radius = circle.getRadius();
            totalPerimeter += 2 * Math.PI * radius;
            visitedObjects.add(circle.getID());
        }
    }

    @Override
    public void visit(RectangleObject rectangle) {
        if (!visitedObjects.contains(rectangle.getID())) {
            double width = rectangle.getDimension().getWidth();
            double height = rectangle.getDimension().getHeight();
            totalPerimeter += 2 * (width + height);
            visitedObjects.add(rectangle.getID());
        }
    }

    @Override
    public void visit(ImageObject image) {
        // Se necessario, implementa il calcolo del perimetro per le immagini
    }

    @Override
    public void visit(GraphicObjectGroup group) {
        for (GraphicObject obj : group.getObjects()) {
            obj.accept(this);
        }
    }

    public double getTotalPerimeter() {
        return totalPerimeter;
    }
}
