package is.tests;

import static org.junit.Assert.*;

import is.interpreter.singleton.ObjectRegister;
import is.shapes.model.*;
import is.shapes.specificcommand.CalculateAreaCommand;
import is.shapes.specificcommand.CalculatePerimeterCommand;
import is.shapes.specificcommand.CreateGroupCommand;
import is.shapes.specificcommand.MoveCommand;
import org.junit.Before;
import org.junit.Test;
import is.interpreter.parser.Parser;
import is.command.Command;
import is.shapes.view.GraphicObjectPanel;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ParserTest {

    private GraphicObjectPanel panel;
    private Parser parser;


    @Test
    public void testParseCreateCircleCommand() throws Exception {
        String commandString = "new circle (5.0) (3.1,4.5)";
        parser = new Parser(new StringReader(commandString), panel);
        Command command = parser.parseCommand();
        assertNotNull(command);
        assertTrue(command.doIt());
        Point2D position = new Point2D.Double(3.1, 4.5);
        GraphicObject go = panel.getGraphicObjectAt(position);
        assertNotNull(go);
    }


    @Test
    public void testParseDeleteCommand() throws Exception {
        String commandString = "del 1";
        parser = new Parser(new StringReader(commandString), panel);
        Command command = parser.parseCommand();
        assertNotNull(command);
        assertTrue(command.doIt());
        assertNull(panel.getGraphicObjectAt(new Point2D.Double(3.1, 4.5)));
    }

    @Test
    public void testParseMoveCommand() throws IOException {
        ObjectRegister.getInstance().reset();
        // Simulazione di un Reader per la stringa di comando
        String commandString = "mv 1 1.0 1.0";
        Reader reader = new StringReader(commandString);

        // Crea il parser con il Reader e il pannello
        Parser parser = new Parser(reader, panel);

        // Crea e aggiungi un oggetto al pannello e al registro
        CircleObject circle = new CircleObject(new Point2D.Double(3.1, 4.5), 5.0);
        panel.add(circle);
        ObjectRegister.getInstance().addObject(circle);

        // Esegue il parsing del comando
        Command command = parser.parseCommand();

        // Verifica che il comando sia corretto
        assertNotNull(command);
        assertTrue(command instanceof MoveCommand);

        // Esegui il comando
        boolean result = command.doIt();
        assertTrue(result);

        // Verifica la nuova posizione

        assertEquals(new Point2D.Double(1.0, 1.0), circle.getPosition());

        // Verifica che l'oggetto sia effettivamente nella nuova posizione nel pannello
        GraphicObject foundObject = panel.getGraphicObjectAt(new Point2D.Double(1.0, 1.0));
        assertNotNull(foundObject);
        assertEquals(circle, foundObject);
    }


    @Test
    public void testParseScaleCommand() throws Exception {
        // Reset dell'ObjectRegister e preparazione del pannello
        ObjectRegister.getInstance().reset();
        panel = new GraphicObjectPanel();
        parser = new Parser(new StringReader(""), panel);

        // Creazione e aggiunta di un oggetto di prova
        CircleObject circle = new CircleObject(new Point2D.Double(1.0, 1.0), 1.0);
        ObjectRegister.getInstance().addObject(circle);
        panel.add(circle);

        // Assicurarsi che l'oggetto sia stato aggiunto correttamente
        assertNotNull(panel.getGraphicObjectAt(new Point2D.Double(1.0, 1.0)));
        assertEquals(1.0, circle.getRadius(), 0.01);

        // Stringa di comando per scalare l'oggetto
        String commandString = "scale " + circle.getID() + " 2.0";
        parser = new Parser(new StringReader(commandString), panel);
        Command command = parser.parseCommand();
        assertNotNull(command);

        // Aggiunta del debug
        System.out.println("Scaling object with ID: " + circle.getID() + " by a factor of 2.0");

        // Esecuzione del comando di scala
        assertTrue(command.doIt());

        // Verifica che l'oggetto sia stato scalato correttamente
        double expectedRadius = 2.0; // Il raggio atteso dopo la scala
        double actualRadius = circle.getRadius();
        System.out.println("Expected radius: " + expectedRadius);
        System.out.println("Actual radius: " + actualRadius);
        assertEquals(expectedRadius, actualRadius, 0.01);
    }


    @Test
    public void testParseListCommand() throws Exception {
        // Reset dell'ObjectRegister e preparazione del pannello
        ObjectRegister.getInstance().reset();
        panel = new GraphicObjectPanel();
        parser = new Parser(new StringReader(""), panel);

        // Creazione e aggiunta di alcuni oggetti di prova
        CircleObject circle = new CircleObject(new Point2D.Double(1.0, 1.0), 1.0);
        RectangleObject rectangle = new RectangleObject(new Point2D.Double(2.0, 2.0), 2.0, 3.0);
        ImageObject image = new ImageObject(new ImageIcon(), new Point2D.Double(3.0, 3.0));

        ObjectRegister.getInstance().addObject(circle);
        ObjectRegister.getInstance().addObject(rectangle);
        ObjectRegister.getInstance().addObject(image);
        panel.add(circle);
        panel.add(rectangle);
        panel.add(image);

        // Assicurarsi che gli oggetti siano stati aggiunti correttamente
        assertNotNull(panel.getGraphicObjectAt(new Point2D.Double(1.0, 1.0)));
        assertNotNull(panel.getGraphicObjectAt(new Point2D.Double(2.0, 2.0)));
        assertNotNull(panel.getGraphicObjectAt(new Point2D.Double(3.0, 3.0)));

        // Test ls all
        String commandString = "ls all";
        parser = new Parser(new StringReader(commandString), panel);
        Command command = parser.parseCommand();
        assertNotNull(command);
        System.out.println("Elenco qualunque oggetto: ");
        assertTrue(command.doIt());
        for (GraphicObject obj : ObjectRegister.getInstance().getRegistry().values()) {
            System.out.println("ID oggetto: " + obj.getID() + ", Tipo: " + obj.getType());
        }

        // Test ls with specific ID
        int testId = circle.getID();
        commandString = "ls " + testId;

        parser = new Parser(new StringReader(commandString), panel);
        command = parser.parseCommand();
        assertNotNull(command);
        System.out.println("L'oggetto con ID: " + testId);
        assertTrue(command.doIt());

        // Test ls with type "circle"
        commandString = "ls circle";
        parser = new Parser(new StringReader(commandString), panel);
        command = parser.parseCommand();
        assertNotNull(command);
        System.out.println("Elenco degli oggetti di tipo cerchio: ");
        assertTrue(command.doIt());
        for (GraphicObject obj : ObjectRegister.getInstance().getRegistry().values()) {
            if (obj.getType().equals("circle")) {
                System.out.println("Object ID: " + obj.getID() + ", Type: " + obj.getType());
            }
        }

        // Test ls groups
        // Create a group for testing
        List<Integer> ids = new ArrayList<>();
        ids.add(circle.getID());
        ids.add(rectangle.getID());
        CreateGroupCommand createGroupCommand = new CreateGroupCommand(ids);
        createGroupCommand.doIt();

        commandString = "ls groups";
        parser = new Parser(new StringReader(commandString), panel);
        command = parser.parseCommand();
        assertNotNull(command);
        System.out.println("Elenco i gruppi: ");
        assertTrue(command.doIt());
        for (GraphicObject obj : ObjectRegister.getInstance().getRegistry().values()) {
            if (obj instanceof GraphicObjectGroup) {
                System.out.println("Group ID: " + obj.getID());
            }
        }
    }



    @Test
    public void testParseGroupCommand() throws Exception {
        ObjectRegister.getInstance().reset();
        panel = new GraphicObjectPanel();

        // Creazione di nuovi oggetti da raggruppare
        CircleObject circle1 = new CircleObject(new Point2D.Double(1.0, 1.0), 1.0);
        CircleObject circle2 = new CircleObject(new Point2D.Double(2.0, 2.0), 1.0);
        CircleObject circle3 = new CircleObject(new Point2D.Double(3.0, 3.0), 1.0);

        // Aggiunta degli oggetti al registro e al pannello
        ObjectRegister.getInstance().addObject(circle1);
        ObjectRegister.getInstance().addObject(circle2);
        ObjectRegister.getInstance().addObject(circle3);
        panel.add(circle1);
        panel.add(circle2);
        panel.add(circle3);


        // Assicurarsi che gli oggetti siano stati aggiunti correttamente
        assertNotNull(panel.getGraphicObjectAt(new Point2D.Double(1.0, 1.0)));
        assertNotNull(panel.getGraphicObjectAt(new Point2D.Double(2.0, 2.0)));
        assertNotNull(panel.getGraphicObjectAt(new Point2D.Double(3.0, 3.0)));

        // Stringa di comando per raggruppare gli oggetti
        String commandString = "grp 1 2 3";
        parser = new Parser(new StringReader(commandString), panel);
        Command command = parser.parseCommand();
        assertNotNull(command);

        // Esecuzione del comando di raggruppamento
        assertTrue(command.doIt());


        // Verifica che il gruppo sia stato creato
        // Ottieni il nuovo ID del gruppo
        int newGroupID = 0;
        for (int id = 1; id <= ObjectRegister.getInstance().getMaxId(); id++) {
            GraphicObject obj = ObjectRegister.getInstance().getObject(id);
            if (obj instanceof GraphicObjectGroup) {
                newGroupID = id;
                break;
            }
        }
        assertTrue(newGroupID != 0);

        GraphicObject group = ObjectRegister.getInstance().getObject(newGroupID);
        assertNotNull(group);

        assertTrue(group instanceof GraphicObjectGroup);


        GraphicObjectGroup groupObj = (GraphicObjectGroup) group;


        assertTrue(groupObj.getObjects().contains(circle1));
        assertTrue(groupObj.getObjects().contains(circle2));
        assertTrue(groupObj.getObjects().contains(circle3));
    }


    @Test
    public void testParseUngroupCommand() throws Exception {
        ObjectRegister.getInstance().reset();
        panel = new GraphicObjectPanel();
        parser = new Parser(new StringReader(""), panel);

        // Creazione di nuovi oggetti da raggruppare
        CircleObject circle1 = new CircleObject(new Point2D.Double(1.0, 1.0), 1.0);
        CircleObject circle2 = new CircleObject(new Point2D.Double(2.0, 2.0), 1.0);
        CircleObject circle3 = new CircleObject(new Point2D.Double(3.0, 3.0), 1.0);

        // Aggiunta degli oggetti al registro e al pannello
        ObjectRegister.getInstance().addObject(circle1);
        ObjectRegister.getInstance().addObject(circle2);
        ObjectRegister.getInstance().addObject(circle3);
        panel.add(circle1);
        panel.add(circle2);
        panel.add(circle3);

        // Stringa di comando per raggruppare gli oggetti
        String groupCommandString = "grp " + circle1.getID() + " " + circle2.getID() + " " + circle3.getID();
        parser = new Parser(new StringReader(groupCommandString), panel);
        Command groupCommand = parser.parseCommand();
        assertNotNull(groupCommand);

        // Esecuzione del comando di raggruppamento
        assertTrue(groupCommand.doIt());

        // Verifica che il gruppo sia stato creato
        GraphicObject group = ObjectRegister.getInstance().getObject(4); // Assume che l'ID del gruppo sia 4
        assertNotNull(group);
        assertTrue(group instanceof GraphicObjectGroup);

        // Stringa di comando per scomporre il gruppo
        String ungroupCommandString = "ungrp " + group.getID();
        parser = new Parser(new StringReader(ungroupCommandString), panel);
        Command ungroupCommand = parser.parseCommand();
        assertNotNull(ungroupCommand);

        // Esecuzione del comando di scomposizione
        assertTrue(ungroupCommand.doIt());

        // Verifica che il gruppo sia stato rimosso
        GraphicObject ungrouped = ObjectRegister.getInstance().getObject(4);
        assertNull(ungrouped);

        // Verifica che gli oggetti originali siano ancora presenti nel registro
        assertNotNull(ObjectRegister.getInstance().getObject(circle1.getID()));
        assertNotNull(ObjectRegister.getInstance().getObject(circle2.getID()));
        assertNotNull(ObjectRegister.getInstance().getObject(circle3.getID()));
    }


    @Test
    public void testParseAreaCommand() throws Exception {
        ObjectRegister.getInstance().reset();
        panel = new GraphicObjectPanel();
        parser = new Parser(new StringReader(""), panel);

        // Creazione di nuovi oggetti da testare
        CircleObject circle1 = new CircleObject(new Point2D.Double(1.0, 1.0), 1.0);
        CircleObject circle2 = new CircleObject(new Point2D.Double(2.0, 2.0), 1.0);
        RectangleObject rectangle = new RectangleObject(new Point2D.Double(3.0, 3.0), 2.0, 3.0);

        // Aggiunta degli oggetti al registro e al pannello
        ObjectRegister.getInstance().addObject(circle1);
        ObjectRegister.getInstance().addObject(circle2);
        ObjectRegister.getInstance().addObject(rectangle);
        panel.add(circle1);
        panel.add(circle2);
        panel.add(rectangle);

        // Comando per calcolare l'area di tutti gli oggetti
        String commandString = "area all";
        parser = new Parser(new StringReader(commandString), panel);
        Command command = parser.parseCommand();
        assertNotNull(command);
        assertTrue(command.doIt());

        // Verifica dell'area calcolata per tutti gli oggetti
        if (command instanceof CalculateAreaCommand) {
            CalculateAreaCommand areaCommand = (CalculateAreaCommand) command;
            double expectedArea = Math.PI * 1.0 * 1.0 * 2 + 2.0 * 3.0; // Area dei due cerchi + area del rettangolo
            assertEquals(expectedArea, areaCommand.getAreaResult(), 0.01);
        } else {
            System.out.println("Il comando non è un'istanza di CalculateAreaCommand");
        }

        // Comando per calcolare l'area di un singolo oggetto per ID
        commandString = "area " + circle1.getID();

        parser = new Parser(new StringReader(commandString), panel);
        Command command2 = parser.parseCommand();
        assertNotNull(command2);
        assertTrue(command2.doIt());

        // Verifica dell'area calcolata per un singolo oggetto
        if (command2 instanceof CalculateAreaCommand) {
            CalculateAreaCommand areaCommand2 = (CalculateAreaCommand) command2;
            double expectedArea = Math.PI * 1.0 * 1.0; // Area del primo cerchio
            assertEquals(expectedArea, areaCommand2.getAreaResult(), 0.01);
        } else {
            System.out.println("Il comando non è un'istanza di CalculateAreaCommand");
        }

        // Comando per calcolare l'area di tutti gli oggetti di tipo "circle"
        commandString = "area circle";
        parser = new Parser(new StringReader(commandString), panel);
        Command command3 = parser.parseCommand();
        assertNotNull(command3);
        assertTrue(command3.doIt());

        // Verifica dell'area calcolata per tutti gli oggetti di tipo "circle"
        if (command3 instanceof CalculateAreaCommand) {
            CalculateAreaCommand areaCommand3 = (CalculateAreaCommand) command3;
            double expectedArea = Math.PI * 1.0 * 1.0 * 2; // Area dei due cerchi
            assertEquals(expectedArea, areaCommand3.getAreaResult(), 0.01);
        } else {
            System.out.println("Il comando non è un'istanza di CalculateAreaCommand");
        }
    }


    @Test
    public void testParsePerimeterCommand() throws Exception {
        ObjectRegister.getInstance().reset();
        panel = new GraphicObjectPanel();
        parser = new Parser(new StringReader(""), panel);

        // Creazione di nuovi oggetti da testare
        CircleObject circle1 = new CircleObject(new Point2D.Double(1.0, 1.0), 1.0);
        CircleObject circle2 = new CircleObject(new Point2D.Double(2.0, 2.0), 1.0);
        RectangleObject rectangle = new RectangleObject(new Point2D.Double(3.0, 3.0), 2.0, 3.0);

        // Aggiunta degli oggetti al registro e al pannello
        ObjectRegister.getInstance().addObject(circle1);
        ObjectRegister.getInstance().addObject(circle2);
        ObjectRegister.getInstance().addObject(rectangle);
        panel.add(circle1);
        panel.add(circle2);
        panel.add(rectangle);

        // Comando per calcolare il perimetro di tutti gli oggetti
        String commandString = "perimeter all";
        parser = new Parser(new StringReader(commandString), panel);
        Command command = parser.parseCommand();
        assertNotNull(command);
        assertTrue(command.doIt());

        // Verifica del perimetro calcolato per tutti gli oggetti
        if (command instanceof CalculatePerimeterCommand) {
            CalculatePerimeterCommand perimeterCommand = (CalculatePerimeterCommand) command;
            double expectedPerimeter = 2 * Math.PI * 1.0 * 2 + 2 * (2.0 + 3.0); // Perimetro dei due cerchi + perimetro del rettangolo
            assertEquals(expectedPerimeter, perimeterCommand.getPerimeterResult(), 0.01);
        } else {
            fail("Il comando non è un'istanza di CalculatePerimeterCommand");
        }

        // Comando per calcolare il perimetro di un singolo oggetto per ID
        commandString = "perimeter " + circle1.getID();
        parser = new Parser(new StringReader(commandString), panel);
        command = parser.parseCommand();
        assertNotNull(command);
        assertTrue(command.doIt());

        // Verifica del perimetro calcolato per un singolo oggetto
        if (command instanceof CalculatePerimeterCommand) {
            CalculatePerimeterCommand perimeterCommand = (CalculatePerimeterCommand) command;
            double expectedPerimeter = 2 * Math.PI * 1.0; // Perimetro del primo cerchio
            assertEquals(expectedPerimeter, perimeterCommand.getPerimeterResult(), 0.01);
        } else {
            fail("Il comando non è un'istanza di CalculatePerimeterCommand");
        }

        // Comando per calcolare il perimetro di tutti gli oggetti di tipo "circle"
        commandString = "perimeter circle";
        parser = new Parser(new StringReader(commandString), panel);
        command = parser.parseCommand();
        assertNotNull(command);
        assertTrue(command.doIt());

        // Verifica del perimetro calcolato per tutti gli oggetti di tipo "circle"
        if (command instanceof CalculatePerimeterCommand) {
            CalculatePerimeterCommand perimeterCommand = (CalculatePerimeterCommand) command;
            double expectedPerimeter = 2 * Math.PI * 1.0 * 2; // Perimetro dei due cerchi
            assertEquals(expectedPerimeter, perimeterCommand.getPerimeterResult(), 0.01);
        } else {
            fail("Il comando non è un'istanza di CalculatePerimeterCommand");
        }
    }
}
