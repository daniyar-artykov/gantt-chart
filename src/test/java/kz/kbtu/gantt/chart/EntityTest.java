package kz.kbtu.gantt.chart;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import kz.kbtu.gantt.chart.entity.Task;
import kz.kbtu.gantt.chart.entity.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class EntityTest {

	private static final EntityManagerFactory emf = 
			Persistence.createEntityManagerFactory("ganttPU");
	private static EntityManager em;

	@Before
	public void init() {
		em = emf.createEntityManager();
		em.getTransaction().begin();
	}

	@Test
	@Ignore
	public void insertTest() {
		User u = new User();
		u.setFirstName("Daniyar");
		u.setSecondName("Artykov");
		u.setRole("Developer");
		
		em.persist(u);
		
		Calendar monthBefore = Calendar.getInstance();
		monthBefore.add(Calendar.MONTH, -1);
		Calendar monthBefore3 = (Calendar) monthBefore.clone();
		monthBefore3.add(Calendar.DAY_OF_WEEK, 3);
		Calendar monthBefore8 = (Calendar) monthBefore3.clone();
		monthBefore8.add(Calendar.DAY_OF_WEEK, 5);
		Calendar monthBefore13 = (Calendar) monthBefore8.clone();
		monthBefore13.add(Calendar.DAY_OF_WEEK, 5);
		Calendar monthBefore20 = (Calendar) monthBefore13.clone();
		monthBefore20.add(Calendar.DAY_OF_WEEK, 7);
		Calendar monthBefore15 = (Calendar) monthBefore13.clone();
		monthBefore15.add(Calendar.DAY_OF_WEEK, 2);
		Calendar monthBefore27 = (Calendar) monthBefore13.clone();
		monthBefore27.add(Calendar.DAY_OF_WEEK, 14);
		Calendar monthBefore29 = (Calendar) monthBefore27.clone();
		monthBefore29.add(Calendar.DAY_OF_WEEK, 2);
		Calendar monthBefore30 = (Calendar) monthBefore27.clone();
		monthBefore30.add(Calendar.DAY_OF_WEEK, 3);
		
		Calendar dayBefore2 = Calendar.getInstance();
		dayBefore2.add(Calendar.DAY_OF_WEEK, -2);
		Calendar dayAfter7 = Calendar.getInstance();
		dayAfter7.add(Calendar.DAY_OF_WEEK, 7);
		Calendar dayAfter5 = Calendar.getInstance();
		dayAfter5.add(Calendar.DAY_OF_WEEK, 5);
		Calendar dayAfter2 = Calendar.getInstance();
		dayAfter2.add(Calendar.DAY_OF_WEEK, 2);
		Calendar dayAfter12 = Calendar.getInstance();
		dayAfter12.add(Calendar.DAY_OF_WEEK, 12);
		
		Task t1 = new Task();
		t1.setDuration(3);
		t1.setStartDate(monthBefore.getTime());
		t1.setTaskDescription("Software concept");
		t1.setProgress(100.0);
		t1.setCreator(u);
		em.persist(t1);
		
		Task t2 = new Task();
		t2.setDuration(5);
		t2.setStartDate(monthBefore3.getTime());
		t2.setTaskDescription("Requirements analysis");
		t2.setProgress(100.0);
		t2.setSequence(t1);
		t2.setCreator(u);
		em.persist(t2);
		
		Task t3 = new Task();
		t3.setDuration(5);
		t3.setStartDate(monthBefore8.getTime());
		t3.setTaskDescription("Architectural design");
		t3.setProgress(100.0);
		t3.setSequence(t2);
		t3.setCreator(u);
		em.persist(t3);
		
		Task t4 = new Task();
		t4.setDuration(14);
		t4.setStartDate(monthBefore13.getTime());
		t4.setTaskDescription("Coding and debugging");
		t4.setProgress(80.0);
		t4.setSequence(t3);
		t4.setCreator(u);
		em.persist(t4);
		
		Task t5 = new Task();
		t5.setDuration(3);
		t5.setStartDate(monthBefore27.getTime());
		t5.setTaskDescription("System testing");
		t5.setProgress(0.0);
		t5.setSequence(t4);
		t5.setCreator(u);
		em.persist(t5);
		
		Task t4_1 = new Task();
		t4_1.setDuration(7);
		t4_1.setStartDate(monthBefore13.getTime());
		t4_1.setTaskDescription("Database architecture");
		t4_1.setProgress(100.0);
		t4_1.setParent(t4);
		t4_1.setCreator(u);
		em.persist(t4_1);
		
		Task t4_2 = new Task();
		t4_2.setDuration(7);
		t4_2.setStartDate(monthBefore20.getTime());
		t4_2.setTaskDescription("UI and main logic");
		t4_2.setProgress(60.0);
		t4_2.setSequence(t4_1);
		t4_2.setParent(t4);
		t4_2.setCreator(u);
		em.persist(t4_2);
		
	}

	@Test
	@Ignore
	public void taskSequenceTest() {
		Task t = em.find(Task.class, (long) 1);
		System.out.println(t);

		Task t1 = new Task();
		t1.setDuration(1);
		t1.setStartDate(new Date());
		t1.setTaskDescription("test 1");
		t1.setCreator(t.getCreator());
		t1.setParent(t);

		em.persist(t1);

		//		Task t1 = em.find(Task.class, (long) 4);

		Task t2 = new Task();
		t2.setDuration(3);
		t2.setStartDate(new Date());
		t2.setTaskDescription("test 2");
		t2.setCreator(t.getCreator());
		t2.setParent(t);
		t2.setSequence(t1);

		em.persist(t2);

		//		em.remove(t);
	}

	@Test
	@Ignore
	public void deleteParentTest() {
		Task t = em.find(Task.class, (long) 1);
		System.out.println(t);
		em.remove(t);
	}

	@After
	public void commig() {
		em.getTransaction().commit();
		em.close();
		emf.close();
	}
}
