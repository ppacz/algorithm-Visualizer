package visualizer;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

public class SortingController implements Initializable {
    @FXML
    private Label numberOfValuesText;

    @FXML
    private HBox sortingPane;
    
    @FXML
    private Slider numberOfValuesSlider;
    private int numberOfValues;
    private int minHeight = 10;
    private boolean generated = false;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        numberOfValuesSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                numberOfValues = (int) numberOfValuesSlider.getValue();
                numberOfValuesText.setText("Počet hodnot: " + numberOfValues);
            }
            
        });
        
    }

    @FXML
    protected void generate(){
        if(generated) clearStage();
        int amount = numberOfValues;
        int maxHeight = (int) sortingPane.getHeight()-10;
        int maxWidth = (int) (sortingPane.getWidth()/amount*.9);
        double padding = (sortingPane.getWidth()/amount*.05)/2;
        this.sortingPane.setSpacing(padding);
        Random random = new Random();
        for(int i=0; i<amount; i++){
            int randomNum = random.nextInt((maxHeight - minHeight) + 1) + minHeight;
            Rectangle rect = new Rectangle(maxWidth, randomNum);
            this.sortingPane.getChildren().add(rect);
        }
        generated = true;
    }
    
    private void clearStage(){
        ObservableList<Rectangle> list = (ObservableList)this.sortingPane.getChildren();
        list.clear();
    }
}