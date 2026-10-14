package controllers;

import models.Student;
import services.StudentService;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import javax.inject.Inject;

import java.util.List;
import java.util.Optional;

public class StudentController extends Controller {

    private final StudentService studentService;

    @Inject
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // GET /students
    public Result getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ok(Json.toJson(students));
    }

    // GET /students/:id
    public Result getStudentById(Long id) {

        if (id <= 0) {
            return badRequest(error("ID must be positive"));
        }

        Optional<Student> student =
                studentService.getStudentById(id);

        if (student.isEmpty()) {
            return notFound(error("Student not found"));
        }

        return ok(Json.toJson(student.get()));
    }

    // POST /students
    public Result createStudent(Http.Request request) {

        JsonNode json = request.body().asJson();

        if (json == null || !json.isObject()) {
            return badRequest(error("A JSON object is required"));
        }

        try {
            Student student = readStudent(json);
            Student created = studentService.createStudent(student);

            // Sends the response with status code 201 Created
            return created(Json.toJson(created));

        } catch (IllegalArgumentException e) {
            // Returns 400 Bad Request for invalid inputs
            return badRequest(error(e.getMessage()));
        }
    }

    // PUT /students/:id
    public Result updateStudent(Long id, Http.Request request) {

        if (id <= 0) {
            return badRequest(error("ID must be positive"));
        }

        JsonNode json = request.body().asJson();

        if (json == null || !json.isObject()) {
            return badRequest(error("A JSON object is required"));
        }

        try {
            Student student = readStudent(json);

            boolean updated =
                    studentService.updateStudent(id, student);

            if (!updated) {
                return notFound(error("Student not found"));
            }

            student.id = id;

            return ok(Json.toJson(student));

        } catch (IllegalArgumentException e) {
            // Returns 400 Bad Request for invalid inputs
            return badRequest(error(e.getMessage()));
        }
    }

    // DELETE /students/:id
    public Result deleteStudent(Long id) {

        if (id <= 0) {
            return badRequest(error("ID must be positive"));
        }

        boolean deleted = studentService.deleteStudent(id);

        if (!deleted) {
            return notFound(error("Student not found"));
        }

        return noContent();
    }

    // Convert request JSON into a Student object
    private Student readStudent(JsonNode json) {

        if (!json.hasNonNull("name")
                || !json.get("name").isTextual()) {
            throw new IllegalArgumentException(
                    "Name must be a string"
            );
        }

        if (!json.hasNonNull("age")
                || !json.get("age").isIntegralNumber()
                || !json.get("age").canConvertToInt()) {
            throw new IllegalArgumentException(
                    "Age must be an integer"
            );
        }

        if (!json.hasNonNull("course")
                || !json.get("course").isTextual()) {
            throw new IllegalArgumentException(
                    "Course must be a string"
            );
        }

        Student student = new Student();

        student.name = json.get("name").asText();
        student.age = json.get("age").asInt();
        student.course = json.get("course").asText();

        return student;
    }

    // Create a JSON error response
    private JsonNode error(String message) {
        return Json.newObject().put("error", message);
    }
}
