package ch.unibe.ese.Tutorfinder.controller.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.unibe.ese.Tutorfinder.controller.exceptions.InvalidUserException;
import ch.unibe.ese.Tutorfinder.controller.pojos.SignupForm;
import ch.unibe.ese.Tutorfinder.model.User;
import ch.unibe.ese.Tutorfinder.model.dao.UserDao;

@Service
public class RegisterServiceImpl implements RegisterService {

	 @Autowired    UserDao userDao;
	
	public RegisterServiceImpl()  {}
	
	
	@Transactional
	public SignupForm saveFrom(SignupForm signupForm) throws InvalidUserException {
		
		User user = new User();
		user.setFirstName(signupForm.getFirstName());
		user.setLastName(signupForm.getLastName());
		user.setEmail(signupForm.getEmail());
		user.setPassword(signupForm.getPassword());
		user.setTutor(signupForm.isTutor());
		
		user = userDao.save(user);   // save object to DB
		
		signupForm.setId(user.getId());

        return signupForm;
	}

}