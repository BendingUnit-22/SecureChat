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
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.Serializable;
import java.util.Random;

/** Rixing Wu (Extra credit)
*/

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
    Button sendMeg;
    TextField message;
    Button sendFile;
    SecureConnection secConnect;

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
        username.setPromptText("");
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


        hostAddress.setText("localhost");


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
                System.out.println("Not user has connected at the moment!!");
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
        secConnect = secCon;
        String sender = username.getText();
        secConnect.setDaemon(true);
        secConnect.setCallback(this);
        secConnect.start();

        INFO greeting  = new INFO(sender, "Greetings!", false);
        secConnect.sendMessage(greeting);
    }

    @Override
    public void didRecieveInfo(INFO info) {
     if (!hasStartedChat){
          hasStartedChat = true;
          String takingWith = info.getSender();
          items.add(info);
          startChat(takingWith);
      }else{
         items.add(info);
      }

    }
    /**  New Scene (Chat room )
     *
     */

    ListView<INFO> list;
    ObservableList<INFO> items = FXCollections.observableArrayList();
    Scene chatroom;
    static String icon1;
    static String icon2;

    public void startChat(String name){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Random generator = new Random();
                int p1 = generator.nextInt(7) + 1;
                int p2 = generator.nextInt(7) + 1;
                while (p1 == p2){
                    p2 = generator.nextInt(7) + 1;
                }
                icon1 = "p" + p1 + ".png";
                icon2 = "p" + p2 + ".png";

                connect_btn.setText("Start Chat");
                connect_btn.setDisable(false);

                double width = window.getScene().getWidth();
                double height = window.getScene().getHeight();
                BorderPane layout = new BorderPane();
                layout.setMinWidth(width);
                layout.setMinHeight(height);
                window.setTitle("Secure Chat Room");

                Label title = new Label("connected with (" + name + ")");
                title.setId("greeting");
                title.setMinWidth(width);

               list = new ListView<>();

                list.setItems(items);
                list.setCellFactory((ListView<INFO> l) -> new MessageCell());

                ToolBar toolbar = new ToolBar();
                message = new TextField();
                message.setMinWidth(450);
                sendFile = new Button();
                sendFile.setGraphic(new ImageView("open.png"));
                sendMeg = new Button("Send");
                sendMeg.setMinHeight(25);
                toolbar.getItems().addAll(message,sendFile,sendMeg);

                layout.setTop(title);
                layout.setCenter(list);
                layout.setBottom(toolbar);

                sendMeg.setOnAction(e -> {

                    INFO info = new INFO("Me",message.getText(), true);
                    items.add(info);
                    INFO out = new INFO(username.getText() ,message.getText(), false);
                    secConnect.sendMessage(out);

                    message.clear();
                    list.scrollTo(items.size()-1);
                    });
                sendMeg.setDefaultButton(true);

                sendFile.setOnAction(e -> {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Open Resource File");
                    File file = fileChooser.showOpenDialog(window);
                    if (file != null){
                        IMAGE_OBJ img = new IMAGE_OBJ(file);
                        INFO info = new INFO("Me",img, true);
                        items.add(info);
                        INFO out = new INFO(username.getText(),img, false);
                        secConnect.sendMessage(out);

                    }
                });

                 chatroom = new Scene(layout);

                chatroom.getStylesheets().add(this.getClass().getResource("UIStyle.css").toExternalForm());


            }
        });

    }


    static class MessageCell extends ListCell<INFO>{
        @Override
        public void updateItem(INFO item, boolean empty){
            super.updateItem(item, empty);
            if (item != null){
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        boolean fromHere = item.isLocalCopy();
                        String imsrc;
                        String name;


                        if(fromHere){
                            imsrc = icon1;
                            name = "Me";
                        }else{
                            imsrc = icon2;
                            name = item.getSender();
                        }

                        HBox layout =new HBox();
                        ImageView image= new ImageView(imsrc);
                        image.setFitHeight(50);
                        image.setFitWidth(50);
                        Label nameLabel = new Label(name);
                        nameLabel.setMinWidth(45);
                        nameLabel.setAlignment(Pos.CENTER);
                        nameLabel.setId("name");
                        VBox icon = new VBox(5);
                        icon.getChildren().addAll(image, nameLabel);

                        HBox contentBox = new HBox(4);
                        Serializable data = item.getData();

                        if (data instanceof String){
                            Label meslabel = new Label((String)data);
                            meslabel.setFont(new Font(18));
                            meslabel.setPadding(new Insets(20, 5, 3, 5));
                            contentBox.getChildren().add(meslabel);
                        }

                        if (data instanceof IMAGE_OBJ){
                            IMAGE_OBJ img = (IMAGE_OBJ)data;
                            ImageView imgView = new ImageView(img.getImage());
                            imgView.setPreserveRatio(true);
                            imgView.setFitHeight(200);
                            contentBox.getChildren().add(imgView);
                            HBox.setHgrow(imgView, Priority.ALWAYS);
                       }
                        contentBox.setId("contentBox");
                        layout.getChildren().add(contentBox);

                        if (fromHere){
                            layout.getChildren().add(0,icon);
                        }else {
                            layout.setAlignment(Pos.TOP_RIGHT);
                            layout.getChildren().add(1, icon);
                        }
                        layout.setMaxWidth(Double.MAX_VALUE);
                        setGraphic(layout);

                    }
                });

            }else{
                setGraphic(null);
            }

        }
    }



}
