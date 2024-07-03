package is.visitor;

import is.shapes.model.CircleObject;
import is.shapes.model.GraphicObjectGroup;
import is.shapes.model.ImageObject;
import is.shapes.model.RectangleObject;

public interface GraphicObjectVisitor {
    void visit(CircleObject circle);
    void visit(RectangleObject rectangle);
    void visit(ImageObject imageObject);
    void visit(GraphicObjectGroup group);

}
