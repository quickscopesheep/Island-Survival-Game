package renderEngine.Texture;

public class Texture {
    int id, slot;

    public Texture(int id, int slot) {
        this.id = id;
        this.slot = slot;
    }

    public int getId() {
        return id;
    }

    public int getSlot() {
        return slot;
    }
}
