package CPUSimulator;

/**
 * 
 * @author Sam Buck
 * 
 */
public class Register {
	private String binary, hex, name;
	private int decimal, size;

	public Register(String n, int s) {
		this.name = n;
		this.size = s;
		this.binary = "0";
		update();
	}

	private void update() {
		updateBinary();
		this.decimal = Integer.parseInt(this.binary, 2);
		this.hex = Integer.toHexString(this.decimal);
	}

	private void updateBinary() {
		while (this.binary.length() < this.size) {
			this.binary = "0" + this.binary;
		}
	}

	public void loadHex(String d) {
		this.hex = d;
		this.decimal = Integer.parseInt(d, 16);
		this.binary = Integer.toBinaryString(this.decimal);
		updateBinary();
	}

	public void loadBinary(String d) {
		this.binary = d;
		this.decimal = Integer.parseInt(this.binary, 2);
		this.hex = Integer.toHexString(this.decimal);
		updateBinary();
	}

	public void loadBinary12Bit(String d){
		this.binary = this.binary.substring(0, 4) + d;
		this.decimal = Integer.parseInt(this.binary, 2);
		this.hex = Integer.toHexString(this.decimal);
		updateBinary();
	}
	
	public String getNegativeBinary(){
		//invert, add 1
		char[] a = this.binary.toCharArray().clone();
		for(int i = this.binary.length()-1; i >= 0; i--){
			if(this.binary.charAt(i) == '0'){
				a[i] = '1';
			} else {
				a[i] = '0';
			}
		}
		String r = String.valueOf(a);
		
		for (int i = r.length() - 1; i >= 0; i--) {
			if (r.charAt(i) == '0') {
				a[i] = '1';
				break;
			} else {
				a[i] = '0';

			}
		}
		return String.valueOf(a);
		
	}

	public void increment() {
		char[] a = this.binary.toCharArray().clone();
		for (int i = this.binary.length() - 1; i >= 0; i--) {
			if (this.binary.charAt(i) == '0') {
				a[i] = '1';
				break;
			} else {
				a[i] = '0';

			}
		}
		this.binary = String.valueOf(a);
		update();
	}

	public String print() {
		return this.name + "\t" + String.format("%4s", this.getHex()) + "\t"
				+ this.formatBinary() + "\t" + this.getDecimal();
	}

	private String formatBinary() {
		String b = "";
		for (int i = 0; i < this.size - 1; i += 4) {
			b += this.getBinary().substring(i, i + 4) + " ";
		}
		return String.format("%20s", b.substring(0, b.length() - 1));
	}

	public void clear() {
		this.binary = "0";
		update();
	}

	public String getBinary() {
		return this.binary;
	}

	public String getHex() {
		return this.hex;
	}

	public int getDecimal() {
		return this.decimal;
	}

	public String getName() {
		return this.name;
	}
}
