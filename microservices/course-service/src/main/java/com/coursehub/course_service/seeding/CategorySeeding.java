package com.coursehub.course_service.seeding;

import com.coursehub.course_service.model.Category;
import com.coursehub.course_service.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static com.coursehub.course_service.model.enums.CategoryStatus.ACTIVE;

@Component
@RequiredArgsConstructor
public class CategorySeeding implements CommandLineRunner {
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        Category programming = new Category("1", "Programming", ACTIVE, null, new ArrayList<>());

        categoryRepository.save(programming);

        Category java = new Category("2", "Java", ACTIVE, programming, new ArrayList<>());

        categoryRepository.save(java);


        Category cSharp = new Category("3", "C#", ACTIVE, programming, new ArrayList<>());

        categoryRepository.save(cSharp);


        Category phyton = new Category("4", "Phyton", ACTIVE, programming, new ArrayList<>());

        categoryRepository.save(phyton);

    }
}
