package discordrpc;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public class Main {
    private static boolean ready = false;
    private static JFrame frame = new JFrame("JavaRPC");
    private static DiscordRichPresence presence;
    private static Timer timer;

    private static DiscordRichPresence presenceBuilder(String title, String details, String imageKey,
	    String keyHoverText, int partysize, int partymax, String partytext) {
	/*
	 * Playing A Game: BOT NAME TITLE DETAILS
	 */
	DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder(details);
	presence.setDetails(title);
	presence.setParty(partytext, partysize, partymax);
	presence.setBigImage(imageKey, keyHoverText);
	return presence.build();
    }

    public static void main(String[] args) throws Exception {
	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	// Set presence:
	presence = presenceBuilder("Playing Life.exe", "Being a potato, as always.", "codescreen", "Coding in Java", 1,
		1, "Alone");
	// End of set presence
	frame = new JFrame("JavaRPC");
	GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	int width = gd.getDisplayMode().getWidth();
	int height = gd.getDisplayMode().getHeight();
	frame.getContentPane().setLayout(new FlowLayout());
	frame.getContentPane().setBackground(new Color(114, 137, 218));
	frame.setResizable(true);
	frame.setSize(width / 4, height / 4);
	frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	frame.setVisible(true);
	Runtime.getRuntime().addShutdownHook(new Thread(() -> {
	    System.out.println("Closing Discord hook.");
	    timer.cancel();
	    DiscordRPC.discordShutdown();
	}));

	initDiscord();
	System.out.println("Running callbacks...");

	while(true) {
	    DiscordRPC.discordRunCallbacks();
	    if(!ready)
		continue;
	    System.out.print("> ");
	    Scanner in = new Scanner(System.in);
	    String input = in.nextLine();
	    if(!input.equalsIgnoreCase("shutdown")) {
	    } else {
		frame.dispose();
		in.close();
		System.exit(0);
	    }
	    DiscordRPC.discordUpdatePresence(presence);
	}
    }

    private static void initDiscord() {
	DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
	    Main.ready = true;
	    System.out.println("User profile found: " + user.username + "#" + user.discriminator);
	    JLabel text = new JLabel("<html>User profile found: " + user.username + "#" + user.discriminator
		    + "<br />Sucessfully loaded Discord Rich Presence integration. </html>");
	    frame.getContentPane().add(text, SwingConstants.CENTER);
	    frame.setVisible(true);
	}).build();
	DiscordRPC.discordInitialize("755358751636062291", handlers, true);
	DiscordRPC.discordRegister("755358751636062291", "");
	DiscordRPC.discordUpdatePresence(presence);
	timer = new Timer();
	timer.schedule(new TimerTask() {
	    @Override
	    public void run() {
		DiscordRPC.discordUpdatePresence(presence);
	    }
	}, 1000L, 500L);
    }
}
