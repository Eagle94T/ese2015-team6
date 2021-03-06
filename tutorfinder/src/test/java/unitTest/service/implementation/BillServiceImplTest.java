package unitTest.service.implementation;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.unibe.ese.Tutorfinder.controller.exceptions.PaymentStatusException;
import ch.unibe.ese.Tutorfinder.controller.service.AppointmentService;
import ch.unibe.ese.Tutorfinder.controller.service.implementations.BillServiceImpl;
import ch.unibe.ese.Tutorfinder.model.Appointment;
import ch.unibe.ese.Tutorfinder.model.Bill;
import ch.unibe.ese.Tutorfinder.model.User;
import ch.unibe.ese.Tutorfinder.model.dao.BillDao;
import ch.unibe.ese.Tutorfinder.model.dao.UserDao;
import ch.unibe.ese.Tutorfinder.util.Availability;
import ch.unibe.ese.Tutorfinder.util.ConstantVariables;
import ch.unibe.ese.Tutorfinder.util.PaymentStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/test.xml" })
public class BillServiceImplTest {
	
	
	private BillServiceImpl billServiceImpl;
	
	@Mock
	Bill mockBill;
	
	@Mock
	AppointmentService appointmentService ;
	
	@Mock
	BillDao billDao;
	
	@Mock
	UserDao userDao;
	
	@Mock
	Appointment appointment;
	
	@Mock
	User tutor;
	
	@Mock
	User differentTutor;
	
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		billServiceImpl = new BillServiceImpl(appointmentService, billDao, userDao);
	}
	
	@Test
	public void getBillsTest() {
		ArrayList<Bill> tmpList = new ArrayList<Bill>();
		tmpList.add(mockBill);
		when(billDao.findAllByTutorAndPaymentStatus(eq(tutor), eq(PaymentStatus.UNPAID))).thenReturn(tmpList);
		
		List<Bill> testList = billServiceImpl.getBills(tutor, PaymentStatus.UNPAID);
		
		verify(billDao, times(1)).findAllByTutorAndPaymentStatus(tutor, PaymentStatus.UNPAID);
		assertTrue(tmpList.equals(testList));
	}
	
	@Test
	public void updateLastMonthsBillTest() {
		
		//GIVEN
		List<User> testList = new ArrayList<User>();
		testList.add(tutor);
		when(userDao.findAllByRole(ConstantVariables.TUTOR)).thenReturn(testList);
		LocalDate tmpDate = LocalDate.now();
		tmpDate = tmpDate.plusMonths(-1);
		
		when(appointment.getWage()).thenReturn(BigDecimal.ONE);
		List<Appointment> tmpList = new ArrayList<Appointment>();
		tmpList.add(appointment); 
		when(appointmentService.getAppointmentsForMonthAndYear(tutor, Availability.ARRANGED, 
																tmpDate.getMonthValue(), tmpDate.getYear()))
																.thenReturn(tmpList);
		//WHEN
		billServiceImpl.updateMonthlyBills();
		
		//THEN
		verify(billDao, times(1)).save(any(Bill.class));
		
	}
	
	@Test
	public void getBillForCurentMonthTest()  {
		when(appointment.getWage()).thenReturn(BigDecimal.ONE);
		List<Appointment> tmpList = new ArrayList<Appointment>();
		tmpList.add(appointment);
		
		when(appointmentService.getAppointmentsForMonthAndYear(any(User.class), 
				eq(Availability.ARRANGED), anyInt(), anyInt())).thenReturn(tmpList);
		
		BigDecimal testBalance = billServiceImpl.getBillForCurrentMonth(new User());
		
		assertTrue(BigDecimal.ONE.compareTo(testBalance) >= 0);
	}
	
	@Test
	public void getBillForCurrentMonthNullInDBTest() {
		when(appointment.getWage()).thenReturn(BigDecimal.ZERO);
		List<Appointment> tmpList = new ArrayList<Appointment>();
		tmpList.add(null);
		
		when(appointmentService.getAppointmentsForMonthAndYear(any(User.class), 
				eq(Availability.ARRANGED), anyInt(), anyInt())).thenReturn(tmpList);
		
		BigDecimal testBalance = billServiceImpl.getBillForCurrentMonth(new User());
		
		assertTrue(BigDecimal.ZERO.compareTo(testBalance) == 0);
		
	}
	
	@Test
	public void payTest() {
		//GIVEN
		when(billDao.findById(1)).thenReturn(mockBill);
		when(mockBill.getTutor()).thenReturn(tutor);
		
		//WHEN
		billServiceImpl.pay(tutor, 1);
		
		//THEN
		verify(mockBill, times(1)).setPaymentStatus(PaymentStatus.PAID);
	}
	
	@Test(expected=PaymentStatusException.class)
	public void testPaymentStatusException() {
		this.mockBill.setPaymentStatus(PaymentStatus.of(5));
	}
	
	@Test
	public void payTestUsersDoNotCorrespond() {
		//Given
		when(billDao.findById(1)).thenReturn(mockBill);
		when(mockBill.getTutor()).thenReturn(differentTutor);
		
		//WHEN
		billServiceImpl.pay(tutor, 1);
		
		//THEN
		verify(mockBill, never()).setPaymentStatus(any(PaymentStatus.class));		
	}
	
	@Test
	public void payTestDBReturnsNull() {
		//Given
		when(billDao.findById(1)).thenReturn(null);
		
		//WHEN
		billServiceImpl.pay(tutor, 1);
		
		//THEN
		verify(billDao, never()).save(any(Bill.class));
	}
	
	@Test(expected=AssertionError.class)
	public void getBillForCurrentMonthNullTest() {
		billServiceImpl.getBillForCurrentMonth(null);
	}
	 
	@Test(expected=AssertionError.class)
	public void getBillsNullTest() {
		billServiceImpl.getBills(null, PaymentStatus.UNPAID);
	}
	
	@Test(expected=AssertionError.class)
	public void payUserisNullTest() {
		billServiceImpl.pay(null, 0L);
	}

}
