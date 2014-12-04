package kz.kbtu.gantt.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import kz.kbtu.gantt.chart.entity.Task;
import kz.kbtu.gantt.chart.entity.User;

import com.jidesoft.gantt.DateGanttChartPane;
import com.jidesoft.gantt.DefaultGanttEntry;
import com.jidesoft.gantt.DefaultGanttEntryRelation;
import com.jidesoft.gantt.DefaultGanttEntryRenderer;
import com.jidesoft.gantt.DefaultGanttModel;
import com.jidesoft.gantt.GanttChart;
import com.jidesoft.gantt.GanttChartPane;
import com.jidesoft.gantt.GanttChartPopupMenuInstaller;
import com.jidesoft.gantt.GanttEntry;
import com.jidesoft.gantt.GanttEntryRelation;
import com.jidesoft.gantt.RelationGanttChartPopupMenuCustomizer;
import com.jidesoft.grid.CellEditorManager;
import com.jidesoft.grid.CellRendererManager;
import com.jidesoft.grid.TableUtils;
import com.jidesoft.grid.TreeTable;
import com.jidesoft.range.Range;
import com.jidesoft.range.TimeRange;
import com.jidesoft.scale.DateScaleModel;
import com.jidesoft.scale.ResizePeriodsPopupMenuCustomizer;
import com.jidesoft.scale.VisiblePeriodsPopupMenuCustomizer;

public class OldGanttChartHandler {

	private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("ganttPU");
	private static final String roles[] = { "ProjectManager", "Developer", "Designer"};
	private static final String columnNames[] = {"ID", "First name", "Second name", "Role"};
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	private static JFrame newFrame;
	private static EntityManager em;
	private static User currentUser;
	private static List<User> usersList;
	private static List<Task> tasksList;
	private static JFrame mainFrame;
	private static JPanel usersTab;
	private static JScrollPane scrollPane;
	
	private static GanttChartPane<Date, DefaultGanttEntry<Date>> _ganttChartPane;
	private static GanttChart<Date, DefaultGanttEntry<Date>> _ganttChart;
	private static boolean _debug = false;
	
