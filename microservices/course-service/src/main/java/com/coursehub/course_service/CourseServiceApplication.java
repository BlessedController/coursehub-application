package com.coursehub.course_service;

import com.coursehub.course_service.model.Category;
import com.coursehub.course_service.model.Course;
import com.coursehub.course_service.repository.CategoryRepository;
import com.coursehub.course_service.repository.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static com.coursehub.course_service.model.enums.CourseStatus.PUBLISHED;

@SpringBootApplication
@EnableFeignClients
@EnableKafka
public class CourseServiceApplication implements CommandLineRunner {
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    public CourseServiceApplication(CategoryRepository categoryRepository, CourseRepository courseRepository) {
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(CourseServiceApplication.class, args);
    }

    @Override
    public void run(String... args) {
        createCategoryIfNotExist();
        createCourseIfNotExist();
    }

    void createCategoryIfNotExist() {
        String programmingCategoryName = "Programming";
        String javaCategoryName = "Java";
        String cSharpCategoryName = "C#";


        boolean isProgrammingCategoryExists = categoryRepository.existsByName(programmingCategoryName);
        boolean isJavaCategoryExists = categoryRepository.existsByName(javaCategoryName);
        boolean isCSharpCategoryExists = categoryRepository.existsByName(cSharpCategoryName);

        if (!isProgrammingCategoryExists) {
            Category category = Category.builder()
                    .name("Programming")
                    .parentCategory(null)
                    .build();
            categoryRepository.save(category);
        }
        if (!isJavaCategoryExists) {
            Category parent = categoryRepository.findByName(programmingCategoryName).orElse(null);

            Category javaCat = Category.builder()
                    .name("Java")
                    .parentCategory(parent)
                    .build();
            categoryRepository.save(javaCat);
        }

        if (!isCSharpCategoryExists) {
            Category parent = categoryRepository.findByName(programmingCategoryName).orElse(null);

            Category cCat = Category.builder()
                    .name("C#")
                    .parentCategory(parent)
                    .build();
            categoryRepository.save(cCat);
        }

    }


    private void createCourseIfNotExist() {

        String javaCourseName = "Java Programming for Beginners";
        String cSharpCourseName = "C# Programming for Beginners";

        boolean isJavaCourseExists = courseRepository.existsByTitle(javaCourseName);
        boolean isCSharpCourseExists = courseRepository.existsByTitle(cSharpCourseName);

        Category ProgramminCategory = categoryRepository.findByName("Programming").orElse(null);

        if (!isJavaCourseExists) {
            Category javacategory = categoryRepository.findByName("Java").orElse(null);


            assert javacategory != null;
            assert ProgramminCategory != null;
            Course javaCourse = Course.builder()
                    .instructorId("e39b30f6-7479-41aa-89dd-c90e4c93c42c")
                    .title("Java Programming for Beginners")
                    .price(BigDecimal.valueOf(49.99))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .rating(4.5)
                    .ratingCount(300L)
                    .status(PUBLISHED)
                    .description("Learn Java programming from scratch with hands-on projects and real-world examples. Perfect for absolute beginners.")
                    .categories(Set.of(ProgramminCategory, javacategory))
                    .build();

            courseRepository.save(javaCourse);
        }
        if (!isCSharpCourseExists) {

            Category cSharpCategory = categoryRepository.findByName("C#").orElse(null);

            assert ProgramminCategory != null;
            assert cSharpCategory != null;
            Course cSharpCourse = Course.builder()
                    .instructorId("e39b30f6-7479-41aa-89dd-c90e4c93c42c")
                    .title("C# Programming for Beginners")
                    .price(BigDecimal.valueOf(59.99))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .rating(4.3)
                    .ratingCount(200L)
                    .status(PUBLISHED)
                    .description("Learn C# programming from scratch with hands-on projects and real-world examples. Perfect for absolute beginners.")
                    .categories(Set.of(ProgramminCategory, cSharpCategory))
                    .build();

            courseRepository.save(cSharpCourse);
        }

    }


}
