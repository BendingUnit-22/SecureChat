import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;



public class Main extends Application implements ConnectCallback{

    Stage window;
    Label status;
    TextArea textbox;
    Logger log;
    String local_ip;
    Server sever;
    Client client;
    Button connect_btn;
    TextField username;
    boolean hasStartedChat;
    @Override
    public void start(Stage primaryStage) throws Exception {
        hasStartedChat = false;
        window = primaryStage;
        window.setTitle("Secure Chat");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10,10,10,10));
        layout.setAlignment(Pos.CENTER);
        layout.setMinWidth(300);
        Label title = new Label("SecureChat");
        title.setFont(new Font("Charcoal CY", 30));

        Label  local_ip_label = new Label();
        local_ip = NetworkTool.requestPublicIPAddress();

        local_ip_label.setText("Local IP: " + local_ip);

        HBox row1 = new HBox(10);
        row1.setAlignment(Pos.CENTER);
         username = new TextField();
        username.setPromptText("Rixing");
        Label name = new Label("username:");
        row1.getChildren().addAll(name, username);

        HBox row2 = new HBox(10);
        row2.setAlignment(Pos.CENTER);
        TextField hostAddress = new TextField();
        hostAddress.setPromptText("192.168.1.122");
        Label iplabel = new Label("host addr:");
        row2.getChildren().addAll(iplabel, hostAddress);

        HBox row3 = new HBox(10);
        status = new Label();

        CheckBox isServer = new CheckBox("Server");
        isServer.setSelected(false);

        isServer.setOnAction(e -> {
            hostAddress.setDisable(isServer.isSelected());
            hostAddress.setText("localhost");
        });

        connect_btn = new Button("Connect");
        connect_btn.setOnAction(e -> {
            boolean selected = isServer.isSelected();
            if (hasStartedChat){
                window.setScene(chatroom);

            }
            else if (username.getText().isEmpty()){
                status.setText("Error some field(s) is empty!");
            }else{
                status.setText("");
                String text = selected ? "Connecting...": "Listening...";
                connect_btn.setText(text);
                connectChat(hostAddress.getText(), isServer.isSelected());
            }
        });

        status.setId("status");
        row3.setAlignment(Pos.CENTER_LEFT);
        row3.getChildren().addAll(connect_btn,status);

        textbox = new TextArea();

        layout.getChildren().addAll(title, isServer,local_ip_label, row1,row2, row3, textbox);
        Scene scene = new Scene(layout);

        window.setScene(scene);
        window.show();
        scene.getStylesheets().add(this.getClass().getResource("UIStyle.css").toExternalForm());

        log = new Logger(textbox, System.out);
        System.setOut(log);


        ///Test

        hostAddress.setText("localhost");
        username.setText("Rixing");
      //  startChat("Rixing");
        //Test

    }

    public void connectChat( String ipAddress, boolean asHost){
        connect_btn.setDisable(true);
        int port = 1234;
            try {
                if (!asHost) {
                    client = new Client(ipAddress, port, this);
                }else {
                    textbox.clear();
                    sever = new Server(port, this);
                    sever.setDaemon(true);
                    sever.start();
                }
            }catch (Exception e){
                System.out.println("Error! Sever is down at the moment!");
                connect_btn.setText("Connect");
                connect_btn.setDisable(false);
                return;
            }

    }

    @Override
    public void ConnectionCallback(SecureConnection secCon) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                connect_btn.setText("Connected");
                connect_btn.setId("btnConnected");

            }
        });
        String sender = username.getText();
        secCon.setDaemon(true);
        secCon.setCallback(this);
        secCon.start();

        INFO greeting  = new INFO(sender, "Me: ", "Greeting!");
        secCon.sendMessage(greeting);
    }

    @Override
    public void didRecieveInfo(INFO info) {
      if (!hasStartedChat){
          hasStartedChat = true;
          String senderTo = info.getSender();
          items.add(senderTo + ": " + info.getData());
          startChat(senderTo);
      }else{




      }

    }





    ObservableList<String> items = FXCollections.observableArrayList ();
    Scene chatroom;



    public void startChat(String name){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                connect_btn.setText("Start Chat");
                connect_btn.setDisable(false);

                double width = window.getScene().getWidth();
                double height = window.getScene().getHeight();
                BorderPane layout = new BorderPane();
                layout.setMinWidth(width);
                layout.setMinHeight(height);
                window.setTitle("Secure Chat Room");

                Label title = new Label("AES 256-bit ( Chating with " + name + ")");
                title.setId("greeting");
                title.setMinWidth(width);

                ToolBar toolbar = new ToolBar();
                ListView<String> list = new ListView<String>();
                list.setItems(items);
                layout.setTop(title);

                layout.setCenter(list);
                layout.setBottom(toolbar);

                 chatroom = new Scene(layout);

                chatroom.getStylesheets().add(this.getClass().getResource("UIStyle.css").toExternalForm());

            }
        });

    }
}
