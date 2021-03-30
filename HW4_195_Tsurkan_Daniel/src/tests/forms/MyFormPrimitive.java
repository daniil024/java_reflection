package forms;

import annotationPackage.*;

@Constrained
public class MyFormPrimitive {

    @Positive
    @InRange(min = 1, max = 100)
    int positive;

    @Negative
    byte negative;

    @NotNull
    String name;

    @Positive
    int age;

    @NotEmpty
    @Size(min = 4, max = 20)
    String email;

    public MyFormPrimitive(int p, byte n, String name, int a, String email){
        positive = p;
        negative = n;
        this.name = name;
        age = a;
        this.email = email;
    }
}