	public static void main(String [] args) {
		em = emf.createEntityManager();
		currentUser = em.find(User.class, (long) 1);
		loadUsers();
		loadTasks();
		mainFrame = new JFrame("Gantt chart");
		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		
		JLabel currentUserLabel = new JLabel(currentUser.getFirstName() + " " + currentUser.getSecondName() + " [" + currentUser.getRole() + "]");	
		content.add(currentUserLabel, BorderLayout.NORTH);
		
		JPanel ganttTab = new JPanel();
		
		JButton addTaskBtn = new JButton("Add task");
		addTaskBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openNewTaskWindow();
			}
		});
		ganttTab.add(addTaskBtn);

		usersTab = new JPanel();
		
		JButton addPersonBtn = new JButton("Add person");
		addPersonBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openNewPersonWindow();
			}
		});
		usersTab.add(addPersonBtn);
		
		JTable table = new JTable(getAllUsers(), columnNames);
		
	    scrollPane = new JScrollPane(table);
		usersTab.add(scrollPane);
	    
		JTabbedPane mainTabPane = new JTabbedPane();
		
		mainTabPane.add("Gantt chart", ganttTab);
		ganttTab.add(getDemoPanel());
		mainTabPane.add("Resources", usersTab);
		content.add(mainTabPane, BorderLayout.CENTER);
		
		mainFrame.add(content);
		
		mainFrame.setVisible(true);
	}

	private static void loadUsers() {
		usersList = em.createQuery( "from User u", User.class ).getResultList();
	}

	private static String[] getAllUsersName() {
		String []usersName = new String[usersList.size() + 1];
		usersName[0] = "";
		for(int i = 1; i < usersList.size() + 1; i++) {
			usersName[i] = usersList.get(i - 1).getFirstName() + " " + usersList.get(i - 1).getSecondName();
		}

		return usersName;
	}
	
	private static Object[][] getAllUsers() {
		Object [][] allUsers = new Object[usersList.size()][4];
		for(int i = 0; i < usersList.size(); i++) {
			allUsers[i][0] = usersList.get(i).getId();
			allUsers[i][1] = usersList.get(i).getFirstName();
			allUsers[i][2] = usersList.get(i).getSecondName();
			allUsers[i][3] = usersList.get(i).getRole();
		}
		
		return allUsers;
	}

	private static void openNewTaskWindow() {
		loadUsers();
		
		newFrame = new JFrame("Add task");
		newFrame.setSize(500, 350);
		newFrame.setVisible(true);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2));
		
		JLabel descLabel = new JLabel("Description");
		panel.add(descLabel);
		JTextField taskDesc  = new JTextField();
		panel.add(taskDesc);
		
		JLabel startLabel = new JLabel("Start date");
		panel.add(startLabel);
		JTextField startDate  = new JTextField();
		panel.add(startDate);
		
		JLabel durationLabel = new JLabel("Duration");
		panel.add(durationLabel);
		JTextField duration  = new JTextField();
		panel.add(duration);
		
		JLabel dueLabel = new JLabel("Due date");
		panel.add(dueLabel);
		JTextField dueDate  = new JTextField();
		panel.add(dueDate);
		dueDate.setEditable(false);

		JLabel progressLabel = new JLabel("Progress");
		panel.add(progressLabel);
		JTextField progress  = new JTextField();
		panel.add(progress);
		
		JLabel personLabel = new JLabel("Assigned person");
		panel.add(personLabel);
		JComboBox<String> comboBox = new JComboBox<String>(getAllUsersName());
		comboBox.setEditable(true);
		panel.add(comboBox);
		
		JLabel mainTaskLabel = new JLabel("Main task");
		panel.add(mainTaskLabel);
		JComboBox<String> mainTask = new JComboBox<String>(getAllTasks());
		mainTask.setEditable(true);
		panel.add(mainTask);
		
		JLabel perviousTaskLabel = new JLabel("Pervious task");
		panel.add(perviousTaskLabel);
		JComboBox<String> perviousTask = new JComboBox<String>(getAllTasks());
		perviousTask.setEditable(true);
		panel.add(perviousTask);

		JButton saveBtn = new JButton("Save");
		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newFrame.setVisible(false);
			}
		});
		
		panel.add(saveBtn);
		JButton closeBtn = new JButton("Close");
		closeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newFrame.setVisible(false);
			}
		});
		
		panel.add(closeBtn);
		newFrame.add(panel);
	}
	
	private static void loadTasks() {
		tasksList = em.createQuery( "from Task t", Task.class ).getResultList();
	}

	private static String[] getAllTasks() {
		String []usersName = new String[tasksList.size() + 1];
		usersName[0] = "";
		for(int i = 1; i < tasksList.size() + 1; i++) {
			usersName[i] = tasksList.get(i - 1).getTaskDescription() + " [" + DATE_FORMAT.format(tasksList.get(i - 1).getStartDate()) + "]";
		}

		return usersName;
	}

	private static void openNewPersonWindow() {
		newFrame = new JFrame("Add person");
		newFrame.setSize(500, 250);
		newFrame.setVisible(true);
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2));
		JLabel firstNameLabel = new JLabel("First name");
		panel.add(firstNameLabel);
		final JTextField firstName  = new JTextField();
		panel.add(firstName);
		JLabel secondNameLabel = new JLabel("Second name");
		panel.add(secondNameLabel);
		final JTextField secondName  = new JTextField();
		panel.add(secondName);
		JLabel roleLabel = new JLabel("Role");
		panel.add(roleLabel);
		final JList<String> role = new JList<String>(roles);
		panel.add(role);
		role.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JButton saveBtn = new JButton("Save");
		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				User u = new User();
				u.setFirstName(firstName.getText());
				u.setSecondName(secondName.getText());
				u.setRole(role.getSelectedValue());

				em.getTransaction().begin();
				em.persist(u);
				em.getTransaction().commit();

				newFrame.setVisible(false);
				loadUsers();
				usersTab.remove(scrollPane);
				JTable table = new JTable(getAllUsers(), columnNames);
				
				scrollPane = new JScrollPane(table);
				usersTab.add(scrollPane);
				usersTab.validate();
				usersTab.repaint();
			}
		});
		panel.add(saveBtn);
		JButton closeBtn = new JButton("Close");
		closeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newFrame.setVisible(false);
			}
		});
		panel.add(closeBtn);
		newFrame.add(panel);
	}
	
	public static Component getDemoPanel() {
		CellRendererManager.initDefaultRenderer();
		CellEditorManager.initDefaultEditor();

		DefaultGanttModel<Date, DefaultGanttEntry<Date>> model = createGanttModel();

		_ganttChartPane = new DateGanttChartPane<DefaultGanttEntry<Date>>(model);

		TreeTable treeTable = _ganttChartPane.getTreeTable();
		treeTable.setDragEnabled(true);
		treeTable.setTransferHandler(new TreeTableTransferHandler());

		_ganttChart = _ganttChartPane.getGanttChart();
		_ganttChart.getScaleArea().addPopupMenuCustomizer(new VisiblePeriodsPopupMenuCustomizer<Date>());
		_ganttChartPane.getGanttChart().getScaleArea().addPopupMenuCustomizer(new ResizePeriodsPopupMenuCustomizer<Date>(_ganttChartPane.getGanttChart()));
		_ganttChart.setDefaultEntryRenderer(new CustomGanttEntryRenderer());

		GanttChartPopupMenuInstaller installer = new GanttChartPopupMenuInstaller(_ganttChartPane.getGanttChart());
		installer.addGanttChartPopupMenuCustomizer(new RelationGanttChartPopupMenuCustomizer());

		TableUtils.autoResizeAllColumns(_ganttChartPane.getTreeTable());

		return _ganttChartPane;
	}

	private static DefaultGanttModel<Date, DefaultGanttEntry<Date>> createGanttModel() {
		DefaultGanttModel<Date, DefaultGanttEntry<Date>> model = new DefaultGanttModel<Date, DefaultGanttEntry<Date>>();

		DateScaleModel scaleModel = new DateScaleModel();

		model.setScaleModel(scaleModel);

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
		
		Calendar today = Calendar.getInstance();
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

		
		model.setRange(new TimeRange(dayBefore2.getTime(), dayAfter7.getTime()));
		DefaultGanttEntry<Date> group1 = new DefaultGanttEntry<Date>("Software concept", Date.class, new TimeRange(monthBefore.getTime(), monthBefore3.getTime()), 1);
		group1.setExpanded(true);
		DefaultGanttEntry<Date> group2 = new DefaultGanttEntry<Date>("Requirements analysis", Date.class, new TimeRange(monthBefore3.getTime(), monthBefore8.getTime()), 1);
		group2.setExpanded(true);
		DefaultGanttEntry<Date> group3 = new DefaultGanttEntry<Date>("Architectural design", Date.class, new TimeRange(monthBefore8.getTime(), monthBefore13.getTime()), 1);
		group3.setExpanded(true);
		DefaultGanttEntry<Date> group4 = new DefaultGanttEntry<Date>("Coding and debugging", Date.class, new TimeRange(monthBefore13.getTime(), monthBefore27.getTime()), 0.8);
		group4.setExpanded(true);
		DefaultGanttEntry<Date> group5 = new DefaultGanttEntry<Date>("System testing", Date.class, new TimeRange(monthBefore27.getTime(), monthBefore30.getTime()), 0);
		group5.setExpanded(true);
		
		DefaultGanttEntry<Date> child4_1 = new DefaultGanttEntry<Date>("Database architecture", Date.class, new TimeRange(monthBefore13.getTime(), monthBefore20.getTime()), 1);
		DefaultGanttEntry<Date> child4_2 = new DefaultGanttEntry<Date>("UI and main logic", Date.class, new TimeRange(monthBefore20.getTime(), monthBefore27.getTime()), 0.6);
		group4.addChild(child4_1);
		group4.addChild(child4_2);
		
		model.getGanttEntryRelationModel().addEntryRelation(new DefaultGanttEntryRelation<DefaultGanttEntry<Date>>(child4_1, child4_2, GanttEntryRelation.ENTRY_RELATION_FINISH_TO_START));
		
//		DefaultGanttEntry<Date> subgroup = new DefaultGanttEntry<Date>("Sub group",
//				Date.class, new TimeRange(today.getTime(), dayAfter5.getTime()), 0);
//		subgroup.setExpanded(true);
//
//		for (int relation : new int[]{
//				GanttEntryRelation.ENTRY_RELATION_FINISH_TO_START,
//				GanttEntryRelation.ENTRY_RELATION_START_TO_FINISH,
//				GanttEntryRelation.ENTRY_RELATION_FINISH_TO_FINISH,
//				GanttEntryRelation.ENTRY_RELATION_START_TO_START}) {
//
//			DefaultGanttEntry<Date> child1 = new DefaultGanttEntry<Date>("Task 1." + relation,
//					Date.class, new TimeRange(today.getTime(), dayAfter2.getTime()), 0.4);
//			DefaultGanttEntry<Date> child2 = new DefaultGanttEntry<Date>("Task 2." + relation,
//					Date.class, new TimeRange(dayAfter2.getTime(), dayAfter5.getTime()), 0);
//
//			group1.addChild(child1);
//			if (_debug) {
//				group2.addChild(new DefaultGanttEntry<Date>("Task 2." + relation + "spacer",
//						new TimeRange(today.getTime(), today.getTime())));
//			}
//			group2.addChild(child2);
//
//			if (_debug) {
//				child1 = new DefaultGanttEntry<Date>("Task 1." + relation,
//						Date.class, new TimeRange(today.getTime(), dayAfter2.getTime()), 0.4);
//
//				subgroup.addChild(child1);
//
//				model.getGanttEntryRelationModel().addEntryRelation(new DefaultGanttEntryRelation<DefaultGanttEntry<Date>>(child1, child2, relation));
//			} 
///*			else {
//				model.getGanttEntryRelationModel().addEntryRelation(new DefaultGanttEntryRelation<DefaultGanttEntry<Date>>(child1, child2, relation));
//			} */
//		}
//
//		if (_debug) {
//			group1.addChild(subgroup);
//		}
		model.addGanttEntry(group1);
		model.addGanttEntry(group2);
		model.addGanttEntry(group3);
		model.addGanttEntry(group4);
		model.addGanttEntry(group5);

		return model;
	}

	public static class CustomGanttEntryRenderer extends DefaultGanttEntryRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7939921415401394639L;

		@Override
		public Component getGanttEntryRendererComponent(GanttChart<?, ?> chart,
				GanttEntry<?> entry, boolean isSelected, boolean hasFocus, int row, int column,
				Insets insets) {
			Component component = super.getGanttEntryRendererComponent(chart, entry, isSelected, hasFocus, row, column, insets);

			if (column != -1) {
				Range<?> range = entry.getRange();
				DateFormat format = DateFormat.getDateInstance();
				setToolTipText(entry.getName() + " from " + format.format((Date) range.lower())
						+ " to " + format.format((Date) range.upper()));

				if (column % 2 == 0) {
					setForeground(new Color(0x2ee62e));
				}
				else {
					setForeground(new Color(0xe62e2e));
				}
			}
			else {
				setToolTipText(null);
				setForeground(new Color(0x2e2ee6));
			}

			return component;
		}
	}
}
