package ch.unibe.ese.Tutorfinder.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ch.unibe.ese.Tutorfinder.controller.exceptions.InvalidMessageException;
import ch.unibe.ese.Tutorfinder.controller.pojos.Forms.MessageForm;
import ch.unibe.ese.Tutorfinder.controller.service.MessageService;
import ch.unibe.ese.Tutorfinder.controller.service.UserService;
import ch.unibe.ese.Tutorfinder.model.Message;
import ch.unibe.ese.Tutorfinder.model.User;
import ch.unibe.ese.Tutorfinder.util.ConstantVariables;

@Controller
public class MessageController {

	@Autowired
	MessageService messageService;
	@Autowired
	UserService userService;

	/**
	 * Maps the /messages page to the {@code messagesOverview.html}.
	 * 
	 * @param authUser
	 *            actually logged in user, is used to get the users messages
	 *            information and shows it to the user to allow reading them
	 * @return ModelAndView for Spring framework with the users messagesOverview
	 */
	@RequestMapping(value = "/messages", method = RequestMethod.GET)
	public ModelAndView messages(Principal authUser, @RequestParam(value = "inbox", required = false) String inbox,
			@RequestParam(value = "outbox", required = false) String outbox) {
		ModelAndView model = new ModelAndView("messagesOverview");
		
		User tmpUser = userService.getUserByPrincipal(authUser);
		model.addObject("authUser", tmpUser);
		
		if(inbox != null) {
			model.addObject("inbox", messageService.getMessageByBox(ConstantVariables.INBOX, tmpUser));
		} else if (outbox != null) {
			model.addObject("outbox", messageService.getMessageByBox(ConstantVariables.OUTBOX, tmpUser));
		} else {
			model.addObject("unread", messageService.getMessageByBox(ConstantVariables.UNREAD, tmpUser));
		}

		return model;
	}

	@RequestMapping(value = "/newMessage", params = "newMessage", method = RequestMethod.POST)
	public ModelAndView newMessage(Principal authUser, final HttpServletRequest req) {
		ModelAndView model = new ModelAndView("messagesOverview");

		User tmpUser = userService.getUserByPrincipal(authUser);
		model.addObject("authUser", tmpUser);

		final Long receiverId = Long.valueOf(req.getParameter("newMessage"));
		MessageForm messageForm = new MessageForm();
		messageForm.setReceiver(userService.getUserById(receiverId));

		model.addObject("messageForm", messageForm);

		return model;
	}

	@RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
	public ModelAndView sendMessage(Principal authUser, @Valid MessageForm messageForm, BindingResult result,
			RedirectAttributes redirectAttributes) {
		ModelAndView model = new ModelAndView("messagesOvervice");
		if (!result.hasErrors()) {
            try {
            	messageService.saveFrom(messageForm, authUser);
            	model = messages(authUser, null, ConstantVariables.OUTBOX);

            } catch (InvalidMessageException e) {
            	model.addObject("page_error", e.getMessage());
            }
        } else {
        	User tmpUser = userService.getUserByPrincipal(authUser);
    		model.addObject("authUser", tmpUser);
        	
        	model.addObject("messageForm", messageForm);
        } 

		return model;
	}

}
