package org.koekepan.Minecraft.behaviours;

import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientResourcePackStatusPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientSettingsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientTabCompletePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerAbilitiesPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerChangeHeldItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerInteractEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerMovementPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPlaceBlockPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerStatePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerSwingArmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerUseItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCloseWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientConfirmTransactionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCreativeInventoryActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientEnchantItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientWindowActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientSpectatePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientSteerBoatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientSteerVehiclePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientUpdateSignPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientVehicleMovePacket;
import com.github.steveice10.mc.protocol.packet.login.client.EncryptionResponsePacket;
import com.github.steveice10.mc.protocol.packet.status.client.StatusPingPacket;
import com.github.steveice10.mc.protocol.packet.status.client.StatusQueryPacket;
import com.github.steveice10.packetlib.packet.Packet;
import org.koekepan.Minecraft.behaviours.client.ClientChatPacketBehaviour;
import org.koekepan.Minecraft.behaviours.client.ClientPlayerMovementPacketBehaviour;
import org.koekepan.Minecraft.behaviours.client.EstablishConnectionPacketBehaviour;
import org.koekepan.VAST.Connection.ClientConnectedInstance;
import org.koekepan.VAST.CustomPackets.EstablishConnectionPacket;
import org.koekepan.VAST.CustomPackets.PINGPONG;
import org.koekepan.VAST.Packet.BehaviourHandler;

public class ServerBoundPacketBehaviours extends BehaviourHandler<Packet> {

//	private SPSConnection spsConnection = null;
//	private IClientSession clientSession;
	private ClientConnectedInstance clientInstance;
	private ForwardPacketBehaviour serverForwarder;
	private ForwardPacketBehaviour clientForwarder;

//	public ServerBoundPacketBehaviours(IProxySessionNew proxySession) {
//		this.proxySession = proxySession;
//	}
//
//	public ServerBoundPacketBehaviours(IProxySessionNew proxySession, SPSConnection spsConnection) {
//		this.proxySession = proxySession;
//		this.spsConnection = spsConnection;
//	}

	public ServerBoundPacketBehaviours(ClientConnectedInstance clientInstance) {
		this.clientInstance = clientInstance;
	}
	

//	public void registerDefaultBehaviours(IClientSession clientSession) {
//		this.clientSession = clientSession;
//		clearBehaviours();
//		registerBehaviour(HandshakePacket.class, new ClientHandshakePacketBehaviour(this.clientSession));										// 0x06 Player Position And Look
//		registerBehaviour(LoginStartPacket.class, new ClientLoginStartPacketBehaviour(proxySession, spsConnection));												// 0x01 Login Start
//	}

	public void registerForwardingBehaviour() {

		//////////////////////////////////// PACKETS THAT ARE SENT TO THE SERVER /////////////////////////////////////

		serverForwarder = new ForwardPacketBehaviour(clientInstance, true);
		registerBehaviour(EncryptionResponsePacket.class, serverForwarder);
		registerBehaviour(ClientTeleportConfirmPacket.class, serverForwarder);
		registerBehaviour(ClientTabCompletePacket.class, serverForwarder);
		registerBehaviour(ClientChatPacket.class, 						new ClientChatPacketBehaviour(clientInstance));
		registerBehaviour(ClientRequestPacket.class, serverForwarder);
		registerBehaviour(ClientSettingsPacket.class, serverForwarder);
		registerBehaviour(ClientConfirmTransactionPacket.class, serverForwarder);
		registerBehaviour(ClientEnchantItemPacket.class, serverForwarder);
		registerBehaviour(ClientWindowActionPacket.class, serverForwarder);
		registerBehaviour(ClientCloseWindowPacket.class, serverForwarder);
		registerBehaviour(ClientPluginMessagePacket.class, serverForwarder);
		registerBehaviour(ClientPlayerInteractEntityPacket.class, serverForwarder);
		registerBehaviour(ClientKeepAlivePacket.class, serverForwarder);

		registerBehaviour(ClientPlayerPositionPacket.class, 			new ClientPlayerMovementPacketBehaviour(clientInstance));
		registerBehaviour(ClientPlayerPositionRotationPacket.class, 	new ClientPlayerMovementPacketBehaviour(clientInstance));

		registerBehaviour(ClientPlayerRotationPacket.class, serverForwarder);
		registerBehaviour(ClientPlayerMovementPacket.class, serverForwarder);
		registerBehaviour(ClientVehicleMovePacket.class, serverForwarder);
		registerBehaviour(ClientSteerBoatPacket.class, serverForwarder);
		registerBehaviour(ClientPlayerAbilitiesPacket.class, serverForwarder);
		registerBehaviour(ClientPlayerActionPacket.class, serverForwarder);
		registerBehaviour(ClientPlayerStatePacket.class, serverForwarder);
		registerBehaviour(ClientSteerVehiclePacket.class, serverForwarder);
		registerBehaviour(ClientResourcePackStatusPacket.class, serverForwarder);
		registerBehaviour(ClientPlayerChangeHeldItemPacket.class, serverForwarder);
		registerBehaviour(ClientCreativeInventoryActionPacket.class, serverForwarder);
		registerBehaviour(ClientUpdateSignPacket.class, serverForwarder);
		registerBehaviour(ClientPlayerSwingArmPacket.class, serverForwarder);
		registerBehaviour(ClientSpectatePacket.class, serverForwarder);
		registerBehaviour(ClientPlayerPlaceBlockPacket.class, serverForwarder);
		registerBehaviour(ClientPlayerUseItemPacket.class, serverForwarder);
		
		registerBehaviour(StatusQueryPacket.class, serverForwarder);
		registerBehaviour(StatusPingPacket.class, serverForwarder);

		registerBehaviour(EstablishConnectionPacket.class,				new EstablishConnectionPacketBehaviour(clientInstance) );  // Custom packet for establishing a connection (login) with the server

		registerBehaviour(PINGPONG.class, new PINGPONGBehaviour(clientInstance));
	}
}
