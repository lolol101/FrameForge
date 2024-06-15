package frameforge.view;

import javafx.beans.binding.Bindings;
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

        suggestionsListView.setVisible(false);
        textField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                suggestionsListView.setVisible(false);
                return change;
            }
            suggestions.clear();
            suggestions.addAll(getSuggestions(newText));
            suggestionsListView.setVisible(!suggestions.isEmpty());
            System.out.println(this.getClass() + ": " + suggestions);
            return change;
        }));

        final int LIST_CELL_HEIGHT = 24; // TODO: move to appropriate place
        suggestionsListView.prefHeightProperty().bind(Bindings.size(suggestions).multiply(LIST_CELL_HEIGHT));

        suggestionsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedSuggestion = suggestionsListView.getSelectionModel().getSelectedItem();
                contextActionWithChosenEntry.accept(selectedSuggestion);
                textField.setText(""); // TODO: or edit?
                suggestionsListView.setVisible(false);
            }
        });

        vbox.getChildren().add(suggestionsListView); // TODO: clean up suggestionsListView usage and placement
    }

    List<String> getSuggestions(String newText) {
        List<String> result = new ArrayList<>();
        for (var s : possibleSuggestions) {
            if (s.startsWith(newText)) {
                result.add(s);
            }
        }
        return result;
    }
}
