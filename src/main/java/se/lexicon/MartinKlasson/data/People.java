package se.lexicon.MartinKlasson.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface People {
    public Person create(Person person);
    public List<Person> findAll();
    public Optional<Person> findById (int personId);
    public List<Person> findByName(String name);
    public Person update(Person person);
    public boolean deleteById(Person person);
}
