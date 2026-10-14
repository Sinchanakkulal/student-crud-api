//A service contains the application's business logic. For example, it can check whether a student's age is valid before saving the student.

package services;

import models.Student;
import repositories.StudentRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class StudentService{
    private final StudentRepository studentRepository;

    @Inject
    public StudentService(StudentRepository studentRepository){
        this.studentRepository=studentRepository;
    }

    //get all students
    public List<Student> getAllStudents(){
        return studentRepository.findAll();
    }

    // Get one student by ID
    public Optional<Student> getStudentById(Long id){
        return studentRepository.findById(id);
    }

    // Add a new student
    public Student createStudent(Student student){
        validateStudent(student);
        return studentRepository.create(student);
    }

    // Update a student
    public boolean updateStudent(Long id,Student student){
        validateStudent(student);
        return studentRepository.update(id,student);
    }

    // Delete a student
    public boolean deleteStudent(Long id){
        return studentRepository.delete(id);
    }

    //check a student data
    private void validateStudent(Student student){
        if(student==null){
            throw new IllegalArgumentException("student data is required");
        }

        if(student.name == null || student.name.isBlank()){
            throw new IllegalArgumentException("Student name is required");
        }
        if(student.age <= 0 || student.age>120){
            throw new IllegalArgumentException("student's age is inbetween 1 and 120");
        }
        if(student.course==null || student.course.isBlank()){
            throw new IllegalArgumentException("Student course is required");
        }
    }
}