package ch.unibe.ese.Tutorfinder.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ch.unibe.ese.Tutorfinder.controller.exceptions.InvalidEmailException;
import ch.unibe.ese.Tutorfinder.controller.pojos.Forms.SignupForm;
import ch.unibe.ese.Tutorfinder.controller.service.RegisterService;
import ch.unibe.ese.Tutorfinder.model.dao.UserDao;

/**
 * Provides ModelAndView objects for the Spring MVC to load pages relevant to
 * the login/logout and register process.
 * 
 * @version 1.0.1
 *
 */
@Controller
public class LoginController {

	@Autowired
	RegisterService registerService;
	@Autowired	
	UserDao userDao;
	
	/**
	 * Constructor for testing purposes
	 * @param registerService
	 */
	@Autowired
	public LoginController(RegisterService registerService) {
		this.registerService = registerService;
	}

	/**
	 * Redirects the {@code /} and {@code /home} to the 
	 * {@code login.html} page
	 * @return redirection to login
	 */
	@RequestMapping(value={"/","/home"})
	public String home() {
		return "redirect:login";
	}
    
    /**
     * Loads the {@link User}'s input from the {@link SignupForm} and
     * creates with this information a new entry in the database.
     * After this is happens, it is possible for the user to login and
     * he has access to the sites corresponding to his role.
     * Redirects the user after the registration to a {@code signupCompleted.html}
     * page to show the successful creation of a new login.
     * 
     * @param signupForm holds the information needed to create an new {@link User}
     * @param result
     * @param redirectAttributes
     * @return if everything is valid a {@code signupCompleted.html} page, else again 
     * 			the login page with the necessary error messages
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ModelAndView create(@Valid SignupForm signupForm, BindingResult result, RedirectAttributes redirectAttributes) {
    	ModelAndView model;    	
    	if (!result.hasErrors()) {
            try {
            	registerService.saveFrom(signupForm);
            	model = new ModelAndView("signupCompleted");
            	

            } catch (InvalidEmailException e) {
            	result.rejectValue("email", "", e.getMessage());
            	model = new ModelAndView("login");
            }

        } else {
        	model = new ModelAndView("login");
        }   	
    	return model;
    }
    
    /**
	 * Redirects the {@code /register} to the {@code login.html} page
	 * 
	 * @return redirection to login
	 */
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register() {
    	return "redirect:/login?register";
    }
    
    /**
	 * Maps the /login page to the login form (login.html) and provides optional
	 * parameters for displaying messages
	 * 
	 * @param error displays invalid credentials message
	 * @param logout displays successful logout message
	 * @return ModelAndView for Spring Framework
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout, @RequestParam(value = "register", required = false) String register) {
		ModelAndView model = new ModelAndView("login");
		if (error != null) {
			model.addObject("error", "Invalid username or password!");
		}

		if (logout != null) {
			model.addObject("msg", "You've been logged out successfully.");
		}
		model.addObject("loginUrl", "/login");
		model.addObject("signupForm", new SignupForm());
		
		return model;
	}

	/**
	 * Redirects the {@code /success} to the {@code findTutor.html} page
	 * 
	 * @return redirection to login
	 */
	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String success() {
		return "redirect:findTutor";

	}

	/**
	 * Handles logout of the user by invalidating his session.
	 * 
	 * @param request
	 * @param response
	 * @return redirection to login screen
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return "redirect:/login?logout";
	}

	/**
	 * Displays custom access denied page with optional message displaying username
	 * 
	 * @param user authenticated {@link Principal} user object 
	 * @return
	 */
	@RequestMapping(value = "/403", method = {RequestMethod.POST, RequestMethod.GET})
	public ModelAndView accessDenied(Principal user) {

		ModelAndView model = new ModelAndView("403");
		if (user != null) {
			model.addObject("msg", "Name: " + user.getName());
		}
		return model;

	}
}
