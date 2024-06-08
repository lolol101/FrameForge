package frameforge.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AutoCompleteEntryChooser {
    private final ListView<String> suggestionsListView;
    private final ObservableList<String> suggestions;

    private final List<String> possibleSuggestions;
    Consumer<String> contextActionWithChosenEntry;

    AutoCompleteEntryChooser(VBox vbox, TextField textField, List<String> suggestionsList, Consumer<String> contextActionWithChosenEntry) {
        // TODO: pass existing @FXML ListView instead to have better control on element placement in VBox
        suggestions = FXCollections.observableList(new ArrayList<>());
        suggestions.addAll(suggestionsList);
        suggestionsListView = new ListView<>(suggestions);
        possibleSuggestions = suggestionsList;
        this.contextActionWithChosenEntry = contextActionWithChosenEntry;

//        System.out.println(this.getClass() + " possibleSuggestions: " + possibleSuggestions);

        suggestionsListView.setVisible(false);
        textField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
//            System.out.println("formatter debug: newText is " + newText);
            if (newText.isEmpty()) {
                suggestionsListView.setVisible(false);
                return change;
            }
//            System.out.println("attempting to choose possibleSuggestions: " + possibleSuggestions);
            suggestions.clear();
//            System.out.println("attempting to choose possibleSuggestions: " + possibleSuggestions);
            suggestions.addAll(getSuggestions(newText));
            suggestionsListView.setVisible(!suggestions.isEmpty());
            System.out.println(this.getClass() + ": " + suggestions);
            return change;
        }));

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
        }); // TODO: is needed?

        suggestionsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedSuggestion = suggestionsListView.getSelectionModel().getSelectedItem();
                contextActionWithChosenEntry.accept(selectedSuggestion);
                textField.setText(""); // TODO: or edit?
                suggestionsListView.setVisible(false);
            }
        });

        vbox.getChildren().addAll(textField, suggestionsListView);
    }

    List<String> getSuggestions(String newText) {
        List<String> result = new ArrayList<>();
//        System.out.println("debug: trying to suggest possible tags to add from " + possibleSuggestions);
        for (var s : possibleSuggestions) {
//            System.out.println(s + " is suitable?");
            if (s.startsWith(newText)) {
                result.add(s);
//                System.out.println("Yes!");
            }
//            else System.out.println("No!");

        }
        return result;
    }
}
