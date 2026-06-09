package com.edututor.config;

import com.edututor.entity.*;
import com.edututor.repository.CategoryRepository;
import com.edututor.repository.CourseRepository;
import com.edututor.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Tworzy konta startowe i przykładowe dane przy pierwszym uruchomieniu,
 * jeśli baza jest pusta. Dzięki temu od razu można zalogować się jako
 * nauczyciel i administrator (rejestracja przez API tworzy tylko studenta).
 *
 *  Dane logowania:
 *    admin@edututor.pl   / admin123    (ADMIN)
 *    teacher@edututor.pl / teacher123  (TEACHER)
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, CategoryRepository categoryRepository,
                           CourseRepository courseRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // baza już zainicjowana — nic nie rób
        }

        Admin admin = new Admin();
        admin.setName("Administrator");
        admin.setEmail("admin@edututor.pl");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        userRepository.save(admin);

        Teacher teacher = new Teacher();
        teacher.setName("Jan Korepetytor");
        teacher.setEmail("teacher@edututor.pl");
        teacher.setPasswordHash(passwordEncoder.encode("teacher123"));
        teacher = userRepository.save(teacher);

        Student student = new Student();
        student.setName("Anna Uczennica");
        student.setEmail("student@edututor.pl");
        student.setPasswordHash(passwordEncoder.encode("student123"));
        userRepository.save(student);

        Category math = new Category();
        math.setName("Matematyka");
        math.setDescription("Kursy z matematyki");
        math.setIcon("\uD83D\uDCD0");
        math.setCreatedBy(teacher.getId());
        math = categoryRepository.save(math);

        Course course = new Course();
        course.setTitle("Wprowadzenie do algebry");
        course.setDescription("Przykładowy kurs startowy obejmujący podstawy algebry.");
        course.setStatus(CourseStatus.PUBLISHED);
        course.setCategory(math);
        course.setTeacher(teacher);
        courseRepository.save(course);
    }
}
