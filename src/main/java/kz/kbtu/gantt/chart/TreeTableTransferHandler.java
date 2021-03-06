package kz.kbtu.gantt.chart;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.table.TableModel;

import com.jidesoft.grid.*;

public class TreeTableTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 782232536379799089L;

	@Override
	public int getSourceActions(JComponent component) {
		if(component instanceof TreeTable) {
			TreeTable treeTable = (TreeTable) component;
			if(treeTable.getModel() != null) {
				return MOVE;
			}
		}
		return NONE;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Transferable createTransferable(JComponent component) {
		if(component instanceof TreeTable) {
			TreeTable treeTable = (TreeTable) component;
			TreeTableModel<Row> model = (TreeTableModel<Row>) treeTable.getModel();
			List<Row> rows = getParentsOnly(model, treeTable.getSelectedRows());
			if(!rows.isEmpty()) {
				return new RowTransferable(rows);
			}
		}

		return null;
	}

	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		// the remove is done in the importData method
	}

	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		boolean tasty = false;
		for(DataFlavor flavor : transferFlavors) {
			if(RowTransferable.FLAVOR.equals(flavor)) {
				tasty = true;
				break;
			}
		}
		return tasty && comp instanceof TreeTable;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canImport(TransferSupport support) {
		Component component = support.getComponent();
		if(!(component instanceof TreeTable)
				|| !canImport((JComponent) component, support.getDataFlavors())) {
			return false;
		}

		if(support.isDrop() && support.getDropAction() == MOVE) {
			javax.swing.JTable.DropLocation location = (javax.swing.JTable.DropLocation) support.getDropLocation();
			int rowIndex = location.getRow();
			boolean insert = location.isInsertRow();

			try {
				List<Row> rows = (List<Row>) support.getTransferable().getTransferData(RowTransferable.FLAVOR);
				TreeTableModel<Row> model = (TreeTableModel<Row>) ((TreeTable) component).getModel();
				Node parentNode = getParentNode(model, rowIndex, insert);
				for(Row row : rows) {
					if(!canImport(model, parentNode, row)) {
						return false;
					}
				}
				return true;
			}
			catch (UnsupportedFlavorException e) {
				// shouldn't happen;
				return false;
			}
			catch (IOException e) {
				// shouldn't happen;
				return false;
			}
		}
		return false;
	}

	private boolean canImport(ITreeTableModel<Row> model, Node parent, Row row) {
		return row != parent && !isAnchestor(row, parent)
				&& model.getRoot() instanceof Node && isAnchestor((Node) model.getRoot(), row);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean importData(JComponent component, Transferable transferable) {
		if(component instanceof TreeTable) {
			TreeTable treeTable = (TreeTable) component;
			int rowIndex = treeTable.getSelectedRow();
			boolean insert = false;
			if(rowIndex != -1) {
				insert = !(((TreeTableModel<Row>) treeTable.getModel()).getRowAt(rowIndex) instanceof Expandable);
			}

			return importRows(treeTable, transferable, rowIndex, insert);
		}
		return false;
	}

	@Override
	public boolean importData(TransferSupport support) {
		if(!canImport(support)) {
			return false;
		}

		Component component = support.getComponent();
		if(support.getDropAction() == MOVE && component instanceof TreeTable) {
			javax.swing.JTable.DropLocation location = (javax.swing.JTable.DropLocation) support.getDropLocation();
			int rowIndex = location.getRow();
			boolean insert = location.isInsertRow();

			return importRows((TreeTable) component, support.getTransferable(), rowIndex, insert);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean importRows(TreeTable treeTable, Transferable transferable, int rowIndex,
			boolean insert) {

		try {
			List<Row> rows = new ArrayList<Row>((List<Row>)
					transferable.getTransferData(RowTransferable.FLAVOR));

			ITreeTableModel<Row> model = (ITreeTableModel<Row>) treeTable.getModel();

			Node parentNode = getParentNode(model, rowIndex, insert);
			if(parentNode instanceof Expandable) {
				Expandable parent = (Expandable) parentNode;
				int insertIndex = parent.getChildrenCount();
				if(insert && rowIndex >= 0 && rowIndex < ((TableModel) model).getRowCount()) {
					int childIndex = parent.getChildIndex(model.getRowAt(rowIndex));
					if(childIndex != -1) {
						insertIndex = childIndex;
					}
				}

				for(Iterator<Row> iter = rows.iterator(); iter.hasNext();) {
					// in Java 5 not able to check this in the canImport() method
					Row row = iter.next();
					if(!canImport(model, parent, row)) {
						iter.remove();
					}
				}
				if(rows.isEmpty()) {
					return false;
				}

				Map<Expandable, List<Row>> rowsByParent = new LinkedHashMap<Expandable, List<Row>>();
				int index = insertIndex;
				for(Row row : rows) {
					Expandable rowParent = row.getParent();
					if(parent == rowParent
							&& parent.getChildIndex(row) < insertIndex) {
						index--;
					}
					List<Row> list = rowsByParent.get(rowParent);
					if(list == null) {
						list = new ArrayList<Row>();
						rowsByParent.put(rowParent, list);
					}
					list.add(row);
				}
				for(Entry<Expandable, List<Row>> entry : rowsByParent.entrySet()) {
					entry.getKey().removeChildren(entry.getValue());
				}
				parent.addChildren(index, rows);
				return true;
			}
			return false;
		}
		catch (Throwable e) {
			// drag and drop swallows any exception so log here
			e.printStackTrace();
			return false;
		}
	}

	private Node getParentNode(ITreeTableModel<Row> model, int rowIndex, boolean insert) {
		Node parentNode;
		if(rowIndex < 0 || rowIndex >= ((TableModel) model).getRowCount()) {
			parentNode = (Node) model.getRoot();
		}
		else {
			parentNode = model.getRowAt(rowIndex);
			if(insert) {
				parentNode = parentNode.getParent();
			}
		}
		return parentNode;
	}

	private List<Row> getParentsOnly(ITreeTableModel<? extends Row> model, int[] selectedRows) {
		List<Row> selectedParents = new ArrayList<Row>();
		for(int rowIndex : selectedRows) {
			Row row = model.getRowAt(rowIndex);

			boolean add = true;
			for(Iterator<Row> iter = selectedParents.iterator(); iter.hasNext();) {
				Row current = iter.next();
				if(isAnchestor(current, row)) {
					add = false;
					break;
				}
				else if(isAnchestor(row, current)) {
					iter.remove();
				}
			}

			if(add) {
				selectedParents.add(row);
			}
		}

		return selectedParents;
	}

	private boolean isAnchestor(Node nodeA, Node nodeB) {
		if(nodeB != null) {
			Expandable parent = nodeB.getParent();
			return parent == nodeA || isAnchestor(nodeA, parent);
		}
		return false;
	}

	private static class RowTransferable implements Transferable {

		private static final DataFlavor FLAVOR =
				new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=java.util.List", "List of TreeTable Rows");

		private final List<Row> rows;

		public RowTransferable(List<Row> rows) {
			this.rows = rows;
		}

		public List<Row> getRows() {
			return rows;
		}

		public List<Row> getTransferData(DataFlavor flavor) throws UnsupportedFlavorException,
		IOException {
			if(isDataFlavorSupported(flavor)) {
				return getRows();
			}
			else {
				throw new UnsupportedFlavorException(flavor);
			}
		}

		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { FLAVOR };
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return FLAVOR.equals(flavor);
		}

	}
}

