import processing.core.*;

import java.util.*;

import com.restfb.*;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.types.Page;
import com.restfb.types.Post;
import com.restfb.types.User;

import twitter4j.*;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import controlP5.*;

import ddf.minim.*;


public class SocialPush extends PApplet {

	StatusListener listener;
	Connection<Post> prevFilteredFeed;
	FacebookClient fbc;
	
	String keyword1 = "";
	String keyword2 = "";
	String keyword3 = "";
	String keyword4 = "";
	
	Textfield keyword1TextField;
	Textfield keyword2TextField;
	Textfield keyword3TextField;
	Textfield keyword4TextField;
	PFont font;
	ControlP5 controlP5;
	
	Button goButton;
	PImage goBg;
	PImage rararLogo;
	int tabCount = 0;
	Textfield currentTextField;
	
	Minim minim;
	AudioPlayer in1;
	AudioPlayer in2;
	AudioPlayer in3;
	AudioPlayer in4;
	
	public void setup() {
		size(366, 600);
		background(0);
		smooth();
		controlP5 = new ControlP5(this);
		controlP5.setColorForeground(0xFFFFFF);
		controlP5.setColorBackground(0xFFFFFF);
		controlP5.setColorActive(0xFFFFFF);

		PImage header = loadImage("Header.png");
		font = loadFont("OfficinaSans-Book-14.vlw");
		textFont(font, 14);
		text("enter four keywords", 50, 180);
		image(header, 50, 50);
		fill(255);
//		getGoogleTalkPushes();
//		getTwitterPushes();
//		getFacebookAccessToken();
//		getFacebookPushes();
		
		//Group textFieldGroup = controlP5.addGroup("textFieldGroup");
		
		// apply the newly loaded bit font to the below textfield.
		keyword1TextField = controlP5.addTextfield("keyword1TextField",50,217,270,36);//this is where you type the words
		style("keyword1TextField", keyword1TextField, "First Keyword");
		keyword1TextField.setFocus(true);
		
		keyword2TextField = controlP5.addTextfield("keyword2TextField",50,277,270,36);//this is where you type the words
		style("keyword2TextField", keyword2TextField, "Second Keyword");
		
		keyword3TextField = controlP5.addTextfield("keyword3TextField",50,337,270,36);//this is where you type the words
		style("keyword3TextField", keyword3TextField, "Third Keyword");
		
		keyword4TextField = controlP5.addTextfield("keyword4TextField",50,397,270,36);//this is where you type the words
		style("keyword4TextField", keyword4TextField, "Fourth Keyword");
		
		currentTextField = keyword1TextField;
		goBg = loadImage("GoButton.png");
		goButton = controlP5.addButton("goButton").setPosition(50, 463).setSize(90, 36).setImage(goBg).setCaptionLabel("");
		
		rararLogo = loadImage("RARAR-LOGO.png");
		image(rararLogo, 298, 476);
//		textFieldGroup.add(keyword1TextField);
//		textFieldGroup.add(keyword2TextField);
//		textFieldGroup.add(keyword3TextField);
//		textFieldGroup.add(keyword4TextField);
		
		// instantiate a Minim object
		minim = new Minim(this);
		in1 = minim.loadFile("new-mail.mp3");
		in2 = minim.loadFile("SIMToolkitPositiveACK.mp3");
		in3 = minim.loadFile("sms-received1.mp3");
		in4 = minim.loadFile("sms-received3.mp3");

	}
	
	public void goButton(int theValue) {
		keyword1 = keyword1TextField.getText().toLowerCase();
		keyword2 = keyword2TextField.getText().toLowerCase();
		keyword3 = keyword3TextField.getText().toLowerCase();
		keyword4 = keyword4TextField.getText().toLowerCase();
		
		getTwitterPushes();
		println(keyword1 + " " + keyword2 + " " + keyword3 + " " + keyword4);
	}
	
	
	public void keyPressed() {

		if (key == TAB) {
			tabCount++;
			currentTextField.setFocus(false);
			switch(tabCount % 4) {
			case 0:
				println("Key Press! on 0");
				keyword1TextField.setFocus(true);
				currentTextField = keyword1TextField;
				break;
			case 1:
				println("Key Press! on 1");
				keyword2TextField.setFocus(true);
				currentTextField = keyword2TextField;
				break;
			case 2:
				println("Key Press! on 2");
				keyword3TextField.setFocus(true);
				currentTextField = keyword3TextField;
				break;
			case 3:
				println("Key Press! on 3");
				keyword4TextField.setFocus(true);
				currentTextField = keyword4TextField;
				break;
			}
			
		}
	}
	
	public void style(String textFieldId, Textfield cp5TextField, String captionLabel ) {
		controlP5.getController(textFieldId).setColorActive(color(75,75,75));                //highlighted/clicked color
		controlP5.getController(textFieldId).setColorBackground(color(255,255,255));
		controlP5.getController(textFieldId).setColorValue(color(0,0,0));
		controlP5.getController(textFieldId).setCaptionLabel("");
		//controlP5.getController(textFieldId).getCaptionLabel().setFont(font).toUpperCase(false).setSize(14);
		
		cp5TextField.setColorCursor(color(150, 150, 150));
		cp5TextField.setFont(font);
	}

