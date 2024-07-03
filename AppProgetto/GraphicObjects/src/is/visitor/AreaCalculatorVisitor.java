package is.visitor;

import is.shapes.model.*;

import java.util.HashSet;
import java.util.Set;

public class AreaCalculatorVisitor implements GraphicObjectVisitor {
    private double totalArea;
    private final Set<Integer> visitedObjects;

    public AreaCalculatorVisitor() {
        this.totalArea = 0;
        this.visitedObjects = new HashSet<>();
    }

    @Override
    public void visit(CircleObject circle) {
        if (!visitedObjects.contains(circle.getID())) {
            double radius = circle.getRadius();
            totalArea += Math.PI * radius * radius;
            visitedObjects.add(circle.getID());
        }
    }

    @Override
    public void visit(RectangleObject rectangle) {
        if (!visitedObjects.contains(rectangle.getID())) {
            double width = rectangle.getDimension().getWidth();
            double height = rectangle.getDimension().getHeight();
            totalArea += width * height;
            visitedObjects.add(rectangle.getID());
        }
    }

    @Override
    public void visit(ImageObject image) {
        if (!visitedObjects.contains(image.getID())) {
            double width = image.getDimension().getWidth();
            double height = image.getDimension().getHeight();
            totalArea += width * height;
            visitedObjects.add(image.getID());
        }
    }

    @Override
    public void visit(GraphicObjectGroup group) {
        for (GraphicObject obj : group.getObjects()) {
            obj.accept(this);
        }
    }

    public double getTotalArea() {
        return totalArea;
    }
}

