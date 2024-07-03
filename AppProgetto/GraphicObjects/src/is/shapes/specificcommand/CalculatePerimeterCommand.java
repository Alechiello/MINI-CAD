package is.shapes.specificcommand;

import is.command.Command;
import is.interpreter.singleton.ObjectRegister;
import is.shapes.model.GraphicObject;
import is.visitor.PerimeterCalculatorVisitor;

import java.util.Map;

public class CalculatePerimeterCommand implements Command {
    private final String parameter;
    private double perimeterResult;

    public CalculatePerimeterCommand(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public boolean doIt() {
        PerimeterCalculatorVisitor visitor = new PerimeterCalculatorVisitor();

        if (parameter.equals("all")) {
            // Calcola il perimetro di tutti gli oggetti
            for (GraphicObject object : ObjectRegister.getInstance().getRegistry().values()) {
                object.accept(visitor);
            }
            perimeterResult = visitor.getTotalPerimeter();
            System.out.println("Total perimeter of all objects: " + perimeterResult);
            return true;
        } else if (isNumeric(parameter)) {
            // Calcola il perimetro di un oggetto specifico per ID
            int id = Integer.parseInt(parameter);
            GraphicObject object = ObjectRegister.getInstance().getObject(id);
            if (object != null) {
                object.accept(visitor);
                perimeterResult = visitor.getTotalPerimeter();
                System.out.println("Il perimetro dell'oggetto con id " + id + "è " + perimeterResult);
                return true;
            } else {
                System.out.println("Oggetto " + id + " non trovato.");
                return false;
            }
        } else {
            // Calcola il perimetro di tutti gli oggetti di un tipo specifico
            String type = parameter;
            for (GraphicObject object : ObjectRegister.getInstance().getRegistry().values()) {
                if (object.getType().equalsIgnoreCase(type)) {
                    object.accept(visitor);
                }
            }
            perimeterResult = visitor.getTotalPerimeter();
            System.out.println("Total perimeter of all objects of type " + type + ": " + perimeterResult);
            return true;
        }
    }

    @Override
    public boolean undoIt() {
        // Questo comando non ha un'azione di undo
        return false;
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public double getPerimeterResult() {
        return perimeterResult;
    }
}
