package visualizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Collections;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import visualizer.algorithms.Speed;
import visualizer.algorithms.searching.BinarySearch;
import visualizer.algorithms.searching.LinearSearch;

public class SearchingController implements Initializable {

    @FXML
    private Label numberOfValuesText;

    @FXML
    private HBox searchingPane;
    
    @FXML
    private Slider numberOfValuesSlider;

    @FXML
    private RadioButton linear, binary;

    @FXML
    private Slider speedSlider;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private ChoiceBox<String> numberSearching;

    private boolean isSorted = false;
    private int numberOfValues;
    private boolean generated = false;
    public static boolean isRunning;
    private int numToSearch;
    private boolean fromFile = false;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        numberOfValuesSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                numberOfValues = (int) numberOfValuesSlider.getValue();
                numberOfValuesText.setText("Počet hodnot: " + numberOfValues);
            }
            });
            mainPane.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
                if(key.getCode()==KeyCode.R) {
                    if(SearchingController.isRunning) SearchingController.isRunning = false;
                    else System.out.println("No algorithm running");
                }
            });
        this.numberSearching.setOnAction(this::updateNumber);
    }

    @FXML
    protected void generate(){
        if(generated) this.clearStage();
        int amount = numberOfValues;
        int maxHeight = (int) searchingPane.getHeight()-10;
        int maxWidth = (int) (searchingPane.getWidth()/amount*.9);
        double padding = (searchingPane.getWidth()/amount*.05)/2;
        this.numberSearching.getItems().clear();
        this.searchingPane.setSpacing(padding);
        String[] numbers = new String[amount+1];
        numbers[0] = "náhodná hodnota";
        for(int i=1; i<amount+1; i++){
            int height = (int) (i*(maxHeight-50)/amount)+50;
            Rectangle rect = new Rectangle(maxWidth, height);
            this.searchingPane.getChildren().add(rect);
            numbers[i] = Integer.toString(height);
        }

        this.numberSearching.getItems().addAll(numbers);
        this.isSorted = true;
        if(!binary.isSelected()){
            this.isSorted = false;
            this.shuffle();
        }
        this.fromFile = false;
        generated = true;
    }

    private void generate(int[] numbers){
        System.out.println("is called");
        if(generated) this.clearStage();
        this.fromFile = true;
        int heightMultiplier = (int) (searchingPane.getHeight()-10)/100;
        int maxWidth = (int) (searchingPane.getWidth()/(numbers.length)*.9);
        double padding = (searchingPane.getWidth()/numbers.length*.05)/2;
        this.numberSearching.getItems().clear();
        this.searchingPane.setSpacing(padding);
        String[] numberText = new String[numbers.length+1];
        numberText[0] = "náhodná hodnota";
        for(int i=1; i<numbers.length+1; i++){
            int height = (int) numbers[i-1]*heightMultiplier;
            Rectangle rect = new Rectangle(maxWidth, height);
            this.searchingPane.getChildren().add(rect);
            numberText[i] = Integer.toString(height);
        }
        numberOfValuesSlider.setValue(numbers.length);
        numberOfValuesText.setText("Počet hodnot: " + numbers.length);
        this.numberSearching.getItems().addAll(numberText);
        this.isSorted = true;
        for (int i = 0; i < numbers.length-1; i++){
            if(numbers[i]>numbers[i+1]){
                this.isSorted = false;
                return;
            }
        }
    }
    
    private void clearStage(){
        ObservableList<Rectangle> list = (ObservableList) this.searchingPane.getChildren();
        list.clear();
    }

    @FXML
    private void search(){
        Speed algoSpeed;
        switch ((int) speedSlider.getValue()) {
            case 1:
                algoSpeed = Speed.Slow;
                break;
            case 2:
                algoSpeed = Speed.Normal;
                break;
            case 3:
                algoSpeed = Speed.Fast;
                break;
            default:
                algoSpeed = Speed.Slow;
            }
        Runnable search = this.getAlgorithm(algoSpeed, (ObservableList) this.searchingPane.getChildren(), this.numToSearch);
        if(search!=null){
            SearchingController.isRunning = true;
            Thread thread = new Thread(search);
            thread.setName("Algorithm thread");
            thread.start();
        }
    }


    private Runnable getAlgorithm(Speed sleep, ObservableList<Rectangle> list, int value){
        if(linear.isSelected()){
            return new LinearSearch(sleep, list, value, this.fromFile);
        }else if(binary.isSelected()){
            if(this.isSorted){
                return new BinarySearch(sleep, list, value, this.fromFile);
            }
            Alert alert = new Alert(AlertType.WARNING);
            alert.setHeaderText("Nastala chyba");
            alert.setContentText("Vygenerujte čísla znovu. Čísla pro binární vyhledávání musí být seřazené.");
            alert.show();    
            return null;
        }
        Alert alert = new Alert(AlertType.WARNING);
        alert.setHeaderText("Nastala chyba");
        alert.setContentText("Nebyl vybrán žádný algoritmus");
        alert.show();
        return null;         
    }

    private void shuffle(){
        Random random = new Random();
        ObservableList<Rectangle> list = (ObservableList)this.searchingPane.getChildren();
        for(int i = 0; i < list.size() - 1; i++){
            int randomIndexToSwap = random.nextInt(list.size());
			Double rect1 = list.get(randomIndexToSwap).getHeight();
            Double rect2 = list.get(i).getHeight();
			list.set(randomIndexToSwap, new Rectangle(list.get(0).getWidth(), rect2));
			list.set(i, new Rectangle(list.get(0).getWidth(), rect1));
        }
    }

    private void updateNumber(ActionEvent e){
        String search = this.numberSearching.getValue();
        if("náhodná hodnota" == search){
            Random r = new Random();
            this.numToSearch = r.nextInt(1000);
        }else{
            this.numToSearch = Integer.parseInt(search);
        }
    }

    @FXML
    private void getFile(){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Vyberte soubor s daty:");
        Stage stage = (Stage) mainPane.getScene().getWindow();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("textový soubor", "*.txt"));
        File file = chooser.showOpenDialog(stage);
        try {
            Scanner scanner = new Scanner(file);
            String text = scanner.nextLine();
            String[] numberText = text.split(",");
            int[] numbers = new int[numberText.length-1];
            for(int i = 0; i<numberText.length-1;i++){
                numbers[i] = Integer.parseInt(numberText[i]);
                System.out.print(numbers[i]+" ,");
            }
            this.generate(numbers);
            scanner.close();
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Chyba");
            alert.setContentText("Při načítání souboru nastala chyba");
            e.printStackTrace();
        }

    }
}
