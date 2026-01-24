package org.mkg0882.j5launcher;

import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.*;

import org.mkg0882.j5launcher.json.InstanceEntry;
import org.mkg0882.j5launcher.json.InstanceInfo;
import org.mkg0882.j5launcher.json.InstanceList;
import org.mkg0882.j5launcher.json.VersionEntry;
import org.mkg0882.j5launcher.json.VersionManifest;

import com.google.gson.Gson;

public class InstanceEditor extends JFrame 
							implements WindowListener, ActionListener, ItemListener {
	public InstanceEditor iewindow =  null;

	private static final long serialVersionUID = 1102844236372348645L;
	private static Label instnamelbl = new Label("Instance Name:");
	private static TextField instname = new TextField();
	//private static Label jarpathlbl = new Label("Minecraft JAR Path:");
	private static TextField jarpath = new TextField();
	private static Label launchargslbl = new Label("Java Launch Arguments:");
	private static TextField launchargs = new TextField();
	private static Button savebtn = new Button("Save");
	private static Button cancelbtn = new Button("Cancel");
	private static Checkbox customchk = new Checkbox();
	private static Label versionlbl = new Label("Select Version:");	
	private static boolean isNew = true;
	private static int ilIndex=0;
	static JComboBox versionlist = new JComboBox();
	
	public InstanceEditor (InstanceEntry ie) {
		System.out.println("Instance Editor window opened.");
		iewindow = this;
		setTitle("Instance Editor");
		setSize(new Dimension(400, 180));
		addWindowListener(this);
		JPanel panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		constraints.fill = GridBagConstraints.VERTICAL;
		constraints.ipadx = 5;
		constraints.ipady = 5;
		layout.setConstraints(panel, constraints);
		panel.setLayout(layout);
		this.setContentPane(panel);
		
		panel.add(instnamelbl);
		instnamelbl.setAlignment(Label.RIGHT);
		panel.add(instname);
		constraints.gridwidth = GridBagConstraints.LINE_START;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.fill = GridBagConstraints.NONE;
		layout.setConstraints(instnamelbl, constraints);
		instname.setColumns(24);
		ilIndex = 0;
		if (ie != null) {
			isNew = false;
			instname.setText(ie.name);
			customchk.setState(ie.entry.custom);
			jarpath.setText(ie.entry.jarfile);
			for (InstanceEntry entry : Launcher.il.entries){
				if (entry.equals(ie)){
					break;
				} else {
					ilIndex++;
				}
			}
		} else {
			isNew = true;
			customchk.setState(false);
			instname.setText("");
			jarpath.setText("");
		}
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(instname, constraints);
		
		VersionManifest vm = FetchMCInfo.versionManifest();
		for (VersionEntry ve : vm.versions) {
			String currentItem = ve.id + " (" + ve.type + ")";
			versionlist.addItem(currentItem);
			int lastSelection = versionlist.getItemCount()-1;
			if (!isNew){
				//System.out.println(ve.id + " : " + ie.entry.version);
				if (ie.entry.version.contentEquals(currentItem)){
					versionlist.setSelectedIndex(lastSelection);
				}
			}
		}
		
		panel.add(versionlbl);
		versionlbl.setAlignment(Label.RIGHT);
		constraints.gridwidth = GridBagConstraints.LINE_START;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.fill = GridBagConstraints.NONE;
		layout.setConstraints(versionlbl, constraints);
		panel.add(versionlist);
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(versionlist, constraints);
		
		panel.add(customchk);
		customchk.setLabel("Custom JAR?");
		customchk.addItemListener(this);;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.fill = GridBagConstraints.NONE;
		constraints.gridwidth = GridBagConstraints.LINE_START;
		layout.setConstraints(customchk, constraints);
		panel.add(jarpath);
		jarpath.setColumns(24);
		jarpath.setEditable(false);
		jarpath.setEnabled(false);
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(jarpath, constraints);
		
		panel.add(launchargslbl);
		launchargslbl.setAlignment(Label.RIGHT);
		constraints.gridwidth = GridBagConstraints.LINE_START;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.fill = GridBagConstraints.NONE;
		layout.setConstraints(launchargslbl, constraints);
		panel.add(launchargs);
		launchargs.setColumns(24);
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(launchargs, constraints);
		
		panel.add(savebtn);
		savebtn.addActionListener(this);
		constraints.fill = GridBagConstraints.NONE;
		constraints.gridwidth = GridBagConstraints.LINE_START;
		constraints.anchor = GridBagConstraints.EAST;
		layout.setConstraints(savebtn, constraints);
		panel.add(cancelbtn);
		cancelbtn.addActionListener(this);
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.WEST;
		layout.setConstraints(cancelbtn, constraints);
		
		panel.setVisible(true);
		setVisible(true);
	}

	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		savebtn.removeActionListener(this);
		cancelbtn.removeActionListener(this);
		customchk.removeItemListener(this);
		this.removeWindowListener(this);
		this.dispose();
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
		if (e.getSource() == savebtn){
			InstanceEntry instance = new InstanceEntry();
			instance.name = instname.getText();
			InstanceInfo entry = new InstanceInfo(); 
			entry.jarfile = jarpath.getText();
			entry.custom = customchk.getState();
			entry.version = versionlist.getSelectedItem().toString();
			entry.folder = instname.getText();
			entry.launchargs = launchargs.getText();
			instance.entry = entry;
//			InstanceList il = null;
//			try {
//				File frf = new File(Paths.instancelistpath);
//				if (frf.exists()) {
//					FileReader entriesfile = new FileReader(frf);
//					Gson gson = new Gson();
//					il = gson.fromJson(entriesfile, InstanceList.class);
//				} else {
//					il = new InstanceList();
//					il.entries = new ArrayList<InstanceEntry>();
//					il.lastselection = 0;
//				}
//				il.entries.add(instance);
			    InstanceList ilocal = Launcher.il;
			    if (!isNew){
			    	ilocal.entries.remove(ilIndex);
			    	ilocal.entries.add(ilIndex, instance);
			    }
			    else {
			    	ilocal.entries.add(instance);
			    	Launcher.instancelist.addItem(instance.name + " ("+instance.entry.version+")");
			    }
				Launcher.il = ilocal;
				
//			} catch (FileNotFoundException ex) {
//				System.out.println("ERROR: Entries list file "+ Paths.instancelistpath +" not found!");
//				ex.printStackTrace();
//				return;
//			}
			FileWriter entriesfile = null;
			try {
				entriesfile = new FileWriter(Paths.instancelistpath);
				Gson gson = new Gson();
				entriesfile.write(gson.toJson(Launcher.il));
				entriesfile.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			savebtn.removeActionListener(this);
			cancelbtn.removeActionListener(this);
			customchk.removeItemListener(this);
			this.removeWindowListener(this);
			this.dispose();
		}
		if (e.getSource() == cancelbtn){
			savebtn.removeActionListener(this);
			cancelbtn.removeActionListener(this);
			customchk.removeItemListener(this);
			this.removeWindowListener(this);
			this.dispose();
		}
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == customchk) {
			jarpath.setEditable(customchk.getState());
			jarpath.setEnabled(customchk.getState());
			jarpath.setText("");
		}
	}

}