	public void draw() {
		noStroke();
		fill(0, 0, 0, 25);
		rect(322, 0, 45, 600);
		
		if(controlP5.isMouseOver(controlP5.getController("goButton"))) {
			cursor(HAND);
		} else if ( controlP5.isMouseOver(controlP5.getController("keyword1TextField")) || 
					controlP5.isMouseOver(controlP5.getController("keyword2TextField")) || 
					controlP5.isMouseOver(controlP5.getController("keyword3TextField")) ||
					controlP5.isMouseOver(controlP5.getController("keyword4TextField"))) {
			cursor(TEXT);
		}else {
			cursor(ARROW);
		}

	}
	
	public void getGoogleTalkPushes() {
		ConnectionConfiguration connConfig = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
		XMPPConnection connection = new XMPPConnection(connConfig);
		
		// First connect to google talk
		try {
			connection.connect();
			println("Connected to " + connection.getHost());
		} catch (XMPPException ex) {
			println("Failed to connect to " + connection.getHost());
			System.exit(1);;
		}
		
		// Second connect to user name
		try {
			connection.login("raphaelarar@gmail.com", "Echo4930");
			println("Logged in as " + connection.getUser());
			
			//Presence presence = new Presence(Presence.Type.available);
			//connection.sendPacket(presence);
		} catch (XMPPException ex) {
			println("Failed to log in as " + connection.getUser());
			System.exit(1);
		}
		
		PacketFilter filter = new AndFilter(new PacketTypeFilter(Message.class));
		PacketCollector collector = connection.createPacketCollector(filter);
		while (true) {
			Packet packet = collector.nextResult();
			if (packet instanceof Message) {
				Message message = (Message) packet;
				println("message " + message.getBody());
				if (message != null && message.getBody() !=null ) {
					println("Received message from " + packet.getFrom() + " : " + (message != null ? message.getBody() : "NULL"));
				}
			}
		}
		
//		ChatManager chatManager = connection.getChatManager();
//		Chat newChat = chatManager.createChat("guru@googlelabs.com", new MessageListener() {
//			
//			@Override
//			public void processMessage(Chat chat, Message message) {
//				println("Received Message: " + message.getBody());
//			}
//		});
//		
//		try {
//			newChat.sendMessage("define stupendous");
//			println("message sent");
//		} catch (XMPPException e) {
//			println("Error delivering block");
//		}
	}
	
	public void getFacebookAccessToken() {
		fbc = new DefaultFacebookClient("CAAF51xN8ZAe4BAE65rwQShh0ooRjAZCeHIjENtVaEpVPjwoOaovCQabmZAAZBaKa8lpSndZBp1F87jHGqeZA0GBZAIuNjxE3kHMyjF9wZA9ZAfd8wddNYQZAg6ZA1oX3F9XMXYBzvcu5q86Ux1T0yHme9b91sN3HdONTfjhz4nszoP8HmoETW8Y7fdf6BShGa4OtccZD");
		//fbc.obtainExtendedAccessToken("415439628559854", "b66d2162338fc92247c23f233f1d2f57", "CAAF51xN8ZAe4BAMOcJfH2EWZCKnAbXJLrJ0hK5jniAIgbmPzZAs0tAUw2LEJKEBx2SplPrGom3qSMw57rX2eQJPkH6FyanXZAlg6zlo9vrF0R0wp7vfZB18uIa1WmEQw1b8YcHQSDs7XJPhD7nXPrwG2LETUJ6s29WBjnb6FMkMo6LuFm24hwCpQkLE5tRhZBKpoe1zgRwDwZDZD");
	}

	public void getFacebookPushes() {
		println("entered fb loop");
		Connection<Post> filteredFeed = 
				fbc.fetchConnection("me/feed", Post.class);
		if (!filteredFeed.equals(prevFilteredFeed)) {
			println("we have a valid update!");
		}
		prevFilteredFeed = filteredFeed;

	}


	public void getTwitterPushes() {
		// set up the listener
		listener = new StatusListener() {
			@Override
			public void onStatus(Status status) {
				String content = status.getText();
				if (content.toLowerCase().indexOf(keyword1) != -1) {
					println("WE HAVE KEYWORD 1 = " + keyword1);
					noStroke();
					fill(color(204, 102, 153));
					ellipse(342, 235, 10, 10);
					in1.play(0);
				}
				
				if (content.toLowerCase().indexOf(keyword2) != -1) {
					println("WE HAVE KEYWORD 2 = " + keyword2);
					noStroke();
					fill(color(51, 204, 255));
					ellipse(342, 295, 10, 10);
					in2.play(0);
				}
				
				if (content.toLowerCase().indexOf(keyword3) != -1) {
					println("WE HAVE KEYWORD 3 = " + keyword3);
					noStroke();
					fill(color(255, 255, 102));
					ellipse(342, 355, 10, 10);
					in3.play(0);
				}
				
				if (content.toLowerCase().indexOf(keyword4) != -1) {
					println("WE HAVE KEYWORD 4 = " + keyword4);
					noStroke();
					fill(color(153, 204, 102));
					ellipse(342, 415, 10, 10);
					in4.play(0);
				}


			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				// System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
			}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				// System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
			}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
				// System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
			}

			@Override
			public void onStallWarning(StallWarning warning) {
				// System.out.println("Got stall warning:" + warning);
			}

			@Override
			public void onException(Exception ex) {
				//ex.printStackTrace();
			}
		};

		FilterQuery fq = new FilterQuery();
		String keywords[] = {keyword1, keyword2, keyword3, keyword4};
		fq.track(keywords);


		TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
		twitterStream.getConfiguration();
		twitterStream.addListener(listener);
		twitterStream.filter(fq);
	}

	public static void main(String args[]) {
		//PApplet.main("Pushed");
		PApplet.main(new String[] {"--present", "SocialPush"});
	}
}
