package CPUSimulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * @author Sam Buck
 */
public class Simulator {
	private Register mar, ir, pc, mbr, ac;
	private Memory memory;
	private int clockCycle, r, o;
	private String opCode;

	public static void main(String[] a) {
		Simulator sim = new Simulator();
		sim.load();
		sim.run();
	}

	public Simulator() {
		this.mar = new Register("MAR", 12);
		this.pc = new Register("PC", 12);
		this.ir = new Register("IR", 16);
		this.mbr = new Register("MBR", 16);
		this.ac = new Register("AC", 16);

		this.memory = new Memory(4096);
		this.clockCycle = 0;
		this.r = 0;
	}

	public void load() {
		this.loadText("CPUTest12.txt");
		this.printRegisters();
		System.out.println(this.memory.printChanged() + "\n");
		this.r = 1;
	}

	public void run() {
		while (r == 1) {
			this.fetch();
			this.decode();
			this.execute();
			this.cfi();
			this.showState();
		}
	}

	private void loadText(String n) {
		try {
			Scanner scan = new Scanner(new File(n));
			this.pc.loadHex(scan.next());
			scan.nextLine();
			while (scan.hasNextLine()) {
				int i = Integer.parseInt(scan.next(), 16);
				String d = scan.next();
				this.memory.setCell(i, d);
				scan.nextLine();
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.println("File Error");
			System.exit(0);
		}
	}

	public void fetch() {
		this.mar.loadHex(this.pc.getHex());
		this.ir.loadHex(this.memory.getCell(this.mar.getDecimal()).getHex());
		this.pc.increment();
		this.clockCycle += 2;
	}

	public void decode() {
		this.opCode = this.ir.getBinary().substring(0, 4);
		this.mar.loadBinary(this.ir.getBinary().substring(4, 16));
		this.clockCycle++;
	}

	public String getInstruction(String n) {
		String instruction = null;
		switch (n) {
		case "0000":
			instruction = "JnS X";
			break;
		case "0001":
			instruction = "Load X";
			break;
		case "0010":
			instruction = "Store X";
			break;
		case "0011":
			instruction = "Add X";
			break;
		case "0100":
			instruction = "Subt X";
			break;
		case "0101":
			instruction = "Input";
			break;
		case "0110":
			instruction = "Output";
			break;
		case "0111":
			instruction = "Halt";
			break;
		case "1000":
			instruction = "Skipcond";
			break;
		case "1001":
			instruction = "Jump X";
			break;
		case "1010":
			instruction = "Clear";
			break;
		case "1011":
			instruction = "AddI X";
			break;
		case "1100":
			instruction = "JumpI X";
			break;
		case "1101":
			instruction = "LoadI X";
			break;
		case "1110":
			instruction = "StoreI X";
			break;
		case "1111":
			instruction = "Not used";
			break;
		}
		return instruction;
	}

	public void execute() {
		switch (this.getInstruction(this.opCode)) {
		case "JnS X":
			this.mbr.loadBinary12Bit(this.pc.getBinary());
			this.memory.setCell(this.mar.getDecimal(), this.mbr.getHex());
			this.mar.increment();
			this.pc.loadBinary(this.mar.getBinary());
			this.clockCycle += 3;
			break;
		case "Load X":
			this.mbr.loadHex(this.memory.getCell(this.mar.getDecimal())
					.getHex());
			this.ac.loadHex(this.mbr.getHex());
			this.clockCycle += 2;
			break;
		case "Store X":
			this.mbr.loadHex(this.ac.getHex());
			this.memory.setCell(this.mar.getDecimal(), this.mbr.getHex());
			this.clockCycle += 2;
			break;
		case "Add X":
			this.mbr.loadBinary(this.memory.getCell(this.mar.getDecimal())
					.getBinary());
			char[] sum = new char[16];
			boolean carry = false;
			boolean overflow = false;
			String a = this.ac.getBinary();
			String m = this.mbr.getBinary();
			for (int i = 15; i >= 0; i--) {
				if (a.charAt(i) == '1' && m.charAt(i) == '1') {
					if (!carry) {
						sum[i] = '0';
					} else {
						sum[i] = '1';
					}
					carry = true;
				} else if (a.charAt(i) == '0' && m.charAt(i) == '0') {
					if (!carry) {
						sum[i] = '0';
					} else {
						sum[i] = '1';
						carry = false;
					}
				} else {
					if (!carry) {
						sum[i] = '1';
					} else {
						sum[i] = '0';
					}
				}
				if (i == 1 && carry) {
					overflow = true;
				}
				if (i == 0 && carry && overflow) {
					this.o = 1;
				}
			}
			this.ac.loadBinary(String.valueOf(sum));
			this.clockCycle += 2;
			break;
		case "JumpI X":
			this.pc.loadBinary(this.memory.getCell(this.mar.getDecimal())
					.getBinary().substring(4));
			this.clockCycle++;
			break;
		case "LoadI X":
			this.mar.loadBinary(this.memory.getCell(this.mar.getDecimal())
					.getBinary().substring(4));
			this.mbr.loadHex(this.memory.getCell(this.mar.getDecimal())
					.getHex());
			this.ac.loadBinary(this.mbr.getBinary());
			this.clockCycle += 3;
			break;
		case "StoreI X":
			this.mar.loadBinary(this.memory.getCell(this.mar.getDecimal())
					.getBinary().substring(4));
			this.mbr.loadBinary(this.ac.getBinary());
			this.memory.setCell(this.mar.getDecimal(), this.mbr.getHex());
			this.clockCycle += 3;
			break;
		case "Subt X":
			this.mbr.loadBinary(this.memory.getCell(this.mar.getDecimal())
					.getBinary());
			char[] sumS = new char[16];
			boolean carryS = false;
			String aS = this.ac.getBinary();
			String mS = this.mbr.getNegativeBinary();
			for (int i = 15; i >= 0; i--) {
				if (aS.charAt(i) == '1' && mS.charAt(i) == '1') {
					if (!carryS) {
						sumS[i] = '0';
					} else {
						sumS[i] = '1';
					}
					carryS = true;
				} else if (aS.charAt(i) == '0' && mS.charAt(i) == '0') {
					if (!carryS) {
						sumS[i] = '0';
					} else {
						sumS[i] = '1';
						carryS = false;
					}
				} else {
					if (!carryS) {
						sumS[i] = '1';
					} else {
						sumS[i] = '0';
					}
				}
			}
			this.ac.loadBinary(String.valueOf(sumS));
			this.clockCycle += 2;
			break;
		case "Halt":
			this.r = 0;
			this.clockCycle++;
			break;
		case "Clear":
			this.ac.clear();
			this.clockCycle++;
			break;
		case "AddI X":
			this.mar.loadBinary(this.memory.getCell(this.mar.getDecimal())
					.getBinary().substring(4));
			this.mbr.loadBinary(this.memory.getCell(this.mar.getDecimal())
					.getBinary());
			char[] sumB = new char[16];
			boolean carryB = false;
			boolean overflowB = false;
			String aB = this.ac.getBinary();
			String mB = this.mbr.getBinary();
			for (int i = 15; i >= 0; i--) {
				if (aB.charAt(i) == '1' && mB.charAt(i) == '1') {
					if (!carryB) {
						sumB[i] = '0';
					} else {
						sumB[i] = '1';
					}
					carry = true;
				} else if (aB.charAt(i) == '0' && mB.charAt(i) == '0') {
					if (!carryB) {
						sumB[i] = '0';
					} else {
						sumB[i] = '1';
						carry = false;
					}
				} else {
					if (!carryB) {
						sumB[i] = '1';
					} else {
						sumB[i] = '0';
					}
				}
				if (i == 1 && carryB) {
					overflow = true;
				}
				if (i == 0 && carryB && overflowB) {
					this.o = 1;
				}
			}
			this.ac.loadBinary(String.valueOf(sumB));
			this.clockCycle += 3;
			break;
		case "Skipcond":
			String i = this.ir.getBinary().substring(4, 6);
			int acD = this.ac.getDecimal();
			if ((i.equals("00") && acD < 0) || (i.equals("01") && acD == 0)
					|| (i.equals("10") && acD > 0)) {
				this.pc.increment();
			}
			this.clockCycle++;
			break;
		case "Input":
			break;
		case "Output":
			break;
		case "Jump X":
			this.pc.loadBinary(this.ir.getBinary().substring(4));
			this.clockCycle++;
			break;
		}
	}

	public void cfi() {

	}

	public void showState() {
		System.out.println("Clock Cycle: " + this.clockCycle);
		this.printRegisters();
		System.out.println(this.getInstruction(this.opCode) + " instruction");
		if (this.r == 1) {
			System.out.println("Overflow = " + this.o + " Run Status = True");
		} else {
			System.out.println("Overflow = " + this.o + " Run Status = False");
		}
		System.out.println(this.memory.printChanged());

		System.out.println();
	}

	public void printRegisters() {
		System.out.println(this.pc.print());
		System.out.println(this.mar.print());
		System.out.println(this.ir.print());
		System.out.println(this.ac.print());
		System.out.println(this.mbr.print());

	}
}
