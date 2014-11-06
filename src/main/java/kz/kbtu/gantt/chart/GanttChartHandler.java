package kz.kbtu.gantt.chart;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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

	private static GanttChartPane<Date, DefaultGanttEntry<Date>> _ganttChartPane;
	private static GanttChart<Date, DefaultGanttEntry<Date>> _ganttChart;
	private static JFrame newTaskJFrame;
	private static boolean _debug = true;
	public static void main(String [] args) {

		JFrame mainFrame = new JFrame("Gantt chart");
		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent){
				System.exit(0);
			}        
		});

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());
		JButton b = new JButton("Add task");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newTaskJFrame = new JFrame("Add task");
				newTaskJFrame.setSize(500, 250);
				newTaskJFrame.setVisible(true);
				JPanel panel = new JPanel();
				panel.setLayout(new GridLayout(0, 2));
				JLabel descLabel = new JLabel("Description");
				panel.add(descLabel);
				JTextField taskDesc  = new JTextField("Task 2");
				panel.add(taskDesc);
				JLabel startLabel = new JLabel("Start date");
				panel.add(startLabel);
				JTextField startDate  = new JTextField("5/11/2014");
				panel.add(startDate);
				JLabel durationLabel = new JLabel("Duration");
				panel.add(durationLabel);
				JTextField duration  = new JTextField("7");
				panel.add(duration);
				JLabel dueLabel = new JLabel("Due date");
				panel.add(dueLabel);
				JTextField dueDate  = new JTextField("12/11/2014");
				panel.add(dueDate);
				dueDate.setEditable(false);
				JButton saveBtn = new JButton("Save");
				saveBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						newTaskJFrame.setVisible(false);
					}
				});
				panel.add(saveBtn);
				JButton closeBtn = new JButton("Close");
				closeBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						newTaskJFrame.setVisible(false);
					}
				});
				panel.add(closeBtn);
				newTaskJFrame.add(panel);
			}
		});
		controlPanel.add(b);
		mainFrame.add(controlPanel);


		controlPanel.add(getDemoPanel());
		mainFrame.setVisible(true);
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
		DefaultGanttEntry<Date> group = new DefaultGanttEntry<Date>("Task 1", Date.class, new TimeRange(today.getTime(), dayAfter5.getTime()), 0);
		group.setExpanded(true);
		DefaultGanttEntry<Date> group1 = new DefaultGanttEntry<Date>("Task 2", Date.class, new TimeRange(dayAfter5.getTime(), dayAfter12.getTime()), 0);
		group1.setExpanded(true);

		DefaultGanttEntry<Date> subgroup = new DefaultGanttEntry<Date>("Sub group",
				Date.class, new TimeRange(today.getTime(), dayAfter5.getTime()), 0);
		subgroup.setExpanded(true);

		for (int relation : new int[]{
				GanttEntryRelation.ENTRY_RELATION_FINISH_TO_START,
				GanttEntryRelation.ENTRY_RELATION_START_TO_FINISH,
				GanttEntryRelation.ENTRY_RELATION_FINISH_TO_FINISH,
				GanttEntryRelation.ENTRY_RELATION_START_TO_START}) {

			// test subentries
			//                DefaultGanttEntry<Date> child1 = new DefaultGanttEntry<Date>("Task 1." + relation,
			//                        Date.class, new TimeRange(today.getTime(), dayAfter7.getTime()), 0.4);
			//                child1.addSubEntry(new DefaultGanttEntry<Date>("Task 1.1." + relation,
			//                        Date.class, new TimeRange(today.getTime(), dayAfter2.getTime()), 0.4));
			//                child1.addSubEntry(new DefaultGanttEntry<Date>("Task 1.2." + relation,
			//                        Date.class, new TimeRange(dayAfter5.getTime(), dayAfter7.getTime()), 0.4));
			DefaultGanttEntry<Date> child1 = new DefaultGanttEntry<Date>("Task 1." + relation,
					Date.class, new TimeRange(today.getTime(), dayAfter2.getTime()), 0.4);
			DefaultGanttEntry<Date> child2 = new DefaultGanttEntry<Date>("Task 2." + relation,
					Date.class, new TimeRange(dayAfter2.getTime(), dayAfter5.getTime()), 0);

			group.addChild(child1);
			if (_debug) {
				group.addChild(new DefaultGanttEntry<Date>("Task 2." + relation + "spacer",
						new TimeRange(today.getTime(), today.getTime())));
			}
			group.addChild(child2);

			if (_debug) {
				child1 = new DefaultGanttEntry<Date>("Task 1." + relation,
						Date.class, new TimeRange(today.getTime(), dayAfter2.getTime()), 0.4);

				subgroup.addChild(child1);

				model.getGanttEntryRelationModel().addEntryRelation(new DefaultGanttEntryRelation<DefaultGanttEntry<Date>>(child1, child2, relation));
			}
			else {
				model.getGanttEntryRelationModel().addEntryRelation(new DefaultGanttEntryRelation<DefaultGanttEntry<Date>>(child1, child2, relation));
			}
		}

		if (_debug) {
			group.addChild(subgroup);
		}
		model.addGanttEntry(group);
		model.addGanttEntry(group1);

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