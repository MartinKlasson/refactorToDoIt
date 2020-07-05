package se.lexicon.MartinKlasson.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static se.lexicon.MartinKlasson.data.ToDoItDataSource.*;

public class Person implements People {
    private int person_id;
    private String first_name;
    private String last_name;



    public Person(int person_id, String first_name, String last_name) {
        this.person_id = person_id;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public Person(String first_name, String last_name) {
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public Person() {

    }

    public int getPerson_id() {
        return person_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return person_id == person.person_id &&
                Objects.equals(first_name, person.first_name) &&
                Objects.equals(last_name, person.last_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(person_id, first_name, last_name);
    }

    @Override
    public String toString() {
        return "Person{" +
                "person_id=" + person_id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                '}';
    }

    @Override
    public Person create(Person person) {
        if(person.getPerson_id() != 0){
            throw new IllegalArgumentException("User already in database, use update to update");
        }
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet keySet = null;
        try{
            connection = ToDoItDataSource.getConnection();
            statement = connection.prepareStatement("INSERT INTO person (first_name, last_name) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, person.getFirst_name());
            statement.setString(2, person.getLast_name());
            statement.execute();
            keySet = statement.getGeneratedKeys();
            while(keySet.next()){
                person = new Person(
                        keySet.getInt(1),
                        person.getFirst_name(),
                        person.getLast_name()
                );
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }finally {
            try {
                if (keySet != null) {
                    keySet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return person;
    }

    @Override
    public List<Person> findAll() {
        List<Person> result = new ArrayList<>();
        try(Connection connection = ToDoItDataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM person");
            ResultSet resultSet = statement.executeQuery()) {
            while(resultSet.next()){
                result.add(createPersonFromResultSet(resultSet));
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return result;
    }
    //Used in findById method
    private PreparedStatement getFindByStatement(Connection connection, String sql, int person_id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, person_id);
        return statement;
    }

    @Override
    public Optional<Person> findById(int personId) {
        Optional<Person> result = Optional.empty();
        try(
                Connection connection = ToDoItDataSource.getConnection();
                PreparedStatement statement = getFindByStatement(
                        connection, "SELECT * FROM person WHERE person_id = ?", personId);
                ResultSet resultSet = statement.executeQuery()
        ){
            while (resultSet.next()){
                result = Optional.of(createPersonFromResultSet(resultSet));
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return result;
    }
    //Used in findById method
    private Person createPersonFromResultSet(ResultSet resultSet) throws SQLException {
        return new Person(
                resultSet.getInt("person_id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name")
        );
    }

    @Override
    public List<Person> findByName(String first_name) {
        List<Person> result = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet  resultSet = null;
        try{
            connection = ToDoItDataSource.getConnection();
            statement = connection.prepareStatement("SELECT * FROM person WHERE first_name LIKE ?");
            statement.setString(1, first_name.concat("%"));
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                result.add(createPersonFromResultSet(resultSet));
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static final String UPDATE_PERSON = "UPDATE person SET first_name = ?, last_name = ? WHERE person_id = ?";
    @Override
    public Person update(Person person) {
        if(person.getPerson_id() == 0){
            throw new IllegalArgumentException("Person cam't be updated because it's not in database");
        }
        try(Connection connection = ToDoItDataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(UPDATE_PERSON)){
           statement.setString(1, person.getFirst_name());
           statement.setString(2, person.getLast_name());
           statement.setInt(3, person.getPerson_id());
           statement.execute();

        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return person;
    }

    public static final String DELETE_PERSON_BY_ID = "DELETE FROM person WHERE person_id = ?";
    @Override
    public boolean deleteById(Person person) {
        boolean deleteDone = false;
        try(Connection connection = ToDoItDataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(DELETE_PERSON_BY_ID)){

            statement.setInt(1, person.getPerson_id());
            deleteDone = statement.execute();

        }catch (SQLException ex){
            ex.printStackTrace();
        }

        return deleteDone;
    }
}
