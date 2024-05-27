package com.example.javafx;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Pane pane = new Pane();

        ArrayList<Character> separators = new ArrayList<Character>();

        separators.add('*');
        separators.add('\'');
        separators.add('\"');
        StringBuilder textValue = new StringBuilder("00");
        for (char i : separators){
            textValue.append(i).append("00");
        }

        TextField timeField = new TextField();
        timeField.setText(textValue.toString());
        timeField.setMinSize(100, 40);
        timeField.setFont(Font.font("Times New Roman", FontWeight.NORMAL, 30));

        timeField.setTextFormatter(new TextFormatter<>(c -> {
            //если нажимаемая клавиша изменяет текст, то разрешаем только цифры и клавишу удаления
            if (c.isContentChange()) {
                if (c.getText().matches("[0-9]")) {
                    String selectedString = timeField.getSelectedText();
                    for (char i : separators){
                        if (selectedString.indexOf(i) != -1){
                            return null;
                        }
                    }
                    return c;
                };
                //удаление
                if (c.isDeleted()){
                    //если хотя бы один из символов, который должен удалится, является сепаратором, то удаления не происходит
                    char deletedChar = timeField.getCharacters().charAt(c.getCaretPosition());
                    if (separators.contains(deletedChar)){
                        return null;
                    }
                    String selectedString = timeField.getSelectedText();
                    for (char i : separators){
                        if (selectedString.indexOf(i) != -1) return null;
                    }
                    return c;
                }
                return null;
            }
            return c;
        }));

        timeField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                selectWhereCaretIs(timeField, separators);
            }
        });

        timeField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.RIGHT){
                    selectWhereCaretIs(timeField, separators);
                }
                if (keyEvent.getCode() == KeyCode.LEFT){
                    timeField.positionCaret(timeField.getSelection().getStart()-1);
                    selectWhereCaretIs(timeField, separators);
                }
                if (timeField.getSelectedText().length()>0){
                    if (separators.contains(timeField.getSelectedText().charAt(0))){
                        timeField.deselect();
                    }
                }
            }
        });

        pane.getChildren().add(timeField);
        pane.getChildren();
        Scene scene = new Scene(pane, 400, 400);
        stage.setScene(scene);
        stage.show();
    }

    private ArrayList<Integer> calculateSeparatorPositions(String text, ArrayList<Character> separators) {
        ArrayList<Integer> separatorPositions = new ArrayList<>();
        for (int i = 0; i<text.length(); i++){
            if (separators.contains(text.charAt(i))){
                separatorPositions.add(i);
            }
        }
        return separatorPositions;
    }
    private int[] findWordIndexes(String text, int position, ArrayList<Character> separators){
        int wordIndexes[] = new int[2];
        ArrayList<Integer> separatorPositions = new ArrayList<>();
        separatorPositions = calculateSeparatorPositions(text, separators);
        separatorPositions.addFirst(-1);
        separatorPositions.add(text.length());
        for (int i = 0; i<separatorPositions.size()-1; i++){
            if (position>separatorPositions.get(i) && position < separatorPositions.get(i+1)){
                wordIndexes[0] = separatorPositions.get(i)+1;
                wordIndexes[1] = separatorPositions.get(i+1);
                break;
            }
        }
        return wordIndexes;
    }

    private void selectWhereCaretIs(TextField textField, ArrayList<Character> separators){
        int caretPosition = textField.getCaretPosition();
        String fieldText = textField.getText();
        if (caretPosition >= 0){
            int sep1, sep2 = -1;
            for (int i = 0; i<fieldText.length(); i++){
                if (separators.contains(fieldText.charAt(i))){
                    sep1 = sep2;
                    sep2 = i;
                    if (caretPosition > sep1 && caretPosition < sep2){
                        textField.selectRange(sep1+1, sep2);
                    }
                }
            }
            if (caretPosition > sep2 && caretPosition < fieldText.length()){
                textField.selectRange(sep2+1, fieldText.length());
            }
        }
    }
}