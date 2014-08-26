package main;

import inputGeneration.JDBStuff;
import inputGeneration.SimpleTesting;
import inputGeneration.StaticInfo;

import java.io.File;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Main {

	public static ArrayList<Node> nodes = new ArrayList<Node>();
	
	public static void main(String[] args) {
		//File file = new File("/home/wenhaoc/AppStorage/TestThreads.apk");
		//File file = new File("/home/wenhaoc/workspace/Test/bin/Test.apk");
		//initAnalysis(file);
		
		File file = new File("/home/wenhaoc/AppStorage/Fast.apk");
		StaticInfo.getActivityNames(file);
		for (Node node: nodes) {
			Element e = (Element) node;
			System.out.println(e.getAttributes().getLength());
			e.setAttribute("LOL", "aaa");
		}
		System.out.println("---------------");
		for (Node node: nodes) {
			Element e = (Element) node;
			System.out.println(e.getAttributes().getLength());
			System.out.println(node.getAttributes().getNamedItem("LOL").getNodeValue());
			
		}
		//initAnalysis(file);
		try {
			
		} catch (Exception e1) {e1.printStackTrace();}
		
		try {
			SimpleTesting.clickAll(file);
		} catch (Exception e) {	e.printStackTrace();}
	}
	
	
	private static void initAnalysis(File file) {
		analysisTools.ApkTool.extractAPK(file);
		analysisTools.Soot.generateAPKData(file);
		StaticInfo.process_Intents_And_setContentView(file);
	}
	
	
	private static void testJDB(File file) {
		JDBStuff jdb = new JDBStuff();
		try {
			jdb.initJDB(file);
			jdb.setMonitorStatus(true);
			jdb.setBreakPointLine("com.example.helloworld.MainActivity", 321);
			while (true) {}
		} catch (Exception e) {	e.printStackTrace();}
	}
}
