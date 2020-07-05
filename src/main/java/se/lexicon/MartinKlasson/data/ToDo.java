package se.lexicon.MartinKlasson.data;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.Date;

public class ToDo implements ToDoItems {

    private int toDoId;
    private String title;
    private String description;
    private LocalDate deadLine;
    private boolean done;
    private Person assignee;

    public ToDo(int toDoId, String title, String description, LocalDate deadLine, boolean done, Person assignee) {
        this.toDoId = toDoId;
        this.title = title;
        this.description = description;
        this.deadLine = deadLine;
        this.done = done;
        this.assignee = assignee;
    }

    public ToDo(int toDoId, String title, String description, LocalDate deadLine, boolean done) {
        this.toDoId = toDoId;
        this.title = title;
        this.description = description;
        this.deadLine = deadLine;
        this.done = done;
    }

    public ToDo(){

    }

    public ToDo(String title, String description, LocalDate deadLine, boolean done, Person assignee) {
        this(0, title, description,deadLine, done, assignee);
    }

    public ToDo(String title, String description, LocalDate deadLine, boolean done) {
        this(title, description, deadLine, done, null);
    }

    public int getToDoId() {
        return toDoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(LocalDate deadLine) {
        this.deadLine = deadLine;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Person getAssignee() {
        return assignee;
    }

    public void setAssignee(Person assignee) {
        this.assignee = assignee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToDo toDo = (ToDo) o;
        return done == toDo.done &&
                Objects.equals(title, toDo.title) &&
                Objects.equals(description, toDo.description) &&
                Objects.equals(deadLine, toDo.deadLine) &&
                Objects.equals(assignee, toDo.assignee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, deadLine, done, assignee);
    }

    @Override
    public String toString() {
        return "ToDo{" +
                "toDoId=" + toDoId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", deadLine=" + deadLine +
                ", done=" + done +
                ", assignee=" + assignee +
                '}';
    }

    @Override
    public ToDo create(ToDo newToDo) {
        if(newToDo.getToDoId() != 0){
            throw new IllegalArgumentException();
        }
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet keySet = null;
        try{
            connection = ToDoItDataSource.getConnection();
            statement = connection.prepareStatement("INSERT INTO todo_item (title, description, deadline, done) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, newToDo.getTitle());
            statement.setString(2, newToDo.getDescription());
            statement.setObject(3, newToDo.getDeadLine());
            statement.setBoolean(4, newToDo.isDone());
            statement.execute();
            keySet = statement.getGeneratedKeys();
            while (keySet.next()){
                newToDo = new ToDo(
                        keySet.getInt(1),
                        newToDo.getTitle(),
                        newToDo.getDescription(),
                        newToDo.getDeadLine(),
                        newToDo.isDone()
                );
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        finally {
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
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return newToDo;
    }

    @Override
    public Collection<ToDo> findAll() {
        List<ToDo> result = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = ToDoItDataSource.getConnection();
            statement = connection.prepareStatement("SELECT * FROM todo_item");
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                result.add(createToDoFromResultSet(resultSet));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return result;
    }


    private PreparedStatement createFindByIdStatement(Connection connection, String sql, int id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        return statement;
    }

    private ToDo createToDoFromResultSet(ResultSet resultSet) throws SQLException {
         ToDo toDoItem = new ToDo(
                resultSet.getInt("todo_id"),
                resultSet.getString("title"),
                resultSet.getString("description"),
                resultSet.getObject("deadline",LocalDate.class),
                resultSet.getBoolean("done"),
                null
        );

//        // Can't get it working -- setAssigneeId is wrong in some way...
        Person person = new Person();
        Optional<Person> optionalPerson = person.findById(resultSet.getInt("assignee_id"));
        if(optionalPerson.isPresent()){
           toDoItem.setAssignee(optionalPerson.get());
        }
        return toDoItem;
    }
//------------------------
//    findById method compiled in App and work:
//    ToDo toDo = new ToDo();
//    ToDo toDo1 = toDo.findById(1).get();
//    System.out.println(toDo1);
//--------------------------
    private static final String FIND_BY_ID = "SELECT * FROM todo_item WHERE todo_id = ?";
    @Override
    public Optional<ToDo> findById(int toDoId) {
        Optional<ToDo> result = Optional.empty();
        try(
                Connection connection = ToDoItDataSource.getConnection();
                PreparedStatement statement = createFindByIdStatement(connection, FIND_BY_ID, toDoId);
                ResultSet resultSet = statement.executeQuery()
        ){
            while (resultSet.next()){
                result = Optional.of(createToDoFromResultSet(resultSet));
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return result;
    }

    private PreparedStatement createFindByDoneStatement(Connection connection, String sql, boolean isDone) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setBoolean(1, isDone);
        return statement;
    }

    private static final String FIND_BY_DONE = "SELECT * FROM todo_item WHERE done = ?";
    @Override
    public List<ToDo> findByDoneStatus(Boolean isDone) throws SQLException {

        List<ToDo> result = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = ToDoItDataSource.getConnection();
            statement = connection.prepareStatement(FIND_BY_DONE);
            statement.setBoolean(1, isDone);
            resultSet = statement.executeQuery();
            while (resultSet.next()){
                result.add(createToDoFromResultSet(resultSet));
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

    //I havn't been able to solve problem with adding assignee to database.
    private PreparedStatement createFindByAssigneeId(Connection connection, String sql, int assigneeId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, assigneeId);
        return statement;
    }

    private static final String FIND_BY_ASSIGNEE = "SELECT * FROM todo_item WHERE assignee_id = ?";
    @Override
    public Optional<ToDo> findByAssignee(int assigneeId) {
        Optional<ToDo> result = Optional.empty();
        try(
                Connection connection = ToDoItDataSource.getConnection();
                PreparedStatement statement = createFindByAssigneeId(connection, FIND_BY_ASSIGNEE, assigneeId);
                ResultSet resultSet = statement.executeQuery()
        ){
            while (resultSet.next()){
                result = Optional.of(createToDoFromResultSet(resultSet));
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return result;

    }
    //I havn't been able to solve problem with adding assignee to database.
    @Override
    public List<ToDo> findByAssignee(Person person) {
        return null;
    }
    //I havn't been able to solve problem with adding assignee to database.
    @Override
    public List<ToDo> findByUnassignedToDoItems() {
        return null;
    }

    public static final String UPDATE_TODO = "UPDATE todo_item SET title = ?, description = ?, deadline = ?, done = ?, assignee_id = ? WHERE todo_id = ?";
    @Override
    public ToDo update(ToDo todoItem) {
        if(todoItem.getToDoId() == 0){
            throw new IllegalArgumentException("TodoId can't be updated because it s still not in database");
        }
        try(Connection connection = ToDoItDataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(UPDATE_TODO)){
            statement.setString(1, todoItem.getTitle());
            statement.setString(2, todoItem.getDescription());
            statement.setObject(3, todoItem.getDeadLine());
            statement.setBoolean(4, todoItem.isDone());
            statement.setObject(5, todoItem.getAssignee());
            statement.setInt(6, todoItem.getToDoId());
            statement.execute();

        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return todoItem;
    }

    @Override
    public Boolean deleteById(int toDoId) {
        return null;
    }



}
