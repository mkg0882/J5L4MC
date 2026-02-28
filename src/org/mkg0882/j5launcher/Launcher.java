package org.mkg0882.j5launcher;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.String;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.apache.commons.httpclient.protocol.Protocol;
import org.mkg0882.j5launcher.json.ConfigStore;
import org.mkg0882.j5launcher.json.InstanceEntry;
import org.mkg0882.j5launcher.json.InstanceList;
import org.mkg0882.j5launcher.json.VersionEntry;
import org.mkg0882.j5launcher.json.VersionManifest;

import com.google.gson.Gson;

public class Launcher extends JFrame
		implements WindowListener,
		ActionListener {
	
	private static final long serialVersionUID = -5041468455627834129L;
	static Launcher f = new Launcher();
	static GridLayout column = new GridLayout();
	static JPanel basepanel = new JPanel(column);
	static Button reqbtn = new Button("Start New Sign-In");
	static Button cancelbtn = new Button("Cancel Sign-In");
	static Button playbtn = new Button("Play");
	static Button signoutbtn = new Button("Sign Out");
	static Button fetchbtn = new Button("Get Version List");
	static Button newinstbtn = new Button("New Instance");
	static Button editinstbtn = new Button("Edit Instance");
	static Button deleteinstbtn = new Button("Delete Instance");
	static Label profname = new Label();
	static Label usercode = new Label();
	static Label instlabel = new Label("Select Instance:");
	static Label verlabel = new Label("Select Client Version");
	static Label message = new Label();
	static JComboBox instancelist = new JComboBox();
	public static InstanceList il = new InstanceList();
	static int lastselection = 0;
	private FileWriter f2;
	private static FileReader configread;
	private static FileWriter configwrite;
	//static InstanceEditor ie = null;
	
	public static void main(String[] args) {
		playbtn.setEnabled(false);
		profname.setText("Please wait...");
		ConfigStore config = new ConfigStore();
		//String testdate = "2025-12-11T21:41:04.0176564Z";
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		//sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		//try {
			//Date dt = sdf.parse(testdate);
			//Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			//cal.setTime(dt);
			//System.out.println(cal.getTimeInMillis());
		//} catch (ParseException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		try {
			configread = new FileReader(Paths.configpath);
			Gson gson = new Gson();
			config = gson.fromJson(configread, config.getClass());
			if (config.launcher_id != null && config.launcher_id != "") {
				MSAuthRoutine.appID = config.launcher_id;
			}
		} catch (FileNotFoundException e) {
			System.out.println("No config file found, assuming defaults.");
			config.launcher_id = MSAuthRoutine.appID;
			try {
				File f = new File(Paths.basepath());
				f.mkdirs();
				configwrite = new FileWriter(Paths.configpath);
				Gson gson = new Gson();
				configwrite.write(gson.toJson(config));
				configwrite.close();
			} catch (IOException e1) {
				System.out.println("ERROR: Unable to write new config file!");
				e1.printStackTrace();
			}
		}
		f.showLauncher();
		Protocol.registerProtocol("https", HttpRequest.bchttps);
		File file = new File(Paths.tokenpath);
		if (file.exists()) {
			if (MSAuthRoutine.loadTokens(Paths.tokenpath) != -1) {
				if (System.currentTimeMillis() >= MSAuthRoutine.mcexpirytime) {
					if (MSAuthRoutine.msrefreshtoken != null 
							&& MSAuthRoutine.mcprofilename != null
							&& MSAuthRoutine.mcprofileuuid != null) {
						if (MSAuthRoutine.waitForAuthRefresh(MSAuthRoutine.msrefreshtoken) != -1){
							profname.setText("Signed in to Minecraft as: " + MSAuthRoutine.mcprofilename);
							MSAuthRoutine.finalSignIn();
							MSAuthRoutine.saveTokens(Paths.tokenpath);
							playbtn.setEnabled(true);
						}
					}
				} else if (System.currentTimeMillis() < MSAuthRoutine.mcexpirytime) {
					profname.setText("Signed in to Minecraft as: " + MSAuthRoutine.mcprofilename);
					playbtn.setEnabled(true);
				}
			} else {
				profname.setText("Saved sign-in data is incomplete. Please start a new sign-in session.");
			}
		} else {
			profname.setText("Currently not signed in. Please start a new sign-in session.");
		}
	}
	
	public void showLauncher() {
		f.setSize(850, 500);
		f.add(basepanel);
		f.setTitle("J5L4MC");
		f.addWindowListener(this);
		
		GridLayout single = new GridLayout();
		GridLayout halves = new GridLayout();
		GridLayout thirds = new GridLayout();
		GridLayout quarters = new GridLayout();
		
		column.setColumns(1);
		column.setRows(8);
		single.setColumns(1);
		single.setRows(1);
		halves.setColumns(2);
		halves.setRows(1);
		thirds.setColumns(3);
		thirds.setRows(1);
		quarters.setColumns(4);
		quarters.setRows(1);
		
		basepanel.setLayout(column);
		
		Panel row1 = new Panel(halves);
		row1.setLayout(halves);
		basepanel.add(row1);
		row1.add(reqbtn);
		reqbtn.addActionListener(this);
		row1.add(cancelbtn);
		cancelbtn.addActionListener(this);
		cancelbtn.setEnabled(false);
		
		Panel row2 = new Panel(single);
		row2.setLayout(single);
		basepanel.add(row2);
		row2.add(message);
		
		Panel row3 = new Panel(single);
		row3.setLayout(single);
		basepanel.add(row3);
		row3.add(usercode);
		usercode.setFont(new Font("Sans-serif", Font.BOLD, 32));
		usercode.setAlignment(Label.CENTER);
		
		Panel row4 = new Panel(single);
		row4.setLayout(single);
		basepanel.add(row4);
		row4.add(profname);
		profname.setAlignment(Label.CENTER);
		
		Panel row5 = new Panel(single);
		row5.setLayout(single);
		basepanel.add(row5);
		row5.add(instlabel);
		instlabel.setAlignment(Label.CENTER);
		
		Panel row6 = new Panel(single);
		row6.setLayout(single);
		basepanel.add(row6);
		
		Panel row7 = new Panel(thirds);
		row7.setLayout(thirds);
		basepanel.add(row7);
		row7.add(newinstbtn);
		newinstbtn.addActionListener(this);
		row7.add(editinstbtn);
		editinstbtn.addActionListener(this);
		row7.add(deleteinstbtn);
		
		Panel row8 = new Panel(single);
		row8.setLayout(single);
		basepanel.add(row8);
		row8.add(playbtn);
		playbtn.addActionListener(this);	
		
		populateInstanceList(Paths.instancelistpath);
		row6.add(instancelist);
		instancelist.setVisible(true);
		instancelist.setEnabled(true);
		instancelist.setLightWeightPopupEnabled(false);
		message.setAlignment(Label.CENTER);
		message.setVisible(false);
		message.setIgnoreRepaint(false);
		f.setVisible(true);
		basepanel.addNotify();
	}
	
    public void windowClosing(WindowEvent e) {
    	for (Thread t : Thread.getAllStackTraces().keySet()) {
    		if (t.getName().equals("signinthread")){
    			t.interrupt();
    		}
    	}
        f.dispose();
        Thread.currentThread().interrupt();
    }

	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == reqbtn) {
			reqbtn.setEnabled(false);;
			cancelbtn.setEnabled(true);
			SignIn p = new SignIn();
			Thread t = new Thread(p, "signinthread");
			t.start();
			return;
		}
		if (e.getSource() == cancelbtn) {
			for (Thread t : Thread.getAllStackTraces().keySet()) {
	    		if (t.getName().equals("signinthread")){
	    			t.interrupt();
	    		}
	    	}
			cancelbtn.setEnabled(false);
			reqbtn.setEnabled(true);
			return;
		}
		if (e.getSource() == playbtn){
			launchClient();
			return;
		}
		if(e.getSource() == newinstbtn) {
			newInstance();
			return;
		}
		if(e.getSource() == editinstbtn) {
			editInstance();
			return;
		}
		if(e.getSource() == deleteinstbtn){
			deleteInstance();
		}
		return;
	}
	
	public void newInstance() {
		InstanceEditor ie = new InstanceEditor(null);
		ie.iewindow.setVisible(true);
		ie.setVisible(true);
		return;
	}
	
	public void deleteInstance(){
		int instidx = instancelist.getSelectedIndex();
		il.entries.remove(instidx);
		instancelist.remove(instidx);
		Gson gson = new Gson();
		try {
			il.lastselection = 0;
			f2 = new FileWriter(Paths.instancelistpath);
			f2.write(gson.toJson(il, il.getClass()));
			f2.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
	}
	
	public void editInstance() {
		String instancename = instancelist.getSelectedItem().toString();
		InstanceEntry instance = null;
		FileReader entriesfile;
		try {
			entriesfile = new FileReader(Paths.instancelistpath);
			Gson gson = new Gson();
			il = gson.fromJson(entriesfile, il.getClass());
			for (InstanceEntry inst : il.entries) {
				if (instancename.contains(inst.name)) {
					instance = inst;
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: Entries list file "+ Paths.instancelistpath +" not found!");
			e.printStackTrace();
			return;
		}
		InstanceEditor ie = new InstanceEditor(instance);
		ie.iewindow.setVisible(true);
		ie.setVisible(true);
		return;
	}
	
	public void launchClient() {
		Gson gson = new Gson();
		try {
			il.lastselection = instancelist.getSelectedIndex();
			f2 = new FileWriter(Paths.instancelistpath);
			f2.write(gson.toJson(il, il.getClass()));
			f2.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		LaunchClient lc = new LaunchClient();
		String[] instancetemp = (instancelist.getSelectedItem().toString()).split(" ");
		lc.instance = instancetemp[0];
		System.out.println("Selected instance " + lc.instance);
		Thread t = new Thread(lc, "minecraftcient");
		t.start();
		f.dispose();
        Thread.currentThread().interrupt();
	}
	
	public static void populateInstanceList(String entriespath) {
		FileReader entriesfile;
		try {
			entriesfile = new FileReader(entriespath);
			Gson gson = new Gson();
			il = gson.fromJson(entriesfile, InstanceList.class);
			for (InstanceEntry instance : il.entries) {
				System.out.println("Adding instance" + instance.name);
				instancelist.addItem(instance.name + " ("+instance.entry.version+")");
			}
			if (il.lastselection >= 0) {
				lastselection = il.lastselection;
			} else {
				lastselection = 0;
			}
			instancelist.addActionListener(f);
			instancelist.setSelectedIndex(lastselection);
		} catch (FileNotFoundException e) {
			System.out.println("No instance list file was found, starting with empty list.");
			return;
		}
	}
	
	public static void versionsListPopulate(JComboBox list) {
		VersionManifest vm = FetchMCInfo.versionManifest();
		for (VersionEntry ve : vm.versions) {
			list.addItem(ve.id + " (" + ve.type + ")");
		}
	}
}
