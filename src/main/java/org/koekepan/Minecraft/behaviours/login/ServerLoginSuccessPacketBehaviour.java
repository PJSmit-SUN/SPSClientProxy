package org.koekepan.Minecraft.behaviours.login;

import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket;
import com.github.steveice10.packetlib.packet.Packet;


import org.koekepan.VAST.Connection.ClientConnectedInstance;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;

public class ServerLoginSuccessPacketBehaviour implements Behaviour<Packet> {

//	public static Boolean loginSuccess = false;
	private ClientConnectedInstance clientInstance;
	private static Packet loginPacket;

	public static long lastPacketTime = 0;

	@SuppressWarnings("unused")
	private ServerLoginSuccessPacketBehaviour() {}
	
	
	public ServerLoginSuccessPacketBehaviour(ClientConnectedInstance clientInstance) {
		this.clientInstance = clientInstance;
	}

//    public static Packet get_packet() {
//		return loginPacket;
//    }

	@Override
	public void process(Packet packet) {

		long time = System.currentTimeMillis();

		System.out.println("ServerLoginSuccessPacketBehaviour::process => Processing ServerLoginSuccessPacket");

		if (time - lastPacketTime < 100) {
			return;
		}

		lastPacketTime = time;


		LoginSuccessPacket loginSuccessPacket = (LoginSuccessPacket)packet;
//		clientInstance.getSession().send(loginSuccessPacket);
		clientInstance.getPacketSender().removePacket(packet);

		System.out.println("Player \""+loginSuccessPacket.getProfile().getName()+"\" has successfully logged into the remote server");

		this.clientInstance.setUUID(loginSuccessPacket.getProfile().getId());
	}
}