
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.*;

public class Logger extends PrintStream {

    private TextArea textArea;
    private OutputStream backupStream;
    public Logger( TextArea ta , OutputStream out) {
        super(out);
        backupStream = out;
        this.textArea = ta;
    }


    @Override
    public void println(String string) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                   textArea.appendText(string + "\n");
                }
            });

        }

    @Override
    public void print(String string) {
           Platform.runLater(new Runnable() {
               @Override
               public void run() {
                 textArea.appendText(string);
               }
           });

    }


}
