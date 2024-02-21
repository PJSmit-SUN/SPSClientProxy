//package org.koekepan.herobrineproxy.packet.behaviours.client;
//
//import org.koekepan.herobrineproxy.ConsoleIO;
//import org.koekepan.herobrineproxy.Logger;
//import org.koekepan.herobrineproxy.behaviour.Behaviour;
//import org.koekepan.herobrineproxy.session.IProxySessionNew;
//
//import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
//import com.github.steveice10.packetlib.packet.Packet;
//import org.koekepan.herobrineproxy.sps.SPSConnection;
//
//public class ClientLoginStartPacketBehaviour implements Behaviour<Packet>{
//
//	private SPSConnection spsConnection;
//	private IProxySessionNew proxySession;
//
//	@SuppressWarnings("unused")
//	private ClientLoginStartPacketBehaviour() {}
//
//
//	public ClientLoginStartPacketBehaviour(IProxySessionNew proxySession, SPSConnection spsConnection) {
//		this.proxySession = proxySession;
//		this.spsConnection = spsConnection;
//	}
//
//
//	@Override
//	public void process(Packet packet) {
//		LoginStartPacket loginPacket = (LoginStartPacket)packet;
//		String username = loginPacket.getUsername();
//
////		spsConnection.unsubscribed(username);
//		spsConnection.subscribe(100, 100, 1000, username); // TODO: This Should be a subscribe to the player location
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e) {
//			throw new RuntimeException(e);
//		}
//
//		String serverHost = proxySession.getServerHost();
//		int serverPort = proxySession.getServerPort();
//		proxySession.setUsername(username);
////		ConsoleIO.println("ClientLoginStartPacketBehaviour");
//		Logger.log(this, Logger.Level.INFO, new String[]{"network", "connection", "initialisation", "behaviour"} ,"Player \"" + username + "\" is connecting to <" + serverHost + ":" + serverPort + ">");
//		proxySession.connect(serverHost, serverPort);
//	}
//
//}
