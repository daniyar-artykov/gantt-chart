package kz.kbtu.gantt.chart;

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
		u.setRole("admin");

		Task t = new Task();
		t.setDuration(4);
		t.setStartDate(new Date());
		t.setTaskDescription("test");
		t.setCreator(u);

		u.getCreatedTasks().add(t);

		em.persist(u);

		//		u = em.find(User.class, (long) 3);
		System.out.println(u);
		//		em.remove(u);

		//		t = em.find(Task.class, (long) 1);
		//		em.remove(t);
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
