
package program;

import machine.Machine;

// this can be used in operations, instructions, basic blocks, basic block edges
// or in program
// this must be multiplied by number of executions
public class TiwariResults
{
    private double baseEnergy = 0.0; // E = V_dd * I * cycles * tau, unit Joule
    private double overheadEnergy = 0.0;
    private int cycles = 0;

    private double V_dd = 0.0;        // supply voltage
    private double clockFrequency = 0.0;
    private double tau = 0.0;         // 1 / clockFrequency;


    // E = V_dd * I * N * tau
    public TiwariResults(Machine machine) {
        this.V_dd = machine.getSupplyVoltage();
        this.clockFrequency = machine.getClockFrequency();
        this.tau = 1.0 / this.clockFrequency;
    }

    public TiwariResults(double supplyVoltage, double clockFrequency) {
        this.V_dd = supplyVoltage;
        this.clockFrequency = clockFrequency;
        this.tau = 1.0 / this.clockFrequency;
    }

    public void setCycles(int cycles) {
        this.cycles = cycles;
    }
    private int getCycles() {
        return cycles;
    }
    private double getBaseEnergy() {
        return baseEnergy;
    }

    private double getOverheadEnergy() {
        return overheadEnergy;
    }


    // add n times the energy and cycles in t to this
    public void add(TiwariResults t, int n) {
        // this results object will collect results for
        // other results objects, initialize energy to 0, as this will
        // be used to calculate the currents
        if (n <= 0) // 0: add nothing => return, we don't support subtraction
            return;
        double be = this.getBaseEnergy()
            + n * t.getBaseEnergy();
        double oe = this.getOverheadEnergy()
            + n * t.getOverheadEnergy();
        int     c = this.getCycles()
            + n * t.getCycles();
        this.setCycles(c);
        this.setBaseEnergy(be);
        this.setOverheadEnergy(oe);
    }

    public void setBaseCurrent(double baseCurrent) {
        // E = VINT
        this.baseEnergy = V_dd * baseCurrent * cycles * tau;
    }
    public void setOverheadCurrent(double overheadCurrent) {
        // as if cycles = 1
        this.overheadEnergy = V_dd * overheadCurrent * tau;
    }
    private void setBaseEnergy(double baseEnergy) {
        this.baseEnergy = baseEnergy;
    }
    private void setOverheadEnergy(double overheadEnergy) {
        this.overheadEnergy = overheadEnergy;
    }

    private double getBaseCurrent() {
        if (cycles == 0)
            return 0.0;
        return baseEnergy / (V_dd * cycles * tau);
    }

    private double getOverheadCurrent() {
        // overhead current = total current - base current
        double TI = getCurrent();
        double BI = getBaseCurrent();
        return TI - BI;
    }



    private double getCurrent() {
        if (cycles == 0)
            return 0.0;
        // E = VINT => I = E/(VNT)
        return getEnergy() / (V_dd * cycles * tau);
    }
    public double getEnergy() {
        return getBaseEnergy() + getOverheadEnergy();
    }
    private double getBaseEnergyPercentage() {
        return 100.0 * getBaseEnergy() / getEnergy();
    }
    private double getOverheadEnergyPercentage() {
        return 100.0 * getOverheadEnergy() / getEnergy();
    }

    public static String getHeaderString() {
        int len = 9;
        String ibase = "BI(mA)";   while (ibase.length() < len) ibase += " ";
        String iover = "OI(mA)"; while (iover.length() < len) iover += " ";
        String itot  = "totI(mA)";  while (itot.length()  < len) itot += " ";

        String cycles = "Cycles";  while (cycles.length() < len) cycles += " ";

        String ebase = "BE(nJ)";   while (ebase.length() < len) ebase += " ";
        String ebasep = "BE/totE";     while (ebasep.length() < len) ebasep += " ";
        String eover = "OE(nJ)"; while (eover.length() < len) eover += " ";
        String eoverp = "OE/totE";     while (eoverp.length() < len) eoverp += " ";
        String etot  = "totE(nJ)";  while (etot.length()  < len) etot += " ";

        return ibase + iover + itot
            + cycles
            + ebase + ebasep + eover + eoverp + etot;
    }

