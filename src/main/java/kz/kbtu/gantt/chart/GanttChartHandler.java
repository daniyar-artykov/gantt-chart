package kz.kbtu.gantt.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class GanttChartHandler {

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
	private static JPanel ganttTab;
	private static Component ganttPanel;
	private static JScrollPane scrollPane;

	private static GanttChartPane<Date, DefaultGanttEntry<Date>> _ganttChartPane;
	private static GanttChart<Date, DefaultGanttEntry<Date>> _ganttChart;

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

		ganttTab = new JPanel();

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
		ganttPanel = getDemoPanel();
		ganttTab.add(ganttPanel);
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
		final JTextField taskDesc  = new JTextField();
		panel.add(taskDesc);

		JLabel startLabel = new JLabel("Start date");
		panel.add(startLabel);
		final JTextField startDate  = new JTextField();
		panel.add(startDate);

		JLabel durationLabel = new JLabel("Duration");
		panel.add(durationLabel);
		final JTextField duration = new JTextField();
		panel.add(duration);

		JLabel dueLabel = new JLabel("Due date");
		panel.add(dueLabel);
		final JTextField dueDate  = new JTextField();
		panel.add(dueDate);
		dueDate.setEditable(false);

		JLabel progressLabel = new JLabel("Progress");
		panel.add(progressLabel);
		final JTextField progress  = new JTextField();
		panel.add(progress);

		JLabel personLabel = new JLabel("Assigned person");
		panel.add(personLabel);
		final JComboBox<String> comboBox = new JComboBox<String>(getAllUsersName());
		comboBox.setEditable(true);
		panel.add(comboBox);

		JLabel mainTaskLabel = new JLabel("Main task");
		panel.add(mainTaskLabel);
		final JComboBox<String> mainTask = new JComboBox<String>(getAllTasks());
		mainTask.setEditable(true);
		panel.add(mainTask);

		JLabel perviousTaskLabel = new JLabel("Pervious task");
		panel.add(perviousTaskLabel);
		final JComboBox<String> perviousTask = new JComboBox<String>(getAllTasks());
		perviousTask.setEditable(true);
		panel.add(perviousTask);

		JButton saveBtn = new JButton("Save");
		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Task t = new Task();
				t.setCreator(currentUser);
				t.setDuration(Integer.parseInt(duration.getText()));
				t.setParent(getTaskById(new Long(mainTask.getSelectedIndex())));
				t.setProgress(Double.parseDouble(progress.getText()));
				//				t.setResponsible(responsible);
				t.setSequence(getTaskById(new Long(perviousTask.getSelectedIndex())));
				try {
					t.setStartDate(DATE_FORMAT.parse(startDate.getText()));
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
				t.setTaskDescription(taskDesc.getText());

				em.getTransaction().begin();
				em.persist(t);
				em.getTransaction().commit();
				
				newFrame.setVisible(false);

				ganttTab.remove(ganttPanel);
				ganttPanel = getDemoPanel();
				ganttTab.add(ganttPanel);

				ganttTab.validate();
				ganttTab.repaint();
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

		Calendar dayBefore2 = Calendar.getInstance();
		dayBefore2.add(Calendar.DAY_OF_WEEK, -2);
		Calendar dayAfter7 = Calendar.getInstance();
		dayAfter7.add(Calendar.DAY_OF_WEEK, 7);

		model.setRange(new TimeRange(dayBefore2.getTime(), dayAfter7.getTime()));

		List<Task> tasks = em.createQuery("from Task t").getResultList();

		Map<Long, DefaultGanttEntry<Date>> sequenceMap = new HashMap<Long, DefaultGanttEntry<Date>>();

		for(Task task:tasks) {
			if(task.getParent() == null) {
				Calendar dueDate = Calendar.getInstance();
				dueDate.setTime(task.getStartDate());
				dueDate.add(Calendar.DAY_OF_WEEK, task.getDuration());
				DefaultGanttEntry<Date> group1 = new DefaultGanttEntry<Date>(task.getTaskDescription(), Date.class, 
						new TimeRange(task.getStartDate(), dueDate.getTime()), task.getProgress()/100);
				group1.setExpanded(true);

				if(!task.getChilds().isEmpty()) {
					for(Task child : task.getChilds()) {
						dueDate.setTime(child.getStartDate());
						dueDate.add(Calendar.DAY_OF_WEEK, child.getDuration());
						DefaultGanttEntry<Date> childGroup = new DefaultGanttEntry<Date>(child.getTaskDescription(), Date.class, 
								new TimeRange(child.getStartDate(), dueDate.getTime()), child.getProgress()/100);
						group1.addChild(childGroup);
						sequenceMap.put(child.getId(), childGroup);
						if(child.getSequence() != null) {
							if(sequenceMap.containsKey(child.getSequence().getId())) {
								model.getGanttEntryRelationModel().addEntryRelation(
										new DefaultGanttEntryRelation<DefaultGanttEntry<Date>>(
												sequenceMap.get(child.getSequence().getId()), 
												childGroup, GanttEntryRelation.ENTRY_RELATION_FINISH_TO_START));
								sequenceMap.remove(child.getSequence().getId());
							} else {
								sequenceMap.put(child.getId(), childGroup);
							}
						}
					}
				}

				model.addGanttEntry(group1);
			}
		}

		return model;
	}

	private static Task getTaskById(Long id) {
		System.out.println("user id " + id);
		if(id == null) {
			return null;
		}
		return em.find(Task.class, id);
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
