package com.personal.project.todolist;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TodoListApplicationTests {

	@Test
	void testMain() {
		TodoListApplication.main(new String[]{""});
	}

	@Test
	void contextLoads() {
	}

}
