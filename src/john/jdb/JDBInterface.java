package john.jdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import main.Paths;

public class JDBInterface {
	
	private Process pc;
	private OutputStream out;
	private int tcpPort;
	private String deviceID;
	private String packageName;
	private final String srcPath = "src";
	private JDBMonitor outMon;
	private JDBMonitor errMon;
	
//	public static void main(String[] args) 
//	{
//		ArrayList<Integer> bps = new ArrayList<Integer>();
//		bps.add(648);
//		bps.add(649);
//		bps.add(650);
//		bps.add(651);
//		
//		JDBInterface jdb = new JDBInterface("015d3c26c9540809", "com.bae.drape.gui.calculator", 7777);
//		
//		jdb.initJDB();
//		jdb.setBreakPointsAtLines("com.bae.drape.gui.calculator.CalculatorActivity", bps);
//		jdb.setMonitorStatus(true);
//		
//	}
	
	public JDBInterface(String deviceID, String packageName, int tcpPort) {
		this.deviceID = deviceID;
		this.packageName = packageName;
		this.tcpPort = tcpPort;
	}

	public void initJDB() 
	{
		String pID = getPID(packageName);

		try {
			pc = Runtime.getRuntime().exec(Paths.adbPath +" -s " + deviceID + " forward tcp:" + tcpPort + " jdwp:" + pID);
			pc.waitFor();
			pc = Runtime.getRuntime().exec("jdb -sourcepath " + srcPath + " -attach localhost:" + tcpPort);
		} catch (IOException | InterruptedException e) { e.printStackTrace(); }
		
		printStreams();
		//System.out.println("here");
		
		out = pc.getOutputStream();
		
		if (pc == null) {
			System.out.println("pc is null");
		}
	}
	
	public void setBreakPointsAtLines(String className, ArrayList<Integer> lineNumbers)
	{
		for (int lineNumber: lineNumbers) {
			try {
				out.write(("stop at " + className + ":" + lineNumber + "\n").getBytes());
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<String> getBPsHit()
	{
		return outMon.getBPsHit();
	}
	
	public void exitJDB() {
		outMon.exitThread();
		errMon.exitThread();
		
		try {
			out.write("exit\n".getBytes());
			out.flush();
			pc.waitFor();
		} catch (IOException | InterruptedException e) { e.printStackTrace(); }
	}
	
	public void setMonitorStatus(boolean OnOrOff)
	{
		try {	
			if (OnOrOff) {
				out.write("monitor cont\n".getBytes());
				out.flush();
			}
			else {
				out.write("unmonitor 1\n".getBytes());
				out.flush();
			}
		} catch (IOException e) {e.printStackTrace(); }
	}
	
	private void printStreams() 
	{
		outMon = new JDBMonitor(new BufferedReader(new InputStreamReader(pc.getInputStream())));
		errMon = new JDBMonitor(new BufferedReader(new InputStreamReader(pc.getErrorStream())));
		
		Thread outThread = new Thread(outMon);
		Thread errThread = new Thread(errMon);
		
		outThread.start();
		errThread.start();
	}
	
	private String getPID(String packageName)
	{
		Process prc;
		BufferedReader in;
		String line;
		
		try {
			prc = Runtime.getRuntime().exec(Paths.adbPath + " -s " + deviceID + " shell ps |grep " + packageName);
			in = new BufferedReader(new InputStreamReader(prc.getInputStream()));
			
			while ((line = in.readLine())!=null) {
				if (!line.endsWith(packageName)) continue;
				String[] parts = line.split(" ");
				for (int i = 1; i < parts.length; i++) {
					if (parts[i].equals(""))	continue;
					return parts[i].trim();
				}
			}
		} catch (IOException e) { e.printStackTrace(); }
		
		return "-1";
	}
}
