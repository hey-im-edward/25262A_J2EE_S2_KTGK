package com.example.courseregistration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
	"spring.datasource.url=jdbc:h2:mem:context_load_test;DB_CLOSE_DELAY=-1;MODE=LEGACY",
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
class CourseRegistrationApplicationTests {

	@Test
	void contextLoads() {
	}

}
