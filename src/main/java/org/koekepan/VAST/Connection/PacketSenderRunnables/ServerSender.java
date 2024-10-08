package org.koekepan.VAST.Connection.PacketSenderRunnables;

import com.github.steveice10.packetlib.Session;
import org.koekepan.Performance.PacketCapture;
import org.koekepan.VAST.Connection.PacketSender;
import org.koekepan.VAST.Connection.VastConnection;
import org.koekepan.VAST.Packet.PacketHandler;
import org.koekepan.VAST.Packet.PacketWrapper;
import org.koekepan.VAST.Packet.SPSPacket;

import static org.koekepan.VAST.Connection.ClientConnectedInstance.clientInstances_PacketSenders;

public class ServerSender implements Runnable{

    private PacketSender packetSender;
    private VastConnection vastConnection;

    public ServerSender(PacketSender packetSender, VastConnection vastConnection) {
        this.packetSender = packetSender;
        this.vastConnection = vastConnection;
    }

    private int queueNumberServerbound = 0;

    public int getQueueNumberServerbound() {
        return queueNumberServerbound;
    }

    //    @Override
//    public void run() {
//        //        int lastCheckedQueueNumber = -1; // Last checked queue number for comparison
//        long timeAdded = System.currentTimeMillis();
//        try {
//            while (!packetSender.serverboundPacketQueue.isEmpty()) {
////                System.out.println("PacketSender.run: clientBoundQueue: " + packetSender.clientboundPacketQueue.size() + " serverBoundQueue: " + packetSender.serverboundPacketQueue.size());
////                System.out.println("PacketSender.run: queueNumberServerbound: " + queueNumberServerbound + " queueNumberServerboundLast: " + packetSender.queueNumberServerboundLast);
//
////                System.out.println("PacketSender.run: packetSender.serverboundPacketQueue.containsKey(queueNumberServerbound): " + packetSender.serverboundPacketQueue.containsKey(queueNumberServerbound) + " queueNumberServerbound: " + queueNumberServerbound + " packetSender.queueNumberServerboundLast: " + packetSender.queueNumberServerboundLast);
//                if (packetSender.serverboundPacketQueue.containsKey(queueNumberServerbound)) {
//                    final PacketWrapper wrapper = PacketWrapper.getPacketWrapperByQueueNumber(packetSender.serverboundPacketQueue, queueNumberServerbound);
//
////                    System.out.println("PacketSender.run: (serverbound) Wrapper is: " + wrapper.getPacket().getClass().getSimpleName() + " and isProcessed: " + wrapper.isProcessed + " and VastConnection is null: " + (clientInstances_PacketSenders.get(packetSender).getVastConnection() == null));
//                    if ((wrapper != null) && (wrapper.isProcessed) && (clientInstances_PacketSenders.get(packetSender).getVastConnection() != null)) {
//                        clientInstances_PacketSenders.get(packetSender).getVastConnection().publish(wrapper.getSPSPacket());
//                        packetSender.removePacket(wrapper.getPacket());
//                        timeAdded = System.currentTimeMillis(); // Reset time after sending a packet
//                        queueNumberServerbound++;
//                    }
//                }
//
//                // Handle timeout for both queues
//                long currentTime = System.currentTimeMillis();
//                if (currentTime - timeAdded > 50) { // TODO: Change back to 100 when problem found (could be 50)
////                    System.out.println("PacketSender.run: <TIMED OUT> difference: " + (currentTime - timeAdded) + " processClientbound: " + processClientbound + " clientSession: " + (clientSession != null));
////                    System.out.println("PacketSender.run: <TIMED OUT> Amount of packets in clientbound queue: " + clientboundPacketQueue.size() + " and serverbound queue: " + serverboundPacketQueue.size());
//                    if (packetSender.serverboundPacketQueue.containsKey(queueNumberServerbound)) {
//                        final PacketWrapper wrapper = PacketWrapper.getPacketWrapperByQueueNumber(packetSender.serverboundPacketQueue, queueNumberServerbound);
//                        System.out.println("PacketSender.run: <TIMED OUT> (serverbound) Wrapper is: " + wrapper.getPacket().getClass().getSimpleName() + " and isProcessed: " + wrapper.isProcessed + " and VastConnection is null: " + (clientInstances_PacketSenders.get(packetSender).getVastConnection() == null));
//
//                        packetSender.removePacket(packetSender.serverboundPacketQueue.get(queueNumberServerbound).getPacket());
//                        queueNumberServerbound++;
//                    }
//                    timeAdded = currentTime; // Reset time after handling timeouts
//                }
//
//                if (!packetSender.serverboundPacketQueue.containsKey(queueNumberServerbound) && queueNumberServerbound <= packetSender.queueNumberServerboundLast) {
//                    while (!packetSender.serverboundPacketQueue.containsKey(queueNumberServerbound)) {
//                        queueNumberServerbound++;
//                        // Check if queueNumberServerbound has reached or exceeded the last queue number
//                        if (queueNumberServerbound > packetSender.queueNumberServerboundLast) {
//                            break; // Exit the loop if we have reached the end of the queue
//                        }
//                    }
//                }
//
//            }
//        } catch (Exception e) {
//            System.out.println("ServerSender.run: Exception: " + e.getMessage());
//            // Log the exception
//        }
//    }

