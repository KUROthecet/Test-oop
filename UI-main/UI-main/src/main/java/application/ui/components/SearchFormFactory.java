package application.ui.components;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SearchFormFactory {
    public static GridPane create(Consumer<Map<String,String>> onSearch) {
        GridPane form = new GridPane();
        form.getStyleClass().add("search-form");

        TextField brand    = new TextField(); brand.setPromptText("brand");
        TextField priceFrom= new TextField(); priceFrom.setPromptText("priceFrom");
        TextField priceTo  = new TextField(); priceTo.setPromptText("priceTo");
        TextField cpu      = new TextField(); cpu.setPromptText("cpu");
        TextField gpu      = new TextField(); gpu.setPromptText("gpu");
        TextField dspFrom  = new TextField(); dspFrom.setPromptText("displaySizeFrom");
        TextField dspTo    = new TextField(); dspTo.setPromptText("displaySizeTo");
        TextField res      = new TextField(); res.setPromptText("displayResolution");
        TextField color    = new TextField(); color.setPromptText("color");
        TextField ram      = new TextField(); ram.setPromptText("ram");
        TextField os       = new TextField(); os.setPromptText("os");
        TextField storage  = new TextField(); storage.setPromptText("storage");

        Button searchBtn   = new Button("Search");

        form.addRow(0, new Label("Brand:"), brand);
        form.addRow(1, new Label("Price From:"), priceFrom);
        form.addRow(2, new Label("Price To:"), priceTo);
        form.addRow(3, new Label("CPU:"), cpu);
        form.addRow(4, new Label("GPU:"), gpu);
        form.addRow(5, new Label("Disp. From:"), dspFrom);
        form.addRow(6, new Label("Disp. To:"), dspTo);
        form.addRow(7, new Label("Res:"), res);
        form.addRow(8, new Label("Color:"), color);
        form.addRow(9, new Label("RAM:"), ram);
        form.addRow(10,new Label("OS:"), os);
        form.addRow(11,new Label("Storage:"), storage);
        form.add(searchBtn, 1, 12);

        searchBtn.setOnAction(e -> {
            Map<String,String> params = new LinkedHashMap<>();
            if (!brand.getText().isBlank())    params.put("brand", brand.getText().trim());
            if (!priceFrom.getText().isBlank())params.put("priceFrom", priceFrom.getText().trim());
            if (!priceTo.getText().isBlank())  params.put("priceTo", priceTo.getText().trim());
            if (!cpu.getText().isBlank())      params.put("cpu", cpu.getText().trim());
            if (!gpu.getText().isBlank())      params.put("gpu", gpu.getText().trim());
            if (!dspFrom.getText().isBlank())  params.put("displaySizeFrom", dspFrom.getText().trim());
            if (!dspTo.getText().isBlank())    params.put("displaySizeTo", dspTo.getText().trim());
            if (!res.getText().isBlank())      params.put("displayResolution", res.getText().trim());
            if (!color.getText().isBlank())    params.put("color", color.getText().trim());
            if (!ram.getText().isBlank())      params.put("ram", ram.getText().trim());
            if (!os.getText().isBlank())       params.put("os", os.getText().trim());
            if (!storage.getText().isBlank())  params.put("storage", storage.getText().trim());
            onSearch.accept(params);
        });

        return form;
    }
}