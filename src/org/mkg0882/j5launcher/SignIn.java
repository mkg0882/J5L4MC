package org.mkg0882.j5launcher;

public class SignIn implements Runnable {
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			String user_code = MSAuthRoutine.getUserCodes();
			Launcher.message.setText("Open a web browser on a modern device,"
						  + "navigate to https://www.microsoft.com/link"
						  + "and enter the following code when prompted:");
			Launcher.message.setVisible(true);
			Launcher.usercode.setText(user_code);
			Launcher.basepanel.invalidate();
			Launcher.basepanel.repaint();
			Launcher.f.addWindowListener(Launcher.f);
			int status = MSAuthRoutine.waitForLinkAuth();
			if (status == -1) {
				System.out.println("ERROR: Link Auth wait loop exited prematurely.");
				break;
			}
			String profile_name = MSAuthRoutine.finalSignIn();
			if (profile_name == null) {
				System.out.println("Something went wrong while retrieving profile name!");
				break;
			} else {
				Launcher.profname.setText("Signed in to Minecraft as: " + profile_name);
				MSAuthRoutine.saveTokens(Paths.tokenpath);
				Launcher.playbtn.setEnabled(true);
				Launcher.usercode.setVisible(false);
			}
			Thread.currentThread().interrupt();
		}
		System.out.println("Exited Sign-In Thread.");
	}
}
