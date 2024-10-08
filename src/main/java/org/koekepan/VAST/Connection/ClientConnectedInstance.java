package org.koekepan.VAST.Connection;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.ConnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.koekepan.App;
import org.koekepan.Minecraft.ChunkPosition;
import org.koekepan.Performance.PacketCapture;
import org.koekepan.VAST.CustomPackets.EstablishConnectionPacket;
import org.koekepan.VAST.Packet.PacketHandler;
import org.koekepan.VAST.Packet.SPSPacket;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class ClientConnectedInstance {
    private VastConnection vastConnection; // The connection to the VAST_COM server, used to publish packets to the vast network
    private PacketSender packetSender; // The packet sender, used to send packets to queue packets that are destined for the VAST_COM server and the client
    private PacketHandler packetHandler; // The packet handler, used to run packet behaviours that are received from the VAST_COM server and the client

    private Session session;

    private Boolean joined = false;
    private int entityID = -1; // The entity ID of the player, if the player has joined the game (otherwise -1)

    public static HashMap<PacketSender, ClientConnectedInstance> clientInstances_PacketSenders = new HashMap<PacketSender, ClientConnectedInstance>();
//    public static HashSet<UUID> playerUUIDs = new HashSet<UUID>();

    private UUID playerUUID = null;

    private String clientUsername;

    private int x_position = 0;
    private int y_position = 0;

    private boolean migrating = false;

    private String currentMinecratServerHost = null;
    private int currentMinecraftServerPort = 0;
    private String migratingMinecratServerHost = null;
    private int migratingMinecraftServerPort = 0;

    public ClientConnectedInstance( Session session, String VastHost, int VastPort) {
        this.session = session;
        this.session.addListener(new ClientSessionListener());

        this.packetSender = new PacketSender();
        this.packetHandler = new PacketHandler(this);

        this.vastConnection = new VastConnection(VastHost, VastPort, this);

        clientInstances_PacketSenders.put(packetSender, this);
        packetSender.setClientSession(this.session);

//        packetSender.startClientSender();
        packetSender.startServerSender();
        this.vastConnection.connect();


        new Thread(() -> { // This thread is used to disconnect the client if it hasn't joined the game after 5 seconds
            try {
                sleep(5000); // Sleep for 5 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!joined) {
                disconnect();
            }
        }).start();
    }

    public String getUsername() {
//        return session.getFlag("username");
        return clientUsername;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

//    public void addPlayerUUID(UUID id) {
//        playerUUIDs.add(id);
//    }
//
//    public void removePlayerUUID(UUID id) {
//        playerUUIDs.remove(id);
//    }

//    public boolean hasPlayerUUID(UUID id) {
//        return playerUUIDs.contains(id);
//    }

    public void setUUID(UUID id) {
//        this.addPlayerUUID(id);
        this.playerUUID = id;
    }

    public UUID getUUID() {
        return playerUUID;
    }

    private static class ClientSessionListener extends SessionAdapter { // This is the client listener (Listens to the packets sent/received from client)
        /*
        The ClientSessionListener should handle packets from the client, forwarding them to the actual Minecraft server.
        It should also handle the login process if necessary.
         */
        @Override
        public void packetReceived(PacketReceivedEvent event) { // Called when a packet is received from the client (Serverbound)
//            System.out.println("test");

//            System.out.println("Received packet (from client): " + event.getPacket().getClass().getSimpleName());

            if (event.getPacket() instanceof LoginStartPacket) { // Logins should be handled by the serverProxy via EstablishConnectionPacket
                LoginStartPacket startPacket = event.getPacket();
                // Extract the username from the login start packet
                String username = startPacket.getUsername();

                System.out.println("EmulatedServer: Received login packet from " + username);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                App.clientInstances.get(event.getSession()).setClientUsername(username);

//                App.clientInstances.get(event.getSession()).startPermanentSubscriptions(150, 150);
                EstablishConnectionPacket establishConnectionPacket = new EstablishConnectionPacket(username, true);

                if (App.clientInstances.get(event.getSession()) != null) {
                    App.clientInstances.get(event.getSession()).packetSender.addServerboundPacket(establishConnectionPacket);
                } else {
                    System.out.println("EmulatedServer: Client instance is null");
                }
//                App.clientInstances.get(event.getSession()).packetSender.addServerboundPacket(establishConnectionPacket );
//                App.clientInstances.get(event.getSession()).packetHandler.addPacket(establishConnectionPacket);

            } else {
                // Forward packets to the Minecraft server (VASt_COM)
                if (App.clientInstances.get(event.getSession()) != null) {
                    App.clientInstances.get(event.getSession()).packetSender.addServerboundPacket(event.getPacket());
                } else {
                    System.out.println("Client instance is null");
                }
            }
        }

        @Override
        public void connected(ConnectedEvent event) {
            // Called when client is connected to the actual Minecraft server
            System.out.println("Connected to server");
        }

//        @Override
//        public void packetSent(PacketReceivedEvent event) { // SERVERBOUND!
//            // Called when a packet is sent to the server
//            System.out.println("Sent packet (to server)" + event.getPacket().getClass().getSimpleName());
//        }

    }

    private void setClientUsername(String username) {
        this.clientUsername = username;
        this.vastConnection.subscribe(0,0,10, username);
    }

    public VastConnection getVastConnection() {
        return vastConnection;
    }

    public PacketSender getPacketSender() {
        return packetSender;
    }

    public Session getSession() {
        return session;
    }

    public void setJoined(Boolean joined) {
        this.joined = joined;

//        this.addChannelRegistration("Koekepan|migrate");
//        this.addChannelRegistration("Koekepan|kick");
//        this.addChannelRegistration("Koekepan|partition");
//        this.addChannelRegistration("Koekepan|latency");
    }

    public void addChannelRegistration(String channel) {
        byte[] payload = writeStringToPluginMessageData(channel);
        String registerMessage = "REGISTER";
        ClientPluginMessagePacket registerPacket = new ClientPluginMessagePacket(registerMessage, payload);
        this.packetSender.addServerboundPacket(registerPacket);
    }
    private byte[] writeStringToPluginMessageData(String message) {
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        ByteBuf buff = Unpooled.buffer();
        buff.writeBytes(data);
        return buff.array();
    }

    public boolean isJoined() {
        return joined;
    }

    public int getEntityID() {
        if (entityID == -1) {
            System.out.println("EntityID is -1");
        }
        return entityID;
    }

    public void setEntityID(int entityID) {
        System.out.println("<" + this.clientUsername + "> Set entityID to " + entityID);

        this.entityID = entityID;
    }


    int publishMoveCount = 10;
    public void set_position(int x, int y) { // TODO: This is currently done in the serverBound packet, think about moveing it to the clientBound packet
        if (this.x_position == x && this.y_position == y) {
            return;
        }

//        System.out.println("ClientConnectedInstance::set_position => Player <"+getUsername()+"> is moving to x: "+x+" z: "+y);

        this.x_position = x;
        this.y_position = y;

        // Publish move every 10th move
//        if (publishMoveCount++ % 10 != 0) {
        if (x != 8) {
            this.vastConnection.publishMove(x, y);
        }
//            publishMoveCount = 0;
//        }

    }

    public void set_position(int x, int y, boolean forcePublish) {
        if (this.x_position == x && this.y_position == y) {
            return;
        }

//        System.out.println("ClientConnectedInstance::set_position forcePublish => Player <"+getUsername()+"> is moving to x: "+x+" z: "+y);

        this.x_position = x;
        this.y_position = y;

        if (forcePublish) {
            this.vastConnection.publishMove(x, y);
        }
    }

    public int getXPosition() {
        return x_position;
    }
    public int getYPosition() {
        return y_position;
    }


    private Boolean permanentSubscriptionsStarted = false;
    public void startPermanentSubscriptions(double x, double z) {

        if (permanentSubscriptionsStarted) {
            return;
        }
        permanentSubscriptionsStarted = true;


        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            if (!this.migrating) {
                // if lastReceivedPacket is more than 5 seconds ago
                if (Duration.between(this.vastConnection.lastReceivedPacket, Instant.now()).compareTo(Duration.ofSeconds(3)) >= 0) {
//                    startPermanentSubscriptions(true);
//                    PacketCapture.log(
//                            this.getUsername(),
//                            message,
//                            PacketCapture.LogCategory.
//                    );
                }
            }
        }, 10, 1, TimeUnit.SECONDS);


        this.vastConnection.unsubscribe("clientBound"); // Should not have any clientBound subscriptions
//        this.vastConnection.subscribeMobilePolygon(getSquare((int)x, (int)z, (21*16 + 16*2)), "clientBound"); // Each chunk is 16x16, so 21 chunks is 21*16, and 16*2 is the extra 2 chunks on each side as a buffer
        this.vastConnection.subscribeMobile((int) x, (int) z, 80, "clientBound"); // TODO: Change to square (5*12) or something


        // Dangerous! TODO: test
//        this.vastConnection.unsubscribe(this.getUsername());
        this.vastConnection.subscribeMobile((int) x, (int) z, 10, this.getUsername());


//        this.vastConnection.subscribeMobilePolygon(getSquare((int)x, (int)z, (21*16 + 16*2)), this.getUsername()); // Dit is waar hy die chunks en specific userbound packets receive
//        this.vastConnection.subscribeMobilePolygon(getSquare((int)x, (int)z, (21*16 + 16*2)), this.getUsername()); // Dit is waar hy die chunks en specific userbound packets receive



//        this.vastConnection.subscribe(0,0,10, this.getUsername()); // This happens in setClientUsername!

//        this.vastConnection.unsubscribe("clientBound"); // Should not have any clientBound subscriptions
////        this.vastConnection.subscribeMobilePolygon(getSquare((int)x, (int)z, (21*16 + 16*2)), "clientBound"); // Each chunk is 16x16, so 21 chunks is 21*16, and 16*2 is the extra 2 chunks on each side as a buffer
//        this.vastConnection.subscribeMobile((int)x, (int)z, 80, "clientBound"); // TODO: Change to square (5*12) or something
//        this.vastConnection.subscribeMobile((int)x, (int)z, 10000, "clientBound");
    }

    public void startPermanentSubscriptions(boolean force) {
        if (force) {
            this.vastConnection.unsubscribe("clientBound"); // Should not have any clientBound subscriptions
            this.vastConnection.subscribeMobile((int) this.getXPosition(), (int) this.getYPosition(), 80, "clientBound"); // TODO: Change to square (5*12) or something
            this.vastConnection.unsubscribe(this.getUsername());
            this.vastConnection.subscribe(0, 0, 10, this.getUsername());

        } else {
            startPermanentSubscriptions(this.getXPosition(), this.getYPosition());
        }
    }

    private static ArrayList<ChunkPosition> getSquare(int x, int z, int sideLength) {
        ArrayList<ChunkPosition> square = new ArrayList<>();
        int halfLength = sideLength / 2;

        // Calculate the corner positions
        int x1 = x - halfLength;
        int x2 = x + halfLength - 1;
        int z1 = z - halfLength;
        int z2 = z + halfLength - 1;

        // Add the corner positions to the square ArrayList
        square.add(new ChunkPosition(x1, z1));
        square.add(new ChunkPosition(x1, z2));
        square.add(new ChunkPosition(x2, z1));
        square.add(new ChunkPosition(x2, z2));

        return square;
    }

    public void disconnect() {
//        clientInstances_PacketSenders.remove(this.packetSender);

        try {
            if (this.packetSender != null) {
                this.packetSender.stop();
                this.packetSender = null;
            }
        } catch (NullPointerException e) {
            System.out.println("Error stopping packetSender: " + e.getMessage());
        }

        try {
            if (this.packetHandler != null) {
                this.packetHandler.stop();
                this.packetHandler = null;
                System.out.println("PacketHandler stopped");
            }
        } catch (NullPointerException e) {
            System.out.println("Error stopping packetHandler: " + e.getMessage());
        }

        try {
            SPSPacket disconnectPacket = new SPSPacket(
                    new EstablishConnectionPacket(this.clientUsername, false),
                    this.clientUsername,
                    this.x_position,
                    this.y_position,
                    1,
                    "serverBound"
            );
            this.vastConnection.publish(disconnectPacket);
        } catch (NullPointerException e) {
            System.out.println("Error sending disconnect packet: " + e.getMessage());
        }

        try {
            if (this.vastConnection != null) {
                this.vastConnection.disconnect();
                this.vastConnection = null;
            } else {
                System.out.println("VastConnection is null");
            }
        } catch (NullPointerException e) {
            System.out.println("Error disconnecting vastConnection: " + e.getMessage());
        }

        this.session.removeListener(this.session.getListeners().get(0));
        try {
            if (this.session != null) {
                this.session.disconnect("Disconnecting from server :)");
                this.session = null;
            }
        } catch (NullPointerException e) {
            System.out.println("Error disconnecting session: " + e.getMessage());
        }

//        App.stop();
//        clientInstances_PacketSenders.remove(this);
//        App.clientInstances.remove(this);
    }

    public void setMigarting(boolean migrating) {
        this.migrating = migrating;
    }

    public boolean isMigrating() {
        return migrating;
    }

    public void setMinecraftServer(String host, int port) {
        this.currentMinecratServerHost = host;
        this.currentMinecraftServerPort = port;
    }

    public String getMinecraftServerHost() {
        return currentMinecratServerHost;
    }

    public int getMinecraftServerPort() {
        return currentMinecraftServerPort;
    }

    public void setMigratingMinecraftServer(String host, int port) {
        this.migratingMinecratServerHost = host;
        this.migratingMinecraftServerPort = port;
    }

    public String getMigratingMinecraftServerHost() {
        return migratingMinecratServerHost;
    }

    public int getMigratingMinecraftServerPort() {
        return migratingMinecraftServerPort;
    }

    // Variable to save the current time
    private long migrationStartTime = System.currentTimeMillis();

    public void migrate(String host, int port) { // Minecraft Specific

        // If migratoinStartTime is more than 1 second ago, then the migration has timed out
        if (System.currentTimeMillis() - migrationStartTime > 1000) {
            System.out.println("ClientConnectedInstance::migrate => Player <"+getUsername()+"> migration has timed out");
            this.setMigarting(false);
//            return;
        }

        if (isMigrating()) {
            System.out.println("ClientConnectedInstance::migrate => Player <"+getUsername()+"> is already migrating to a new server");
            return;
        }


        this.setMigratingMinecraftServer(host, port);

        System.out.println("ClientConnectedInstance::migrate => Migrating player <"+getUsername()+"> to new server <"+host+":"+port+">");
        // Connect to new MINECRAFT server with user, without switching client - client will switch after connection has finalised
        EstablishConnectionPacket establishConnectionPacket = new EstablishConnectionPacket(getUsername(), true, host, port);

        this.setMigarting(true);
        migrationStartTime = System.currentTimeMillis();

        packetSender.addServerboundPacket(establishConnectionPacket); // Needs to be serverBound to new server TODO add ip info to packet so that serverproxy knows that this is a for it
    }
}
