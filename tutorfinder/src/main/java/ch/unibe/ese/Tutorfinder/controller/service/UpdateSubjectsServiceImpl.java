package ch.unibe.ese.Tutorfinder.controller.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.unibe.ese.Tutorfinder.controller.exceptions.InvalidSubjectException;
import ch.unibe.ese.Tutorfinder.controller.pojos.Row;
import ch.unibe.ese.Tutorfinder.controller.pojos.UpdateSubjectsForm;
import ch.unibe.ese.Tutorfinder.model.Subject;
import ch.unibe.ese.Tutorfinder.model.User;
import ch.unibe.ese.Tutorfinder.model.dao.SubjectDao;
import ch.unibe.ese.Tutorfinder.model.dao.UserDao;

@Service
public class UpdateSubjectsServiceImpl implements UpdateSubjectsService {

	@Autowired
	UserDao userDao;
	@Autowired
	SubjectDao subjectDao;

	public UpdateSubjectsServiceImpl() {
	}

	@Transactional
	public UpdateSubjectsForm saveFrom(UpdateSubjectsForm updateSubjectsForm, Principal authUser)
			throws InvalidSubjectException {
		User user = userDao.findByEmail(authUser.getName());
		ArrayList<Subject> subjects = subjectDao.findAllByUser(user);
		List<Row> rowList = updateSubjectsForm.getRows();
		
		for (Row row:rowList) {
			Subject subject = new Subject();
			subject.setUser(user);
			subject.setName(row.getSubject());
			subject.setGrade(row.getGrade());
			subjects.add(subject);
		}
		subjectDao.save(subjects);
		updateSubjectsForm.setId(user.getId());
		return updateSubjectsForm;
	}

}
