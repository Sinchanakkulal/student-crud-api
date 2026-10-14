package repositories;

import models.Student;
import play.db.Database;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentRepository {
    private final Database database;

    // dependency injection
    @Inject
    public StudentRepository(Database database) {
        this.database = database;
    }

    // get all the students
    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        String sql = "select id, name, age, course from students";

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                students.add(new Student(
                    result.getLong("id"),
                    result.getString("name"),
                    result.getInt("age"),
                    result.getString("course")
                ));
            }
            return students;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch students", e);
        }
    }

    // get a student by id
    public Optional<Student> findById(Long id) {
        String sql = "select id, name, age, course from students where id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(new Student(
                        result.getLong("id"),
                        result.getString("name"),
                        result.getInt("age"),
                        result.getString("course")
                    ));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch the student", e);
        }
    }

    // create a new Student
    public Student create(Student student) {
        String sql = "insert into students (name, age, course) values (?, ?, ?)";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, student.name);
            statement.setInt(2, student.age);
            statement.setString(3, student.course);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Creating student failed, no rows affected.");
            }

            try (ResultSet result = statement.getGeneratedKeys()) {
                if (result.next()) {
                    student.id = result.getLong(1);
                }
            }
            return student;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create the student", e);
        }
    }

    // Update an existing student
    public boolean update(Long id, Student student) {
        String sql = "update students set name = ?, age = ?, course = ? where id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, student.name);
            statement.setInt(2, student.age);
            statement.setString(3, student.course);
            statement.setLong(4, id);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update the student", e);
        }
    }

    // Delete a student
    public boolean delete(Long id) {
        String sql = "delete from students where id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete the student", e);
        }
    }
}