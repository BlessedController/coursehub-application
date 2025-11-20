package com.coursehub.identity_service.feeding;

import com.coursehub.commons.security.UserRole;
import com.coursehub.commons.security.UserStatus;
import com.coursehub.identity_service.model.User;
import com.coursehub.identity_service.model.enums.Gender;
import com.coursehub.identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAndContentCreatorFeeding implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        String password = passwordEncoder.encode("password");

        List<User> contentCreators = List.of(
                new User(null, "ironman.tech", "ironman.tech@example.com", "+994-51-523-81-46",
                        password, "Tony", "Stark", UUID.randomUUID().toString(), Gender.MALE,
                        "Building suits and teaching engineering concepts.", 4.92,
                        UserRole.ROLE_CONTENT_CREATOR, UserStatus.ACTIVE, true, null, null),

                new User(null, "heisenberg.codes", "heisenberg.codes@example.com", "+994-51-438-92-17",
                        password, "Walter", "White", UUID.randomUUID().toString(), Gender.MALE,
                        "Breaking bad code habits since 2008.", 4.73,
                        UserRole.ROLE_CONTENT_CREATOR, UserStatus.ACTIVE, true, null, null),

                new User(null, "t.shelby.dev", "t.shelby.dev@example.com", "+994-51-574-60-29",
                        password, "Thomas", "Shelby", UUID.randomUUID().toString(), Gender.MALE,
                        "By order of the Peaky Coders.", 4.81,
                        UserRole.ROLE_CONTENT_CREATOR, UserStatus.ACTIVE, true, null, null),

                new User(null, "sheldon.science", "sheldon.science@example.com", "+994-51-690-45-33",
                        password, "Sheldon", "Cooper", UUID.randomUUID().toString(), Gender.MALE,
                        "Explaining physics and coding with precision.", 4.67,
                        UserRole.ROLE_CONTENT_CREATOR, UserStatus.ACTIVE, true, null, null),

                new User(null, "jon.snow.tech", "jon.snow.tech@example.com", "+994-51-712-84-09",
                        password, "Jon", "Snow", UUID.randomUUID().toString(), Gender.MALE,
                        "I know nothing… except backend development.", 4.55,
                        UserRole.ROLE_CONTENT_CREATOR, UserStatus.ACTIVE, true, null, null),

                new User(null, "khaleesi.codes", "khaleesi.codes@example.com", "+994-51-609-73-58",
                        password, "Daenerys", "Targaryen", UUID.randomUUID().toString(), Gender.FEMALE,
                        "Mother of Dragons & Microservices.", 4.94,
                        UserRole.ROLE_CONTENT_CREATOR, UserStatus.ACTIVE, true, null, null),

                new User(null, "rick.portal.dev", "rick.portal.dev@example.com", "+994-51-821-90-64",
                        password, "Rick", "Sanchez", UUID.randomUUID().toString(), Gender.MALE,
                        "Wubba lubba dub dub! Teaching multiverse coding.", 4.88,
                        UserRole.ROLE_CONTENT_CREATOR, UserStatus.ACTIVE, true, null, null),

                new User(null, "sherlock.solve", "sherlock.solve@example.com", "+994-51-478-56-12",
                        password, "Sherlock", "Holmes", UUID.randomUUID().toString(), Gender.MALE,
                        "Observing, deducing, debugging.", 4.76,
                        UserRole.ROLE_CONTENT_CREATOR, UserStatus.ACTIVE, true, null, null),

                new User(null, "batman.engineer", "batman.engineer@example.com", "+994-51-547-38-01",
                        password, "Bruce", "Wayne", UUID.randomUUID().toString(), Gender.MALE,
                        "Gotham by night, backend by day.", 4.69,
                        UserRole.ROLE_CONTENT_CREATOR, UserStatus.ACTIVE, true, null, null),

                new User(null, "captain.jack", "captain.jack@example.com", "+994-51-694-27-95",
                        password, "Jack", "Sparrow", UUID.randomUUID().toString(), Gender.MALE,
                        "Why is the code always gone?", 4.44,
                        UserRole.ROLE_CONTENT_CREATOR, UserStatus.ACTIVE, true, null, null),

                new User(null, "loki.tricks", "loki.tricks@example.com", "+994-51-737-14-20",
                        password, "Loki", "Odinson", UUID.randomUUID().toString(), Gender.MALE,
                        "God of Mischief & creative coding.", 4.85,
                        UserRole.ROLE_CONTENT_CREATOR, UserStatus.ACTIVE, true, null, null),

                new User(null, "harry.magic.dev", "harry.magic.dev@example.com", "+994-51-822-43-77",
                        password, "Harry", "Potter", UUID.randomUUID().toString(), Gender.MALE,
                        "Coding spells in Java & Spring.", 4.61,
                        UserRole.ROLE_CONTENT_CREATOR, UserStatus.ACTIVE, true, null, null),

                new User(null, "bruce.hulk", "bruce.hulk@example.com", "+994-51-905-86-30",
                        password, "Bruce", "Banner", UUID.randomUUID().toString(), Gender.MALE,
                        "Don’t make me angry… bad code will do.", 4.59,
                        UserRole.ROLE_CONTENT_CREATOR, UserStatus.ACTIVE, true, null, null),

                new User(null, "katniss.react", "katniss.react@example.com", "+994-51-833-27-18",
                        password, "Katniss", "Everdeen", UUID.randomUUID().toString(), Gender.FEMALE,
                        "Frontend skills sharp as an arrow.", 4.72,
                        UserRole.ROLE_CONTENT_CREATOR, UserStatus.ACTIVE, true, null, null),

                new User(null, "neo.matrix", "neo.matrix@example.com", "+994-51-954-63-72",
                        password, "Neo", "Anderson", UUID.randomUUID().toString(), Gender.MALE,
                        "There is no spoon… only clean code.", 4.91,
                        UserRole.ROLE_CONTENT_CREATOR, UserStatus.ACTIVE, true, null, null),

                new User(null, "strider.king", "strider.king@example.com", "+994-51-681-42-59",
                        password, "Aragorn", "Elessar", UUID.randomUUID().toString(), Gender.MALE,
                        "One codebase to rule them all.", 4.78,
                        UserRole.ROLE_CONTENT_CREATOR, UserStatus.ACTIVE, true, null, null),

                new User(null, "legolas.swift", "legolas.swift@example.com", "+994-51-590-26-11",
                        password, "Legolas", "Greenleaf", UUID.randomUUID().toString(), Gender.MALE,
                        "Shooting arrows and shipping features.", 4.66,
                        UserRole.ROLE_CONTENT_CREATOR, UserStatus.ACTIVE, true, null, null),

                new User(null, "frodo.shire", "frodo.shire@example.com", "+994-51-835-78-24",
                        password, "Frodo", "Baggins", UUID.randomUUID().toString(), Gender.MALE,
                        "Carrying code burdens to Mordor.", 4.75,
                        UserRole.ROLE_CONTENT_CREATOR, UserStatus.ACTIVE, true, null, null),

                new User(null, "yoda.wisdom", "yoda.wisdom@example.com", "+994-51-723-17-06",
                        password, "Yoda", "Master", UUID.randomUUID().toString(), Gender.MALE,
                        "Clean code, you must learn.", 4.98,
                        UserRole.ROLE_CONTENT_CREATOR, UserStatus.ACTIVE, true, null, null),

                new User(null, "obiwan.dev", "obiwan.dev@example.com", "+994-51-915-44-83",
                        password, "ObiWan", "Kenobi", UUID.randomUUID().toString(), Gender.MALE,
                        "Hello there. Let's debug.", 4.82,
                        UserRole.ROLE_CONTENT_CREATOR, UserStatus.ACTIVE, true, null, null)
        );

        userRepository.saveAll(contentCreators);

        List<User> users = List.of(
                new User(null, "david.miller", "david.miller@example.com", "+994-51-501-71-42",
                        password, "David", "Miller", UUID.randomUUID().toString(), Gender.MALE,
                        "Just exploring the platform and learning.", 0.0,
                        UserRole.ROLE_USER, UserStatus.ACTIVE, true, null, null),

                new User(null, "olivia.jackson", "olivia.jackson@example.com", "+994-51-522-64-19",
                        password, "Olivia", "Jackson", UUID.randomUUID().toString(), Gender.FEMALE,
                        "Love discovering new content every day.", 0.0,
                        UserRole.ROLE_USER, UserStatus.ACTIVE, true, null, null),

                new User(null, "daniel.harris", "daniel.harris@example.com", "+994-51-580-12-88",
                        password, "Daniel", "Harris", UUID.randomUUID().toString(), Gender.MALE,
                        "Learning backend and enjoying the journey.", 0.0,
                        UserRole.ROLE_USER, UserStatus.ACTIVE, true, null, null),

                new User(null, "grace.lewis", "grace.lewis@example.com", "+994-51-619-42-76",
                        password, "Grace", "Lewis", UUID.randomUUID().toString(), Gender.FEMALE,
                        "Passionate about creativity and tech.", 0.0,
                        UserRole.ROLE_USER, UserStatus.ACTIVE, true, null, null),

                new User(null, "samuel.lee", "samuel.lee@example.com", "+994-51-693-15-04",
                        password, "Samuel", "Lee", UUID.randomUUID().toString(), Gender.MALE,
                        "Enjoying the platform casually.", 0.0,
                        UserRole.ROLE_USER, UserStatus.ACTIVE, true, null, null),

                new User(null, "nora.benson", "nora.benson@example.com", "+994-51-734-58-21",
                        password, "Nora", "Benson", UUID.randomUUID().toString(), Gender.FEMALE,
                        "Always curious and love learning new things.", 0.0,
                        UserRole.ROLE_USER, UserStatus.ACTIVE, true, null, null),

                new User(null, "jack.turner", "jack.turner@example.com", "+994-51-745-20-98",
                        password, "Jack", "Turner", UUID.randomUUID().toString(), Gender.MALE,
                        "Interested in tech and simple tools.", 0.0,
                        UserRole.ROLE_USER, UserStatus.ACTIVE, true, null, null),

                new User(null, "chloe.roberts", "chloe.roberts@example.com", "+994-51-784-69-00",
                        password, "Chloe", "Roberts", UUID.randomUUID().toString(), Gender.FEMALE,
                        "Exploring content and learning daily.", 0.0,
                        UserRole.ROLE_USER, UserStatus.ACTIVE, true, null, null),

                new User(null, "leo.foster", "leo.foster@example.com", "+994-51-815-37-44",
                        password, "Leo", "Foster", UUID.randomUUID().toString(), Gender.MALE,
                        "Enjoying simple and helpful content.", 0.0,
                        UserRole.ROLE_USER, UserStatus.ACTIVE, true, null, null),

                new User(null, "ariana.cole", "ariana.cole@example.com", "+994-51-829-14-53",
                        password, "Ariana", "Cole", UUID.randomUUID().toString(), Gender.FEMALE,
                        "Active learner, always exploring new topics.", 0.0,
                        UserRole.ROLE_USER, UserStatus.ACTIVE, true, null, null),

                new User(null, "evan.brooks", "evan.brooks@example.com", "+994-51-841-27-62",
                        password, "Evan", "Brooks", UUID.randomUUID().toString(), Gender.MALE,
                        "Just enjoying microlearning daily.", 0.0,
                        UserRole.ROLE_USER, UserStatus.ACTIVE, true, null, null),

                new User(null, "zoe.morgan", "zoe.morgan@example.com", "+994-51-855-30-71",
                        password, "Zoe", "Morgan", UUID.randomUUID().toString(), Gender.FEMALE,
                        "Love discovering useful tech content.", 0.0,
                        UserRole.ROLE_USER, UserStatus.ACTIVE, true, null, null),

                new User(null, "miles.perry", "miles.perry@example.com", "+994-51-868-04-28",
                        password, "Miles", "Perry", UUID.randomUUID().toString(), Gender.MALE,
                        "Browsing the platform to learn new topics.", 0.0,
                        UserRole.ROLE_USER, UserStatus.ACTIVE, true, null, null),

                new User(null, "hannah.price", "hannah.price@example.com", "+994-51-902-76-41",
                        password, "Hannah", "Price", UUID.randomUUID().toString(), Gender.FEMALE,
                        "Friendly user exploring creative spaces.", 0.0,
                        UserRole.ROLE_USER, UserStatus.ACTIVE, true, null, null),

                new User(null, "ryan.watson", "ryan.watson@example.com", "+994-51-913-83-57",
                        password, "Ryan", "Watson", UUID.randomUUID().toString(), Gender.MALE,
                        "Enjoying the content daily.", 0.0,
                        UserRole.ROLE_USER, UserStatus.ACTIVE, true, null, null),

                new User(null, "bella.brooks", "bella.brooks@example.com", "+994-51-926-44-68",
                        password, "Bella", "Brooks", UUID.randomUUID().toString(), Gender.FEMALE,
                        "Love simple and friendly learning.", 0.0,
                        UserRole.ROLE_USER, UserStatus.ACTIVE, true, null, null),

                new User(null, "max.henderson", "max.henderson@example.com", "+994-51-938-59-03",
                        password, "Max", "Henderson", UUID.randomUUID().toString(), Gender.MALE,
                        "Tech fan exploring tools and guides.", 0.0,
                        UserRole.ROLE_USER, UserStatus.ACTIVE, true, null, null),

                new User(null, "lily.reed", "lily.reed@example.com", "+994-51-945-70-22",
                        password, "Lily", "Reed", UUID.randomUUID().toString(), Gender.FEMALE,
                        "Learning and trying new technologies.", 0.0,
                        UserRole.ROLE_USER, UserStatus.ACTIVE, true, null, null),

                new User(null, "adrian.cook", "adrian.cook@example.com", "+994-51-957-11-80",
                        password, "Adrian", "Cook", UUID.randomUUID().toString(), Gender.MALE,
                        "Curious learner exploring the platform.", 0.0,
                        UserRole.ROLE_USER, UserStatus.ACTIVE, true, null, null),

                new User(null, "clara.hayes", "clara.hayes@example.com", "+994-51-968-23-45",
                        password, "Clara", "Hayes", UUID.randomUUID().toString(), Gender.FEMALE,
                        "Enjoying microlearning and simple guides.", 0.0,
                        UserRole.ROLE_USER, UserStatus.ACTIVE, true, null, null)
        );


        userRepository.saveAll(users);


    }
}
