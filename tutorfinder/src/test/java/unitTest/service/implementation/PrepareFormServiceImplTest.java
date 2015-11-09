package unitTest.service.implementation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.DayOfWeek;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.ModelAndView;

import ch.unibe.ese.Tutorfinder.controller.pojos.Row;
import ch.unibe.ese.Tutorfinder.controller.pojos.Forms.UpdateProfileForm;
import ch.unibe.ese.Tutorfinder.controller.pojos.Forms.UpdateSubjectsForm;
import ch.unibe.ese.Tutorfinder.controller.pojos.Forms.UpdateTimetableForm;
import ch.unibe.ese.Tutorfinder.controller.service.PrepareFormService;
import ch.unibe.ese.Tutorfinder.controller.service.SubjectService;
import ch.unibe.ese.Tutorfinder.controller.service.TimetableService;
import ch.unibe.ese.Tutorfinder.controller.service.UserService;
import ch.unibe.ese.Tutorfinder.controller.service.implementations.PrepareFormServiceImpl;
import ch.unibe.ese.Tutorfinder.controller.service.implementations.UserServiceImpl;
import ch.unibe.ese.Tutorfinder.model.Profile;
import ch.unibe.ese.Tutorfinder.model.Subject;
import ch.unibe.ese.Tutorfinder.model.Timetable;
import ch.unibe.ese.Tutorfinder.model.User;
import ch.unibe.ese.Tutorfinder.model.dao.ProfileDao;
import ch.unibe.ese.Tutorfinder.model.dao.UserDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/test.xml" })
public class PrepareFormServiceImplTest {

	@Autowired
	TimetableService timetableService;
	@Autowired
	SubjectService subjectService;
	@Autowired
	UserDao userDao;
	@Autowired
	ProfileDao profileDao;
	

	@Mock
	private Principal mockAuthUser;
	@Mock
	private User mockUser;
	@Mock
	private Profile mockProfile;
	@Mock
	private Subject mockSubject;
	@Mock
	private Row mockRow;
	@Mock
	private Timetable mockTimetable;
	@Mock
	private ModelAndView mockModel;
	@Mock
	private UserServiceImpl mockUserService;
	
	private ArrayList<Subject> subjectList = new ArrayList<Subject>();
	private ArrayList<Timetable> timetableList = new ArrayList<Timetable>();

	PrepareFormService prepareFormService;
	
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		ReflectionTestUtils.setField(mockUser, "id", new Long(1));
		ReflectionTestUtils.setField(mockUser, "email", "test@test.ch");
		ReflectionTestUtils.setField(mockUser, "firstName", "TestFirstName");
		ReflectionTestUtils.setField(mockUser, "lastName", "TestLastName");
		ReflectionTestUtils.setField(mockUser, "profile", this.mockProfile);

		ReflectionTestUtils.setField(mockProfile, "biography", "TestBiography");
		ReflectionTestUtils.setField(mockProfile, "region", "TestRegion");
		ReflectionTestUtils.setField(mockProfile, "wage", new BigDecimal(50));

		ReflectionTestUtils.setField(mockRow, "subject", "testSubject");
		ReflectionTestUtils.setField(mockRow, "grade", 5.0);

		ReflectionTestUtils.setField(mockTimetable, "day", DayOfWeek.MONDAY);
		ReflectionTestUtils.setField(mockTimetable, "timeslot", 21);

		this.subjectList.add(this.mockSubject);
		this.timetableList.add(this.mockTimetable);
		
		prepareFormService = new PrepareFormServiceImpl(mockUserService); //Use constructor to inject mock
	}

	@Test
	public void testPrepareForm() {
		when(mockUserService.getUserByPrincipal(mockAuthUser)).thenReturn(mockUser); // Control injected mock
		ModelAndView gotMav = prepareFormService.prepareForm(mockAuthUser, mockModel);
	}

	@Test(expected = AssertionError.class)
	public void testPrepareFormNullModel() {
		// WHEN
		prepareFormService.prepareForm(this.mockAuthUser, null);
	}

	@Test(expected = AssertionError.class)
	public void testPrepareFormNullPrincipal() {
		// WHEN
		prepareFormService.prepareForm(null, this.mockModel);
	}

	@Test
	public void testGetUpdateTimetableFormWithValues() {
		// GIVEN
		when(timetableService.findAllByUser(mockUser)).thenReturn(this.timetableList);
		when(mockTimetable.getDay()).thenReturn(DayOfWeek.MONDAY);
		when(mockTimetable.getTime()).thenReturn(21);

		// WHEN
		UpdateTimetableForm tmpForm = prepareFormService.getUpdateTimetableFormWithValues(mockUser);

		// THEN
		assertEquals(true, tmpForm.getTimetable()[21][0].booleanValue());
	}

	@Test(expected = AssertionError.class)
	public void testGetUpdateNullTimetableFormWithValues() {
		// WHEN
		prepareFormService.getUpdateTimetableFormWithValues(null);
	}

	@Test
	public void testGetUpdateSubjectWithValues() {
		// GIVEN
		when(mockSubject.getName()).thenReturn("testSubject");
		when(mockSubject.getGrade()).thenReturn(5.0);

		// WHEN
		UpdateSubjectsForm tmpForm = prepareFormService.getUpdateSubjectWithValues(this.subjectList);

		// THEN
		assertEquals(ReflectionTestUtils.getField(mockRow, "subject"), tmpForm.getRows().get(0).getSubject());
	}

	@Test(expected = AssertionError.class)
	public void testGetUpdateNullSubjectWithValues() {
		// WHEN
		prepareFormService.getUpdateSubjectWithValues(null);
	}

	@Test
	public void testGetFormWithValues() {
		// GIVEN
		when(userDao.findByEmail(anyString())).thenReturn(this.mockUser);
		when(profileDao.findOne(anyLong())).thenReturn(this.mockProfile);

		// WHEN
		UpdateProfileForm tmpForm = prepareFormService.getFormWithValues(this.mockAuthUser);

		// THEN
		assertEquals(tmpForm.getFirstName(), this.mockUser.getFirstName());

	}

	@Test(expected = AssertionError.class)
	public void testGetFormWithValuesNullPrincipal() {
		// WHEN
		prepareFormService.getFormWithValues(null);
	}

	@Test
	public void testGetUsersProfile() {
		// GIVEN
		when(userDao.findByEmail(anyString())).thenReturn(this.mockUser);
		when(profileDao.findOne(anyLong())).thenReturn(this.mockProfile);

		// WHEN
		Profile tmpProfile = prepareFormService.getUsersProfile(this.mockAuthUser);

		// THEN
		assertEquals(tmpProfile, this.mockProfile);

	}

	@Test(expected = AssertionError.class)
	public void testGetNullUsersProfile() {
		// WHEN
		prepareFormService.getUsersProfile(null);

	}
}
