package ch.unibe.ese.Tutorfinder.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ch.unibe.ese.Tutorfinder.controller.exceptions.InvalidSubjectException;
import ch.unibe.ese.Tutorfinder.controller.exceptions.InvalidUserException;
import ch.unibe.ese.Tutorfinder.controller.pojos.Row;
import ch.unibe.ese.Tutorfinder.controller.pojos.UpdateProfileForm;
import ch.unibe.ese.Tutorfinder.controller.pojos.UpdateSubjectsForm;
import ch.unibe.ese.Tutorfinder.model.Profile;
import ch.unibe.ese.Tutorfinder.model.Subject;
import ch.unibe.ese.Tutorfinder.model.User;
import ch.unibe.ese.Tutorfinder.model.dao.ProfileDao;
import ch.unibe.ese.Tutorfinder.model.dao.UserDao;
import ch.unibe.ese.Tutorfinder.model.dao.SubjectDao;
import ch.unibe.ese.Tutorfinder.controller.service.UpdateProfileService;
import ch.unibe.ese.Tutorfinder.controller.service.UpdateSubjectsService;

/**
 * Provides ModelAndView objects for the Spring MVC to load pages relevant to
 * the edit or update profile process
 * 
 * @author Antonio
 * @author Nicola
 *
 */
@Controller
public class UpdateProfileController {
	
	@Autowired	UpdateProfileService updateProfileService;
	@Autowired	UpdateSubjectsService updateSubjectsService;
	@Autowired	ProfileDao profileDao;
	@Autowired	UserDao userDao;
	@Autowired	SubjectDao subjectDao;

	/**
	 * Maps the /editProfile page to the {@code updateProfile.jsp}.
	 * 
	 * @param user actually logged in user, is used to get the users profile information
	 * and shows it to the user to allow editing the information.
	 * @return ModelAndView for Spring framework with the users editable profile.
	 */
	@RequestMapping(value = "/editProfile", method = RequestMethod.GET)
	public ModelAndView editProfile(Principal user) {
		ModelAndView model = new ModelAndView("html/updateProfile");
		
		model = prepareForm(user, model);
		return model;
	}
	
	/**
	 * Handles users form input with let it validate by the {@code UpdarProfileForm}
	 * class and passing it to the {@code UpdateProfileServiceImpl} class which saves
	 * the new information to the database.
	 * 
	 * @param user actually logged in user, is used to get the users profile information
	 * and shows it to the user to allow editing the information.
	 * @param updateProfileForm class to validate the users form input.
	 * @param result
	 * @param redirectAttributes
	 * @return ModelAndView for Spring framework with the users new and editable profile.
	 */
	@RequestMapping(value ="/update", method = RequestMethod.POST)
	public ModelAndView update(Principal user, @Valid UpdateProfileForm updateProfileForm, BindingResult result,
			RedirectAttributes redirectAttributes) {
		ModelAndView model;
		if (!result.hasErrors()) {
            try {
            	updateProfileService.saveFrom(updateProfileForm, user);
            	model = new ModelAndView("html/updateProfile");
            	//TODO show success message to the user
            } catch (InvalidUserException e) {
            	model = new ModelAndView("html/updateProfile");
            	model.addObject("page_error", e.getMessage());
            }
        } else {
        	model = new ModelAndView("html/updateProfile");
        	//TODO show error massage to the user
        }	
		model = prepareForm(user, model);
		
		return model;
	}
	
	/**
	 * Upload single file using Spring Controller
	 */
	@RequestMapping(value = "/uploadImage", method = RequestMethod.POST)
	public ModelAndView uploadFileHandler(Principal user, @RequestParam("file") MultipartFile file) {
		ModelAndView model;
		if (!file.isEmpty()) {
			try {
				byte[] bytes = file.getBytes();
				
				// Creating the directory to store file
				String rootPath = System.getProperty("user.dir");
				User tmpUser = userDao.findByEmail(user.getName());
				File dir = new File(rootPath + File.separator + "src" + File.separator + "main" 
									+ File.separator + "webapp" + File.separator + "img" + File.separator + "profPic");
				if (!dir.exists())
					dir.mkdirs();

				// Create the file on server
				File serverFile = new File(dir.getAbsolutePath()
						+ File.separator + tmpUser.getId() + ".png");
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
				
				model = new ModelAndView("html/updateProfile");
				//TODO show success massage to the user
			} catch (Exception e) {
				model = new ModelAndView("html/updateProfile");
				model.addObject("page_error", e.getMessage());
			}
		} else {
			model = new ModelAndView("html/updateProfile");
			//TODO show error massage to the user
		}
		
		model = prepareForm(user, model);
		return model;
	}
	
