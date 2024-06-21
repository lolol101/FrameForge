package frameforge.viewmodel;

public abstract class ViewModel<Model> {
    Model model;

    public void setModel(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }
}