    public String toString() {
        int len = 9;
        String bc = String.format("%.2f", 1e3 * getBaseCurrent());
        while (bc.length() < len) bc += " ";
        String oc = String.format("%.2f", 1e3 * getOverheadCurrent());
        while (oc.length() < len) oc += " ";
        String tc = String.format("%.2f", 1e3 * getCurrent());
        while (tc.length() < len) tc += " ";

        String c = String.format("%d", getCycles());
        while (c.length() < len) c += " ";

        String be = String.format("%.2f", 1e9 * getBaseEnergy());
        while (be.length() < len) be += " ";
        String bp = String.format("%.1f%%", getBaseEnergyPercentage());
        while (bp.length() < len) bp += " ";

        String oe = String.format("%.2f", 1e9 * getOverheadEnergy());
        while (oe.length() < len) oe += " ";
        String op = String.format("%.1f%%", getOverheadEnergyPercentage());
        while (op.length() < len) op += " ";

        String e = String.format("%.2f", 1e9 * getEnergy());
        while (e.length() < len) e += " ";

        return 
            bc
            + oc
            + tc
            + c
            + be
            + bp
            + oe
            + op
            + e;
    }

    // example from tiwari-94
    public static void main(String[] args) {

        // correct results:
        // t  = BI(mA)   OI(mA)   totI(mA) Cycles   BE(nJ)   (%)      OE(nJ)   (%)      totE(nJ) 
        // t1 = 309.60   17.20    326.80   1        25.54    (94.7%)  1.42     (5.3%)   26.96    
        // t2 = 313.60   17.90    331.50   1        25.87    (94.6%)  1.48     (5.4%)   27.35    
        // t3 = 400.20   5.25     402.83   2        66.03    (99.3%)  0.43     (0.7%)   66.47    
        // t4 = 308.30   12.25    312.38   3        76.30    (98.7%)  1.01     (1.3%)   77.31    
        // t5 = 306.50   3.30     307.60   3        75.86    (99.6%)  0.27     (0.4%)   76.13    
        // t  = 326.80   5.59     332.39   10       269.61   (98.3%)  4.61     (1.7%)   274.22   

        double supplyVoltage = 3.3;
        double clockFrequency = 40*1e6;
        TiwariResults t1 = new TiwariResults(supplyVoltage, clockFrequency);
        TiwariResults t2 = new TiwariResults(supplyVoltage, clockFrequency);
        TiwariResults t3 = new TiwariResults(supplyVoltage, clockFrequency);
        TiwariResults t4 = new TiwariResults(supplyVoltage, clockFrequency);
        TiwariResults t5 = new TiwariResults(supplyVoltage, clockFrequency);
        TiwariResults t  = new TiwariResults(supplyVoltage, clockFrequency);

        t1.setCycles(1);
        t1.setBaseCurrent(309.6 * 1e-3);
        t1.setOverheadCurrent(17.2 * 1e-3);  // between 5 and 1

        t2.setCycles(1);
        t2.setBaseCurrent(313.6 * 1e-3);
        t2.setOverheadCurrent(17.9 * 1e-3);  // between 1 and 2

        t3.setCycles(2);
        t3.setBaseCurrent(400.2 * 1e-3);
        t3.setOverheadCurrent(5.25 * 1e-3);  // between 2 and 3

        t4.setCycles(3);
        t4.setBaseCurrent(308.3 * 1e-3);
        t4.setOverheadCurrent(12.25 * 1e-3);  // between 3 and 4

        t5.setCycles(3);
        t5.setBaseCurrent(306.5 * 1e-3);
        t5.setOverheadCurrent(3.3 * 1e-3);  // between 4 and 5

        t.add(t1, 1);
        t.add(t2, 1);
        t.add(t3, 1);
        t.add(t4, 1);
        t.add(t5, 1);
        System.out.println("t  = " + t.getHeaderString());
        System.out.println("t1 = " + t1);
        System.out.println("t2 = " + t2);
        System.out.println("t3 = " + t3);
        System.out.println("t4 = " + t4);
        System.out.println("t5 = " + t5);
        System.out.println("t  = " + t);
    }
}
