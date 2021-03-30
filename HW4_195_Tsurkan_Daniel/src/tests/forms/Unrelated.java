package forms;

import annotationPackage.Positive;

public class Unrelated {
    @Positive
    private int x;
    public Unrelated(int x) {
        this.x = x;
    }
}
