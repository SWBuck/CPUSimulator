package CPUSimulator;

/**
 * 
 * @author Sam Buck
 * 
 */
public class Memory {
	private Cell[] cells;

	public Memory(int i) {
		this.cells = new Cell[i];
		for(int j = 0; j < this.cells.length; j++){
			this.cells[j] = new Cell("0");
		}
	}

	public String printChanged() {
		boolean change = false;
		String s = "Recently Modified Cells";
		for (int i = 0; i < this.cells.length; i++) {
			if (cells[i] != null && cells[i].getFlag()) {
				change = true;
				s = s + "\nM[0x" + Integer.toHexString(i) + "]\t" + cells[i].print();
				cells[i].reset();
			}
		}
		if (!change) {
			s = s + "\n" + "null";
		}
		return s;
	}

	public Cell getCell(int index) {
		return cells[index];
	}

	public Cell[] getCells() {
		return this.cells;
	}

	public void setCell(int index, String d) {
		cells[index] = new Cell(d);
	}
}
