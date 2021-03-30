package validationClasses;

import forms.MyFormPrimitive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidateClassTestPrimitives {

    ValidateClass validator;
    MyFormPrimitive myForm;

    @BeforeEach
    void setUp() {
        validator = new ValidateClass();
        myForm = new MyFormPrimitive(-5, (byte) 5, null, 21, "");
    }

    @AfterEach
    void tearDown() {
        myForm = null;
        validator = null;
    }

    @Test
    void validatePrimitives() {
        assertEquals(validator.validate(myForm).size(), 6);
    }
}