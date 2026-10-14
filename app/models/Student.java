package models;

public class Student{
    public Long id;
    public String name;
    public int age;
    public String course;

    public Student(){
        //empty constructor
    }

    public Student(Long id, String name, int age, String course){
        this.id=id;
        this.name=name;
        this.age=age;
        this.course=course;
    }

}