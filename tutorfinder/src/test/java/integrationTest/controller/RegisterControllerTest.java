package integrationTest.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration @ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/config/springMVC.xml", 
		"file:src/main/webapp/WEB-INF/config/springData.xml" }) 

@Transactional 
@TransactionConfiguration(defaultRollback = true) 
public class RegisterControllerTest { 
	
	@Autowired private WebApplicationContext wac; 

	private MockMvc mockMvc;


	@Before public void setup() {
	this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build(); } 

	@Test
	public void ValidSignUpTest() throws Exception {
		this.mockMvc
		.perform(
				post("/create").param("email", "test@test.test")
				.param("firstName", "test")
				.param("lastName", "test")
				.param("password", "testtest")
				.param("confirmPassword", "testtest")
				.param("tutor", "true"))
					.andExpect(status().isOk())
					.andExpect(model().hasNoErrors());
		}
	
	@Test
	public void InvalidEmail() throws Exception {
		this.mockMvc
		.perform(
				post("/create").param("email", "invalidEmail")
				.param("firstName", "test")
				.param("lastName", "test")
				.param("password", "testtest")
				.param("confirmPassword", "testtest")
				.param("tutor", "true"))
					.andExpect(model().errorCount(1));
		//TODO check which error was added
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void NullFirstName() throws Exception {
		this.mockMvc
		.perform(
				post("/create").param("email", "test@test.test")
				.param(null, "test")
				.param("lastName", "test")
				.param("password", "testtest")
				.param("confirmPassword", "testtest")
				.param("tutor", "true"));
		
	}
	
	
	
	}