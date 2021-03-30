package forms;

import annotationPackage.*;

@Constrained
public class GuestForm {
    @NotNull
    @NotBlank
    private final String firstName;
    @NotBlank
    @NotNull
    private final String lastName;
    @Positive
    @InRange(min = 0, max = 200)
    private final int age;
    public GuestForm(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }
}