    @Override
    public void run() {
        long timeAdded = System.currentTimeMillis();
        try {
//            System.out.println(packetSender.serverboundPacketQueue.isEmpty());
            while (!packetSender.serverboundPacketQueue.isEmpty()) {
                boolean dosleep = true;
//                Thread.sleep(5);
//            while (true) {
//            System.out.println("Test");
                try {
//                    PacketCapture.log("ServerSender.run: serverboundPacketQueue.size() = " + packetSender.serverboundPacketQueue.size() + " and queueNumberServerbound = " + queueNumberServerbound + " and serverboundPacketQueueContainsKey = " + packetSender.serverboundPacketQueue.containsKey(queueNumberServerbound) + " and serverboundPacketQueueContainsKey = " + packetSender.serverboundPacketQueue.containsKey(queueNumberServerbound) + " serverboundpacketqueuelast = " + packetSender.queueNumberServerboundLast, PacketCapture.LogCategory.SERVERBOUND_QUEUE);
                    if (packetSender.serverboundPacketQueue.containsKey(queueNumberServerbound)) {
                        PacketWrapper wrapper = null;
                        try {
                            wrapper = PacketWrapper.getPacketWrapperByQueueNumber(packetSender.serverboundPacketQueue, queueNumberServerbound);
                        } catch (Exception e) {
                            System.out.println("Error getting PacketWrapper: " + e.getMessage());
                        }

//                        assert wrapper != null;
//                        PacketCapture.log(wrapper.getPacket().getClass().getSimpleName() + "_" + PacketWrapper.get_unique_id(wrapper.getPacket()) + " " + wrapper.isProcessed, PacketCapture.LogCategory.PROCESSING_START);

                        if (wrapper != null && wrapper.isProcessed && clientInstances_PacketSenders.get(packetSender).getVastConnection() != null) {
                            SPSPacket spsPacket2 = null;
                            try {
                                spsPacket2 = wrapper.getSPSPacket();
                            } catch (Exception e) {
                                System.out.println("Error getting SPSPacket: " + e.getMessage());
                            }

                            try {
                                vastConnection.publish(spsPacket2);
                            } catch (Exception e) {
                                System.out.println("Error publishing packet: <" + wrapper.getPacket().getClass().getSimpleName() + ">: " + e.getMessage());
                            }
                            try {
                                packetSender.removePacket(wrapper.getPacket());
                            } catch (Exception e) {
                                System.out.println("Error removing packet: " + e.getMessage());
                            }
                            try {
                                timeAdded = System.currentTimeMillis(); // Reset time after sending a packet
                                queueNumberServerbound++;
                                dosleep = false;
                            } catch (Exception e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                        }
                    }

                    // Handle timeout for both queues
                    long currentTime = System.currentTimeMillis();
                    if ((currentTime - timeAdded > 250)) { // TODO: Change back to 100 when problem found (could be 50)
                        if (packetSender.serverboundPacketQueue.containsKey(queueNumberServerbound)) {
                            PacketWrapper wrapper = null;
                            try {
                                wrapper = PacketWrapper.getPacketWrapperByQueueNumber(packetSender.serverboundPacketQueue, queueNumberServerbound);
                            } catch (Exception e) {
                                System.out.println("Error getting PacketWrapper: " + e.getMessage());
                            }

                            if (wrapper != null) {
                                try {
                                    timeAdded = System.currentTimeMillis(); // Reset time after handling timeouts
                                    System.out.println("ServerSender.run: <TIMED OUT> (serverbound) Wrapper is: " + wrapper.getPacket().getClass().getSimpleName() + " and isProcessed: " + wrapper.isProcessed + " " + wrapper.getPacket().toString());
                                    packetSender.removePacket(wrapper.getPacket());
                                    queueNumberServerbound++;
                                    dosleep = false;
                                    break;
//                                    timeAdded = System.currentTimeMillis(); // Reset time after handling timeouts
                                } catch (Exception e) {
                                    System.out.println("Error removing packet: " + e.getMessage());
                                }
                            } else {
                                System.out.println("ServerSender.run: <TIMED OUT> (serverbound) Wrapper is null");
                                queueNumberServerbound++;
                                dosleep = false;
                                timeAdded = currentTime;
                                break;
//                                timeAdded = currentTime; // Reset time after handling timeouts
                            }
                        }
                    }

                    if (queueNumberServerbound < packetSender.queueNumberServerboundLast && !packetSender.serverboundPacketQueue.containsKey(queueNumberServerbound)) {
//                        while (!packetSender.serverboundPacketQueue.containsKey(queueNumberServerbound)) {
//                            queueNumberServerbound++;
//                            timeAdded = currentTime; // Reset time after handling timeouts
//                             Check if queueNumberServerbound has reached or exceeded the last queue number
//                            if (queueNumberServerbound > packetSender.queueNumberServerboundLast) {
//                                break; // Exit the loop if we have reached the end of the queue
//                            }
//                        }

//                        while (queueNumberServerbound < packetSender.queueNumberServerboundLast && !packetSender.serverboundPacketQueue.containsKey(queueNumberServerbound)) {
//                            System.out.println("Stuck");
//                        System.out.println("Stuck in loop: User: " + clientInstances_PacketSenders.get(this.packetSender).getUsername() + " queueNumberClientbound: " + queueNumberClientbound + " queueNumberClientboundLast: " + queueNumberClientboundLast);
                            queueNumberServerbound++;
                            dosleep = false;

                            timeAdded = currentTime;
//                        }
//                    if (!packetSender.clientboundPacketQueue.containsKey(queueNumberClientbound)) {
//                        queueNumberClientbound++;
//                        break;
//                    }
//                        System.out.println("tewst");
//                    Thread.sleep(1000);
//                        break;

                    }

                } catch (Exception e) {
                    System.out.println("Error in main loop: " + e.getMessage());
                }

                if (dosleep) {
                    Thread.sleep(1);
                }
            }
        } catch (Exception e) {
            System.out.println("ServerSender.run: Exception: " + e.getMessage());
        }
    }

}
