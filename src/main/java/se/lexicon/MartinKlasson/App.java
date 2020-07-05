package se.lexicon.MartinKlasson;


import se.lexicon.MartinKlasson.data.Person;
import se.lexicon.MartinKlasson.data.ToDo;
import se.lexicon.MartinKlasson.data.ToDoItDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class App {
    public static void main( String[] args ) throws SQLException {
        ToDoItDataSource.getConnection();


//__________________________TEST OF ToDo - create
//        ToDo toDo = new ToDo();
//        ToDo toDoItem1 = new ToDo("Harry's", "Clean the bar", LocalDate.parse("2020-07-11"), false);
//        toDo.create(toDoItem1);
//
//__________________________TEST OF Person - findById

        //Wrong - print out all, both false and true....
        Person person = new Person();
        Person person1 = person.findById(1).get();
//        System.out.println(person1);

//--------------------------TEST OF ToDo - findById
//        ToDo toDo = new ToDo();
//        ToDo toDo1 = toDo.findById(1).get();
//        System.out.println(toDo1);

//--------------------------TEST OF ToDo - findAll
//        ToDo toDo = new ToDo();
//        Collection<ToDo> toDoArrayList = new ArrayList<ToDo>();
//        toDoArrayList = toDo.findAll();
//        toDoArrayList.forEach(System.out::println);


 //-------------------------TEST OF ToDo - update !!! CAN*T UPDATE ASSIGNEE
//        ToDo toDo = new ToDo();
//        ToDo toDo1 = toDo.findById(3).get();
//        toDo1.setTitle("Stina W's");
//        toDo1.setDescription("Clean kitchen");
//        toDo1.setDeadLine(LocalDate.parse("2020-07-14"));
//        toDo1.setDone(false);
        //setAssignee - have to change method implementation
        //toDo1.setAssignee(person1);
//        System.out.println(toDo.update(toDo1));

//--------------------------TEST OF ToDo - findByDone
//        ToDo toDo = new ToDo();
//        Collection<ToDo> toDoArrayList = new ArrayList<ToDo>();
//        toDoArrayList = toDo.findByDoneStatus(false);
//        toDoArrayList.forEach(System.out::println);
//
//___________________________TEST OF TODO - findByAssignee(int id)
//        ToDo toDo = new ToDo();
//        ToDo toDo1 = toDo.findByAssignee(1).get();
//        System.out.println(toDo1);
   }
}
