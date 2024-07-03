package is.shapes.specificcommand;

import is.command.Command;
import is.interpreter.singleton.ObjectRegister;
import is.shapes.model.GraphicObject;
import is.visitor.AreaCalculatorVisitor;

public class CalculateAreaCommand implements Command {
    private final String param;
    private double areaResult;

    public CalculateAreaCommand(String param) {
        this.param = param;
    }

    @Override
    public boolean doIt() {
        System.out.println("Parametro ricevuto: " + param);
        if (param.equals("all")) {
            areaResult = calculateTotalArea();
        } else {
            try {
                int id = Integer.parseInt(param);
                areaResult = calculateAreaById(id);
            } catch (NumberFormatException e) {
                areaResult = calculateAreaByType(param);
            }
        }
        System.out.println("Area calcolata: " + areaResult);
        return true;
    }


    @Override
    public boolean undoIt() {
        // Questo comando non ha un'azione di undo
        return false;
    }

    private double calculateTotalArea() {
        double totalArea = 0.0;
        for (GraphicObject go : ObjectRegister.getInstance().getRegistry().values()) {
            AreaCalculatorVisitor visitor = new AreaCalculatorVisitor();
            go.accept(visitor);
            totalArea += visitor.getTotalArea();
        }
        return totalArea;
    }

    private double calculateAreaById(int id) {
        GraphicObject object = ObjectRegister.getInstance().getObject(id);
        System.out.println("Calcolo l'area dell'oggetto "+object+" con id "+id);
        if (object != null) {
            AreaCalculatorVisitor visitor = new AreaCalculatorVisitor();
            object.accept(visitor);
            return visitor.getTotalArea();
        } else {
            System.out.println("Oggetto con id " + id + " non trovato.");
            return 0.0;
        }
    }

    private double calculateAreaByType(String type) {
        double totalArea = 0.0;
        for (GraphicObject object : ObjectRegister.getInstance().getRegistry().values()) {
            if (object.getType().equalsIgnoreCase(type)) {
                AreaCalculatorVisitor visitor = new AreaCalculatorVisitor();
                object.accept(visitor);
                totalArea += visitor.getTotalArea();
            }
        }
        return totalArea;
    }

    public double getAreaResult() {
        return areaResult;
    }
}
