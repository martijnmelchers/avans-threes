package avans.threes.model;

public class Tile {
    private Integer value = -1;
    private int round = 0;

    public Tile() {

    }

    public Tile(int startValue) throws Exception {
        if (this.invalidValue(startValue))
            throw new Exception("Invalid start value supplied!");

        this.value = startValue;
    }

    /* Check if the value provided is dividable by 3 or is a other valid value */
    private boolean invalidValue(int value) {
        return value != 1 && value != 2 && value != -1 && value % 3 != 0;
    }

    /* Returns whether or not the tile is filled */
    public boolean isFilled() {
        return this.value != -1;
    }

    /* Return the value of the tile as text */
    public String getText() {
        if (this.value == -1)
            return "";
        else
            return this.value.toString();
    }

    /* Return the value of the tile */
    public Integer getValue() {
        return this.value;
    }

    /* Set the text and validate the value */
    public void setValue(int value) throws Exception {
        if (this.invalidValue(value))
            throw new Exception("Invalid start value supplied!");

        this.value = value;
    }

    /* Merge the tile with another */
    public void merge(Integer value) throws Exception {
        if ((this.value == 1 && value == 2) || (this.value == 2 && value == 1))
            this.value = 3;
        else if (this.value.equals(value) && value > 2)
            this.value = this.value * 2;
        else
            throw new Exception("Could not merge tile!");

    }

    /* Get the last round the tiles was moved it so it doesn't keep moving */
    public int getLastMovedRound() {
        return this.round;
    }

    /* Set the last moved round */
    public void setLastMovedRound(int round) {
        this.round = round;
    }

    /* Simulate a merge without actually merging */
    public boolean simulateMerge(Integer value) {
        if ((this.value == 1 && value == 2) || (this.value == 2 && value == 1))
            return true;
        else return this.value.equals(value) && value > 2;
    }
}