	@RequestMapping(value ="/editSubjects", params="save", method = RequestMethod.POST)
	public ModelAndView updateSubjects(Principal user, @Valid UpdateSubjectsForm updateSubjectsForm, BindingResult result,
			RedirectAttributes redirectAttributes) {
		ModelAndView model;
		
		if (!result.hasErrors()) {
            try {
            	updateSubjectsService.saveFrom(updateSubjectsForm, user);
            	model = new ModelAndView("html/updateProfile");
            	//TODO show success message to the user
            } catch (InvalidSubjectException e) {
            	model = new ModelAndView("html/updateProfile");
            	model.addObject("page_error", e.getMessage());
            }
        } else {
        	model = new ModelAndView("html/updateProfile");
        	//TODO show error massage to the user
        }
		
		model = prepareForm(user, model);
		return model;
	}
	
	@RequestMapping(value = "/editSubjects", params="addRow")
	public ModelAndView addRow(@Valid UpdateSubjectsForm updateSubjectsForm, BindingResult result, Principal user) {
		ModelAndView model = new ModelAndView("html/updateProfile");
		updateSubjectsForm.getRows().add(new Row());
		model = prepareForm(user, model, updateSubjectsForm);
		return model;
	}

	@RequestMapping(value = "/editSubjects", params="remRow")
	public ModelAndView removeRow(@Valid UpdateSubjectsForm updateSubjectsForm, BindingResult result, final HttpServletRequest req,Principal user) {
		ModelAndView model = new ModelAndView("html/updateProfile");
		final Integer rowId = Integer.valueOf(req.getParameter("remRow"));
		updateSubjectsForm.getRows().remove(rowId.intValue());
		model = prepareForm(user, model, updateSubjectsForm);
		return model;
	}
	
	private ModelAndView prepareForm(Principal user, ModelAndView model, UpdateSubjectsForm updateSubjectsForm) {
		model.addObject("updateSubjectsForm", updateSubjectsForm);
		model.addObject("updateProfileForm", getFormWithValues(user));
		model.addObject("User", userDao.findByEmail(user.getName()));
		return model;
	}

	/**
	 * Gets an form with the users new information
	 * 
	 * @param user
	 * @return form with the users input values
	 */
	private UpdateProfileForm getFormWithValues(Principal user) {
		UpdateProfileForm tmpForm = new UpdateProfileForm();
		tmpForm.setBiography(getUsersProfile(user).getBiography());
		tmpForm.setRegion(getUsersProfile(user).getRegion());
		tmpForm.setWage(getUsersProfile(user).getWage());
		
		return tmpForm;
	}

	/**
	 * Gets the profile which belongs to the actually logged in user
	 * 
	 * @param user is needed to get the right profile
	 * @return profile of the actually logged in user
	 */
	private Profile getUsersProfile(Principal user) {
		User tmpUser = userDao.findByEmail(user.getName());
		Profile tmpProfile = profileDao.findOne(tmpUser.getId());
		
		return tmpProfile;
	}
	
	/**
	 * Injects needed objects into ModelAndView
	 * @param user
	 * @param model
	 * @return
	 */
	private ModelAndView prepareForm(Principal user, ModelAndView model) {
		model.addObject("updateSubjectsForm", getUpdateSubjectWithValues(subjectDao.findAllByUser(userDao.findByEmail(user.getName()))));
		model.addObject("updateProfileForm", getFormWithValues(user));
		model.addObject("User", userDao.findByEmail(user.getName()));
		return model;
	}

	private UpdateSubjectsForm getUpdateSubjectWithValues(ArrayList<Subject> subjectList) {
		UpdateSubjectsForm tempForm = new UpdateSubjectsForm();
		List<Row> rowList = new ArrayList<Row>();
		for (Subject subject:subjectList) {
			rowList.add(new Row(subject.getName(), subject.getGrade()));
		}
		tempForm.setRows(rowList);
		return tempForm;
	}
}
