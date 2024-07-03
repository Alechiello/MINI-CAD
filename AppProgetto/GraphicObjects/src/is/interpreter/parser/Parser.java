package is.interpreter.parser;

import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.IOException;

import is.command.Command;
import is.interpreter.singleton.ObjectRegister;
import is.shapes.model.CircleObject;
import is.shapes.model.GraphicObject;
import is.shapes.model.RectangleObject;
import is.shapes.model.ImageObject;
import is.shapes.specificcommand.NewObjectCmd;
import is.shapes.specificcommand.*;
import is.shapes.view.GraphicObjectPanel;

import javax.swing.ImageIcon;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Parser {
    private final StreamTokenizer tokenizer;
    private final GraphicObjectPanel panel;

    public Parser(Reader reader, GraphicObjectPanel panel) {
        tokenizer = new StreamTokenizer(reader);
        tokenizer.ordinaryChar('/'); // Ensure '/' is recognized as a separate token
        this.panel = panel;
    }

    public Command parseCommand() throws IOException {
        int token = tokenizer.nextToken();
        if (token == StreamTokenizer.TT_WORD) {
            return switch (tokenizer.sval) {
                case "new" -> new CreateCommandHelper().parse(tokenizer, panel);
                case "del" -> new DeleteCommandHelper().parse(tokenizer, panel);
                case "mv" -> new MoveCommandHelper().parse(tokenizer, false);
                case "mvoff" -> new MoveCommandHelper().parse(tokenizer, true);
                case "scale" -> new ScaleCommandHelper().parse(tokenizer);
                case "ls" -> new ListCommandHelper().parse(tokenizer);
                case "grp" -> new GroupCommandHelper().parse(tokenizer);
                case "ungrp" -> new UngroupCommandHelper().parse(tokenizer);
                case "area" -> new AreaCommandHelper().parse(tokenizer);
                case "perimeter" -> new PerimeterCommandHelper().parse(tokenizer);
                default -> throw new IllegalArgumentException("Comando non riconosciuto: " + tokenizer.sval);
            };
        }
        throw new IllegalArgumentException("Unexpected token: " + tokenizer.toString());
    }

    private static class CreateCommandHelper {
        Command parse(StreamTokenizer tokenizer, GraphicObjectPanel panel) throws IOException {
            tokenizer.nextToken();
            String type = tokenizer.sval;

            tokenizer.nextToken(); // consuma '('
            tokenizer.nextToken(); // Vado al primo numero

            double param1 = 0;
            double param2 = 0;
            double param3 = 0;
            double param4 = 0;
            String path="";
            if(Objects.equals(type, "img") && tokenizer.ttype==StreamTokenizer.TT_WORD) {
                path= tokenizer.sval;
            }
            else if (tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
                param1 = tokenizer.nval;
            } else {
                throw new IllegalArgumentException("Il parametro non è valido: " + tokenizer.toString());
            }

            if(!Objects.equals(type, "rectangle")) {

                tokenizer.nextToken(); // consuma ')'
                tokenizer.nextToken(); // consuma '('

                tokenizer.nextToken(); // vado al primo numero della posizione

                if (tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
                    param2 = tokenizer.nval;
                } else {
                    throw new IllegalArgumentException("Il parametro non è valido: " + tokenizer.toString());
                }

                tokenizer.nextToken(); // consuma ','
                tokenizer.nextToken(); //vado al secondo numero della posizione

                if (tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
                    param3 = tokenizer.nval;
                } else {
                    throw new IllegalArgumentException("Expected a number for param3, got: " + tokenizer.toString());
                }

                tokenizer.nextToken(); // consuma ')'
            }
            else {
                tokenizer.nextToken(); // consuma ','
                tokenizer.nextToken(); // vado all'altezza del rettangolo

                if (tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
                    param2 = tokenizer.nval; //altezza rettangolo
                } else {
                    throw new IllegalArgumentException("Il parametro non è valido: " + tokenizer.toString());
                }
                tokenizer.nextToken(); // consuma ')'
                tokenizer.nextToken(); // consuma '('

                tokenizer.nextToken(); // vado al primo numero della posizione

                if (tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
                    param3 = tokenizer.nval;
                } else {
                    throw new IllegalArgumentException("Il parametro non è valido: " + tokenizer.toString());
                }

                tokenizer.nextToken(); // consuma ','
                tokenizer.nextToken(); //vado al secondo numero della posizione

                if (tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
                    param4 = tokenizer.nval;
                } else {
                    throw new IllegalArgumentException("Expected a number for param3, got: " + tokenizer.toString());
                }

                tokenizer.nextToken(); // consuma ')'




            }

            Point2D position = new Point2D.Double(param2, param3);
            switch (type) {
                case "circle":
                    return new NewObjectCmd(panel, new CircleObject(position, param1));
                case "rectangle":
                    position= new Point2D.Double(param3, param4);
                    return new NewObjectCmd(panel, new RectangleObject(position, param1, param2));
                case "img":
                    return new NewObjectCmd(panel, new ImageObject(new ImageIcon(path), position));
                default:
                    throw new IllegalArgumentException("Il tipo inserito non esiste: " + type);
            }
        }
    }











    private static class DeleteCommandHelper {
        Command parse(StreamTokenizer tokenizer, GraphicObjectPanel panel) throws IOException {
            tokenizer.nextToken();
            int id = (int) tokenizer.nval;
            return new DelObjectCommand(panel, id);
        }
    }

    private static class MoveCommandHelper {
        Command parse(StreamTokenizer tokenizer, boolean isOffset) throws IOException {
            tokenizer.nextToken();
            int id = (int) tokenizer.nval;
            tokenizer.nextToken();
            double x = tokenizer.nval;
            tokenizer.nextToken();
            double y = tokenizer.nval;
            GraphicObject go = ObjectRegister.getInstance().getObject(id);

            if (isOffset) {
                return new MoveCommand(go, x, y);
            } else {
                Point2D position = new Point2D.Double(x, y);
                return new MoveCommand(go, position);
            }
        }
    }


    private static class ScaleCommandHelper {
        Command parse(StreamTokenizer tokenizer) throws IOException {
            tokenizer.nextToken();
            int id = (int) tokenizer.nval;
            GraphicObject go = ObjectRegister.getInstance().getObject(id);

            if (go == null) {
                throw new IllegalArgumentException("Oggetto con ID " + id + " non trovato");
            }

            tokenizer.nextToken();
            double factor = tokenizer.nval;
            return new ZoomCommand(go, factor);
        }
    }

    private static class ListCommandHelper {
        Command parse(StreamTokenizer tokenizer) throws IOException {
            tokenizer.nextToken();

            // Verifica se il token è un numero o una parola
            String param;
            if (tokenizer.sval != null) {
                param = tokenizer.sval;
            } else {
                param = String.valueOf((int)tokenizer.nval);
            }

            // Determina il tipo di lista da creare in base al parametro
            ListObjectCommand.ListType listType;
            if (param.equals("all")) {
                listType = ListObjectCommand.ListType.ALL;
            } else if (param.equals("groups")) {
                listType = ListObjectCommand.ListType.GROUPS;
            } else if (isNumeric(param)) {
                listType = ListObjectCommand.ListType.SINGLE;
            } else {
                listType = ListObjectCommand.ListType.TYPE;
            }

            return new ListObjectCommand(listType, param);
        }

        private boolean isNumeric(String str) {
            try {
                Integer.parseInt(str);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }


    private static class GroupCommandHelper {
        Command parse(StreamTokenizer tokenizer) throws IOException {
            tokenizer.nextToken();
            List<Integer> ids = new ArrayList<>();
            while (tokenizer.ttype != StreamTokenizer.TT_EOF) {
                ids.add((int) tokenizer.nval);
                tokenizer.nextToken();
            }
            return new CreateGroupCommand(ids);
        }
    }

    private static class UngroupCommandHelper {
        Command parse(StreamTokenizer tokenizer) throws IOException {
            tokenizer.nextToken();
            int id = (int) tokenizer.nval;
            GraphicObject go = ObjectRegister.getInstance().getObject(id);

            if (go == null) {
                throw new IllegalArgumentException("Gruppo con ID " + id + " non trovato.");
            }

            return new UngroupCommand(id);
        }
    }

    private static class AreaCommandHelper {
        Command parse(StreamTokenizer tokenizer) throws IOException {
            tokenizer.nextToken();
            if (tokenizer.ttype == StreamTokenizer.TT_WORD || tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
                String param;
                if (tokenizer.sval != null) {
                    param = tokenizer.sval;
                } else {
                    param = String.valueOf((int)tokenizer.nval);
                }

                return new CalculateAreaCommand(param);
            } else {
                throw new IllegalArgumentException("Il parametro inserito non può essere nullo");
            }
        }
    }


    private static class PerimeterCommandHelper {
        Command parse(StreamTokenizer tokenizer) throws IOException {
            tokenizer.nextToken();
            if (tokenizer.ttype == StreamTokenizer.TT_WORD || tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
                String param;
                if (tokenizer.sval != null) {
                    param = tokenizer.sval;
                } else {
                    param = String.valueOf((int) tokenizer.nval);
                }

                return new CalculatePerimeterCommand(param);
            } else {
                throw new IllegalArgumentException("Il parametro inserito non può essere nullo");
            }
        }
    }

}
