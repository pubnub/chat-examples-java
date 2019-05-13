package animal.forest.chat.adapters;

public class ViewType {

    int index;
    int type;

    public ViewType(int index, int type) {
        this.index = index;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
