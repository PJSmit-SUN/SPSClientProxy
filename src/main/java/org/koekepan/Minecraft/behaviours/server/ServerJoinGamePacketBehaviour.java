package org.koekepan.Minecraft.behaviours.server;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.packetlib.packet.Packet;

import org.koekepan.VAST.Connection.ClientConnectedInstance;
import org.koekepan.VAST.Packet.Behaviour;
import org.koekepan.VAST.Packet.PacketWrapper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerJoinGamePacketBehaviour implements Behaviour<Packet> {
	public static boolean joined_game = false;
	private ClientConnectedInstance clientInstance;
//	private IServerSession serverSession;
//	private static Packet joinpacket = null;
	
	@SuppressWarnings("unused")
	private ServerJoinGamePacketBehaviour() {}
	
	
	public ServerJoinGamePacketBehaviour(ClientConnectedInstance clientInstance) {
		this.clientInstance = clientInstance;
//		this.serverSession = serverSession;
	}

//	public static Packet get_packet() {
//		return joinpacket;
//	}

	@Override
	public void process(Packet packet) {
		System.out.println("ServerJoinGamePacketBehaviour::process => Processing ServerJoinGamePacket");
		ServerJoinGamePacket serverJoinPacket = (ServerJoinGamePacket) packet;

		clientInstance.setEntityID(serverJoinPacket.getEntityId());
		clientInstance.setJoined(true);

//		PacketWrapper.setProcessed(packet, true);
		clientInstance.getSession().send(packet);
		clientInstance.getPacketSender().removePacket(packet);

		clientInstance.getPacketSender().startClientSender();
	}
}