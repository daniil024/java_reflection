package forms;

import annotationPackage.*;

import java.util.List;

@Constrained
public class BookingForm {

    /*private final List<@Size(min = 3, max = 6) List<List<@Negative Integer>>> customList;
    @NotNull
    @Size(min = 1, max = 5)
    private final List<@NotNull GuestForm> guests;
    @NotNull
    private final List<@AnyOf({"TV", "Kitchen"}) String> amenities;
    @NotNull
    @AnyOf({"House", "Hostel"})
    private final String propertyType;
    @NotNull
    private final Unrelated unrelated;*/

    @NotNull
    GuestForm guest = new GuestForm(/*firstName*/ null, /*lastName*/ "Ijk", /*age*/ -3);


    public BookingForm(/*List<GuestForm> guests,*/ List<String> amenities, String
            propertyType, Unrelated unrelated, List<List<List<Integer>>> l) {
        /*this.guests = guests;
        this.amenities = amenities;
        this.unrelated = unrelated;
        this.propertyType = propertyType;
        this.customList = l;*/
    }
}



