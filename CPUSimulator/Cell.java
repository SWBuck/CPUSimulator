package CPUSimulator;

/**
 * 
 * @author Sam Buck
 * 
 */
public class Cell {
	private String binary, hex;
	private boolean flag;

	public Cell(String d) {
		this.hex = d;
		this.binary = Integer.toBinaryString(Integer.parseInt(this.hex, 16));
		update();
		this.flag = true;
	}

	private void update() {
		while (this.binary.length() < 16) {
			this.binary = "0" + this.binary;
		}
		while(this.hex.length() < 4){
			this.hex = "0" + this.hex;
		}
	}

	public String print() {
		return String.format("%s",
				this.getBinaryFormatted() + "\t" + this.getHex());
	}

	private String getBinaryFormatted() {
		String b = "";
		for (int i = 0; i < 15; i += 4) {
			b += this.getBinary().substring(i, i + 4) + " ";
		}
		return b.substring(0, b.length() - 1);
	}

	public void reset() {
		this.flag = false;
	}

	public boolean getFlag() {
		return this.flag;
	}

	public String getBinary() {
		return this.binary;
	}

	public String getHex() {
		return this.hex;
	}
}
