package validationClasses;

import forms.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ValidateClassTest {

    ValidateClass validator;
    BookingForm b;

    @BeforeEach
    void setUp() {
        validator = new ValidateClass();
//        List<GuestForm> guests = List.of(
//                new GuestForm(/*firstName*/ "Ann", /*lastName*/ "Redgy", /*age*/ 21),
//                new GuestForm(/*firstName*/ null, /*lastName*/ "Ijk", /*age*/ -3)
//        );
        Unrelated unrelated = new Unrelated(-1);
        List<Integer> l = new ArrayList<>();
        l.add(1);
        l.add(-2);
        l.add(3);
        l.add(4);

        List<Integer> listInt = new ArrayList<>();
        l.add(45);
        l.add(-78);

        List<List<Integer>> l1 = new ArrayList<>();
        l1.add(listInt);
        l1.add(listInt);
        l1.add(listInt);

        List<List<Integer>> l2 = new ArrayList<>();
        l2.add(l);
        l2.add(l);

        List<List<List<Integer>>> list = new ArrayList<>();
        list.add(l1);
        list.add(l2);
        list.add(null);

        b = new BookingForm(
                /*guests,*/ List.of("TV", "Food"), "Room", unrelated, list
        );

    }

    @AfterEach
    void tearDown() {
        validator = null;
        b = null;
    }

    @Test
    void validate() {
        Set<ValidationError> errors = validator.validate(b);
        // Поле guests должно выдать 3 ошибки по 2 гостю. Имя не должно быть null и
        // возраст не может быть отрицательным и должен быть в диапазоне [0, 200].
        // Поле customList содержит 9 положительных чисел, но из-за присутствия аннотации @Negative создаются ошибки.
        // Поле amenities содержит "Food", но это значение не входит в допустимые значения.
        // Поле propertyType тоже не соответствует ни одному значению аннотации.

        // В сумме должно выйти 14 ошибок!
        assertEquals(errors.size(), 3);

        for(ValidationError er:errors)
            System.out.println(er.getMessage()+"\t\t"+er.getPath()+"\t\t"+er.getFailedValue()+"\n");
    }
}