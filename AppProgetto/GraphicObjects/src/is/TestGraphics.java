package is;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.io.StringReader;

import javax.swing.*;

import is.command.Command;
import is.command.HistoryCommandHandler;
import is.interpreter.parser.Parser;
import is.shapes.controller.GraphicObjectController;
import is.shapes.model.CircleObject;
import is.shapes.model.GraphicObject;
import is.shapes.model.ImageObject;
import is.shapes.model.RectangleObject;
import is.shapes.view.*;

public class TestGraphics {

	public static void main(String[] args) {

		JFrame f = new JFrame();

		JToolBar toolbar = new JToolBar();
		JButton undoButt = new JButton("Undo");
		JButton redoButt = new JButton("Redo");

		final HistoryCommandHandler handler = new HistoryCommandHandler();

		undoButt.addActionListener(evt -> handler.undo());

		redoButt.addActionListener(evt -> handler.redo());
		toolbar.add(undoButt);
		toolbar.add(redoButt);

		GraphicObjectLogger logger = new GraphicObjectLogger();

		GraphicObjectPanel gpanel = new GraphicObjectPanel();

		gpanel.setPreferredSize(new Dimension(400, 400));
		GraphicObjectViewFactory.FACTORY.installView(RectangleObject.class, new RectangleObjectView());
		GraphicObjectViewFactory.FACTORY.installView(CircleObject.class, new CircleObjectView());
		GraphicObjectViewFactory.FACTORY.installView(ImageObject.class, new ImageObjectView());

		GraphicObject go = new RectangleObject(new Point(80, 80), 20, 50);
		gpanel.add(go);
		go.addGraphicObjectListener(logger);
		GraphicObjectController goc1 = new GraphicObjectController(go, handler);

		go = new CircleObject(new Point(100, 100), 10);
		gpanel.add(go);
		go.addGraphicObjectListener(logger);
		GraphicObjectController goc2 = new GraphicObjectController(go, handler);

		go = new ImageObject(new ImageIcon(TestGraphics.class.getResource("shapes/model/NyaNya.gif")),
				new Point(40, 87));
		gpanel.add(go);
		go.addGraphicObjectListener(logger);
		GraphicObjectController goc3 = new GraphicObjectController(go, handler);

		f.add(toolbar, BorderLayout.NORTH);
		f.add(gpanel, BorderLayout.CENTER);

		JPanel controlPanel = new JPanel(new FlowLayout());
		controlPanel.add(goc1);
		controlPanel.add(goc2);
		controlPanel.add(goc3);

		f.getContentPane().add(new JScrollPane(controlPanel), BorderLayout.SOUTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);


		// Add command input area
		JTextArea commandTextArea = new JTextArea(5, 40);
		JButton executeButton = new JButton("Execute Command");
		JTextArea resultTextArea = new JTextArea(10, 40);
		resultTextArea.setEditable(false);

		executeButton.addActionListener(evt -> {
			String commandString = commandTextArea.getText();
			resultTextArea.append("Executing: " + commandString + "\n");

			// Create the parser with the command string
			Parser parser = new Parser(new StringReader(commandString), gpanel);

			try {
				Command command = parser.parseCommand();
				if (command != null) {
					if (command.doIt()) {
						handler.handle(command);
						resultTextArea.append("Command executed successfully.\n");
					} else {
						resultTextArea.append("Command execution failed.\n");
					}
				} else {
					resultTextArea.append("Parsed command is null.\n");
				}
			} catch (Exception ex) {
				resultTextArea.append("Error: " + ex.getMessage() + "\n");
			}
		});

		JPanel inputPanel = new JPanel(new BorderLayout());
		inputPanel.add(new JScrollPane(commandTextArea), BorderLayout.CENTER);
		inputPanel.add(executeButton, BorderLayout.SOUTH);

		JPanel outputPanel = new JPanel(new BorderLayout());
		outputPanel.add(new JScrollPane(resultTextArea), BorderLayout.CENTER);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputPanel, outputPanel);
		splitPane.setDividerLocation(150);

		f.add(splitPane, BorderLayout.EAST);

		f.getContentPane().add(new JScrollPane(controlPanel), BorderLayout.SOUTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
	}

}