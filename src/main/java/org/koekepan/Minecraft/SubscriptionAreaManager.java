package org.koekepan.Minecraft;

import com.github.steveice10.packetlib.Client;
import org.koekepan.VAST.Connection.ClientConnectedInstance;

import java.util.*;

public class SubscriptionAreaManager {
//    private static final Map<ClientInstance, List<Position>> clientPositions = new HashMap<>();

    private static HashMap<ClientConnectedInstance, List<ChunkPosition>> positions = new HashMap<>();
    private static HashMap<ClientConnectedInstance, List<ChunkPosition>> isolatedPositions = new HashMap<>();
    private SubscriptionAreaManager() {
        // This is a private constructor
        System.out.println("SubscriptionManager: private constructor");
    }

    public static void receiveChunkPosition(ClientConnectedInstance clientInstance, ChunkPosition position) {
        List<ChunkPosition> clientPositions = positions.get(clientInstance);
        if (clientPositions == null) {
            clientPositions = new ArrayList<>();
            positions.put(clientInstance, clientPositions);
        }
        clientPositions.add(position);
    }

    public static List<ChunkPosition> updateIsolatedPositions(ClientConnectedInstance clientInstance) {
        List<ChunkPosition> clientPositions = positions.get(clientInstance);
        if (clientPositions != null && clientPositions.toArray().length == 441) {
            System.out.println("SubscriptionAreaManager::updateIsolatedPositions => clientPositions.toArray().length == 441");
            List<ChunkPosition> isolated = isolatedPositions.get(clientInstance);

            if (isolated != null) {
                isolated.clear();
            }
            isolated = getPolygonCorners(clientPositions);
            isolatedPositions.put(clientInstance, isolated);

            return isolated;
        }

        return null;
    }

    public static void removeChunkPosition(ClientConnectedInstance clientInstance, int x, int z) {

        List<ChunkPosition> clientPositions = positions.get(clientInstance);

        boolean removed = false;
        while (!removed) {
            synchronized (clientPositions) {
                Iterator<ChunkPosition> iterator = clientPositions.iterator();

//				tempcounter += 1;
                while (iterator.hasNext()) {
                    ChunkPosition position = iterator.next();
                    // Remove the position if it matches the given x and z
                    if (position.getX() == x && position.getZ() == z) {
                        iterator.remove();
                        removed = true;
                        continue;
                    }
                }
            }
        }
//            Logger.log(this, Logger.Level.DEBUG, new String[]{"playerSubscription","VAST"},"length of positions: " + positions.toArray().length);

//			updateIsolatedPositions();
    }

//    public static void removeChunkPosition(ClientConnectedInstance clientInstance, int x, int z) {
//        List<ChunkPosition> clientPositions = positions.get(clientInstance); // Ensure this is thread-safe
//
//        // Synchronize on the clientPositions list to prevent concurrent modifications
//        synchronized (clientPositions) {
//            Iterator<ChunkPosition> iterator = clientPositions.iterator();
//            while (iterator.hasNext()) {
//                ChunkPosition position = iterator.next();
//                if (position.getX() == x && position.getZ() == z) {
//                    iterator.remove();
//                    // Once the position is removed, break out of the loop
//                    break;
//                }
//            }
//        }
//        // Additional logic here if needed
//    }

    private static List<ChunkPosition> getPolygonCorners(List<ChunkPosition> positions) {
        synchronized (positions) {
            Set<ChunkPosition> corners = new HashSet<>();
            // Helper set for faster lookup
            Set<ChunkPosition> positionSet = new HashSet<>(positions); // Protected against concurrent modification
            // Create a copy of the positions list for safe iteration
            List<ChunkPosition> safePositions = new ArrayList<>(positions);

            for (ChunkPosition chunk : safePositions) {
                // Check each side of the chunk for a neighboring chunk
                boolean hasLeft = positionSet.contains(new ChunkPosition(chunk.getX() - 16, chunk.getZ()));
                boolean hasRight = positionSet.contains(new ChunkPosition(chunk.getX() + 16, chunk.getZ()));
                boolean hasTop = positionSet.contains(new ChunkPosition(chunk.getX(), chunk.getZ() - 16));
                boolean hasBottom = positionSet.contains(new ChunkPosition(chunk.getX(), chunk.getZ() + 16));

                // If there's no chunk on the left, add the left corners
                if (!hasLeft) {
                    corners.add(new ChunkPosition(chunk.getX(), chunk.getZ()));
                    corners.add(new ChunkPosition(chunk.getX(), chunk.getZ() + 15));
                }

                // If there's no chunk on the right, add the right corners
                if (!hasRight) {
                    corners.add(new ChunkPosition(chunk.getX() + 15, chunk.getZ()));
                    corners.add(new ChunkPosition(chunk.getX() + 15, chunk.getZ() + 15));
                }

                // If there's no chunk on the top, add the top corners
                if (!hasTop) {
                    corners.add(new ChunkPosition(chunk.getX(), chunk.getZ()));
                    corners.add(new ChunkPosition(chunk.getX() + 15, chunk.getZ()));
                }

                // If there's no chunk on the bottom, add the bottom corners
                if (!hasBottom) {
                    corners.add(new ChunkPosition(chunk.getX(), chunk.getZ() + 15));
                    corners.add(new ChunkPosition(chunk.getX() + 15, chunk.getZ() + 15));
                }
            }

            // Return the list of unique corners
            return new ArrayList<>(corners);
        }
    }



}
