package se.lexicon.MartinKlasson.data;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ToDoItems {

    public ToDo create(ToDo toDo);
    public Collection<ToDo> findAll();
    public Optional<ToDo> findById(int toDoId);
    public List<ToDo> findByDoneStatus(Boolean isDone) throws SQLException;
    public Optional<ToDo> findByAssignee(int assigneeId);
    public List<ToDo> findByAssignee(Person person);
    public List<ToDo> findByUnassignedToDoItems();
    public ToDo update(ToDo todoItem);
    public Boolean deleteById(int toDoId);

}
