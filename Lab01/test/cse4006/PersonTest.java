package cse4006;

import org.junit.Test;

import static org.junit.Assert.*;

public class PersonTest {
    Person person;
    @Test
    public void getName() throws Exception {
        String name = "John";
        person = new Person(name);
        assertEquals(name, person.getName());
    }

    @Test
    public void nextName() throws Exception {
        person = new Person();
        assertNotNull(person.getName());
    }
}