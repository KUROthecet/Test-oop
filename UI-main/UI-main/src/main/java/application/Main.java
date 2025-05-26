package application;

import application.model.Product;
import application.service.ProductClient;
import application.ui.components.NavBarFactory;
import application.ui.components.SearchFormFactory;
import application.ui.components.ChatbotPanelFactory;
import application.ui.layout.ProductGridFactory;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;
import java.util.Map;

public class Main extends Application {
    private ScrollPane productPane;
    private VBox filterPanel;
    private VBox chatbotPanel;

    @Override
    public void start(Stage stage) {
        HBox navBar = NavBarFactory.createNavBar();

        TextField searchField = new TextField();
        searchField.setPromptText("Bạn cần tìm gì?");
        searchField.setPrefWidth(300);

        Button searchBtn = new Button("Search");
        searchBtn.getStyleClass().add("search-button");

        Button filterBtn = new Button("Filter");
        filterBtn.getStyleClass().add("filter-button");

        Button chatBtn = new Button("💬 Chat");
        chatBtn.getStyleClass().add("chat-button");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(10, navBar, spacer, searchField, searchBtn, filterBtn, chatBtn);
        header.setAlignment(Pos.CENTER_LEFT);

        productPane = new ScrollPane();
        productPane.setFitToWidth(true);

        filterPanel = new VBox(10);
        filterPanel.getStyleClass().add("filter-panel");
        filterPanel.setVisible(false);
        filterPanel.setManaged(false);

        chatbotPanel = ChatbotPanelFactory.createChatbotPanel();
        chatbotPanel.setVisible(false);
        chatbotPanel.setManaged(false);

        HBox mainBox = new HBox(10);
        mainBox.getChildren().addAll(filterPanel, productPane, chatbotPanel);
        HBox.setHgrow(productPane, Priority.ALWAYS);

        searchBtn.setOnAction(e -> doSearch(Map.of("model", searchField.getText().trim())));
        doSearch(Map.of());

        filterBtn.setOnAction(e -> {
            boolean show = !filterPanel.isVisible();
            filterPanel.setVisible(show);
            filterPanel.setManaged(show);
        });

        chatBtn.setOnAction(e -> {
            boolean show = !chatbotPanel.isVisible();
            chatbotPanel.setVisible(show);
            chatbotPanel.setManaged(show);
        });

        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(mainBox);

        Scene scene = new Scene(root, 1400, 900);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Seventeen's Store");
        stage.setMaximized(true);
        stage.show();
    }

    private void doSearch(Map<String,String> params) {
        List<Product> hits = ProductClient.fetchWithParams(params);
        productPane.setContent(ProductGridFactory.createProductGrid(hits));
        GridPane form = SearchFormFactory.create(p -> doSearch(p));
        filterPanel.getChildren().setAll(form);
    }

    public static void main(String[] args) {
        launch();
    }
}