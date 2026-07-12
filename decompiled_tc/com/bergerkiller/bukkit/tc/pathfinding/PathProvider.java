/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.BlockLocation
 *  com.bergerkiller.bukkit.common.MessageBuilder
 *  com.bergerkiller.bukkit.common.Task
 *  com.bergerkiller.bukkit.common.component.LibraryComponent
 *  com.bergerkiller.bukkit.common.config.CompressedDataReader
 *  com.bergerkiller.bukkit.common.config.CompressedDataWriter
 *  com.bergerkiller.bukkit.common.utils.StringUtil
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.pathfinding;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.config.CompressedDataReader;
import com.bergerkiller.bukkit.common.config.CompressedDataWriter;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.components.RailJunction;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.pathfinding.PathConnection;
import com.bergerkiller.bukkit.tc.pathfinding.PathNode;
import com.bergerkiller.bukkit.tc.pathfinding.PathNodeSnapshot;
import com.bergerkiller.bukkit.tc.pathfinding.PathPredictEvent;
import com.bergerkiller.bukkit.tc.pathfinding.PathRoutingHandler;
import com.bergerkiller.bukkit.tc.pathfinding.PathWorld;
import com.bergerkiller.bukkit.tc.pathfinding.SignRoutingEvent;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.TrackWalkingPoint;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PathProvider
extends Task
implements LibraryComponent,
TrainCarts.Provider {
    private static final String SWITCHER_NAME_FALLBACK = "::traincarts::switchable::";
    public static final int DEFAULT_MAX_PROCESSING_PER_TICK = 30;
    public static boolean DEBUG_MODE = false;
    private final String fileName;
    private final Map<String, PathWorld> worlds = new HashMap<String, PathWorld>();
    private final List<PathRoutingHandler> handlers = new ArrayList<PathRoutingHandler>();
    private final Queue<BlockLocation> pendingDiscovery = new LinkedList<BlockLocation>();
    private final Set<PathNode> pendingNodes = new LinkedHashSet<PathNode>();
    private final Queue<PathFindOperation> pendingOperations = new LinkedList<PathFindOperation>();
    private final Set<PathNode> scheduledNodesSinceIdle = new HashSet<PathNode>();
    private final Set<PathNodeSnapshot> pathNodesBeforeDiscovery = new HashSet<PathNodeSnapshot>();
    private final Set<CommandSender> sendersToNotifyOfCompletion = new HashSet<CommandSender>();
    private boolean hasChanges = false;
    private int maxProcessingPerTick = 30;

    public PathProvider(TrainCarts plugin, String fileName) {
        super((JavaPlugin)plugin);
        this.fileName = fileName;
        this.registerRoutingHandler(new PathRoutingHandler(){

            @Override
            public void process(PathRoutingHandler.PathRouteEvent event) {
                boolean switchable = false;
                List<String> destinationNames = Collections.emptyList();
                for (RailLookup.TrackedSign trackedSign : event.railPiece().signs()) {
                    SignAction action = trackedSign.getAction();
                    if (trackedSign.isRemoved() || action == null) continue;
                    SignRoutingEvent signEvent = new SignRoutingEvent(trackedSign);
                    signEvent.resetToInitialState(event.railState(), event.railPath(), event.currentDistance());
                    signEvent.overrideCartEnterState(event.railState());
                    action.route(signEvent);
                    if (signEvent.isBlocked()) {
                        event.setBlocked();
                        return;
                    }
                    if (signEvent.isRouteSwitchable() || !signEvent.getDestinationNames().isEmpty()) {
                        switchable |= signEvent.isRouteSwitchable();
                        if (!signEvent.getDestinationNames().isEmpty()) {
                            if (destinationNames.isEmpty()) {
                                destinationNames = new ArrayList();
                            }
                            destinationNames.addAll(signEvent.getDestinationNames());
                        }
                        event.setSwitchedPosition(null);
                        continue;
                    }
                    if (!signEvent.hasSwitchedPosition()) continue;
                    event.setSwitchedPosition(signEvent.getSwitchedPosition());
                }
                if (switchable || !destinationNames.isEmpty()) {
                    PathNode newFoundNode = event.createNode();
                    if (switchable) {
                        newFoundNode.addSwitcher();
                    }
                    destinationNames.forEach(newFoundNode::addName);
                }
            }

            @Override
            public void predict(PathPredictEvent event) {
                for (RailLookup.TrackedSign trackedSign : event.railPiece().signs()) {
                    SignAction action = trackedSign.getAction();
                    if (trackedSign.isRemoved() || action == null || !action.hasPathFindingPrediction()) continue;
                    SignActionEvent signEvent = trackedSign.createEvent(SignActionType.GROUP_ENTER);
                    signEvent.setMember(event.member());
                    signEvent.overrideCartEnterState(event.railState());
                    action.predictPathFinding(signEvent, event);
                }
            }
        });
    }

    @Override
    public TrainCarts getTrainCarts() {
        return (TrainCarts)super.getPlugin();
    }

    public void registerRoutingHandler(PathRoutingHandler handler) {
        this.handlers.add(handler);
    }

    public void unregisterRoutingHandler(PathRoutingHandler handler) {
        this.handlers.remove(handler);
    }

    public void predictRoutingHandler(PathPredictEvent event) {
        this.handlers.forEach(handler -> handler.predict(event));
    }

    public void setMaxProcessingPerTick(int durationMillis) {
        this.maxProcessingPerTick = durationMillis;
    }

    public int getNumPendingNodes() {
        return this.pendingDiscovery.size() + this.pendingNodes.size();
    }

    public int getNumPendingOperations() {
        return this.pendingOperations.size();
    }

    public void notifyOfCompletion(CommandSender sender) {
        this.sendersToNotifyOfCompletion.add(sender);
    }

    public void enable() {
        this.start(1L, 1L);
        new CompressedDataReader(this.fileName){

            public void read(DataInputStream stream) throws IOException {
                PathProvider.this.worlds.clear();
                int count = stream.readInt();
                PathNode[] parr = new PathNode[count];
                for (int i = 0; i < count; ++i) {
                    String name = stream.readUTF();
                    BlockLocation loc = new BlockLocation(stream.readUTF(), stream.readInt(), stream.readInt(), stream.readInt());
                    parr[i] = PathProvider.this.getWorld(loc.world).addNode(loc);
                    if (name.isEmpty()) {
                        parr[i].addSwitcher();
                        continue;
                    }
                    for (String name_part : name.split("\n")) {
                        if (name_part.equals(PathProvider.SWITCHER_NAME_FALLBACK)) {
                            parr[i].addSwitcher();
                            continue;
                        }
                        parr[i].addName(name_part);
                    }
                }
                for (PathNode node : parr) {
                    int ncount = stream.readInt();
                    for (int i = 0; i < ncount; ++i) {
                        node.addNeighbourFast(new PathConnection(parr[stream.readInt()], stream));
                    }
                }
                PathProvider.this.pendingNodes.clear();
                PathProvider.this.scheduledNodesSinceIdle.clear();
            }
        }.read();
        this.hasChanges = false;
        if (TCConfig.rerouteOnStartup) {
            this.reroute();
        }
    }

    public void disable() {
        this.stop();
        for (PathWorld world : this.getWorlds()) {
            world.clearAll();
        }
    }

    public void save(boolean autosave, String filename) {
        if (autosave && !this.hasChanges) {
            return;
        }
        new CompressedDataWriter(filename){

            public void write(DataOutputStream stream) throws IOException {
                int totalNodeCount = 0;
                for (PathWorld world : PathProvider.this.getWorlds()) {
                    totalNodeCount += world.getNodes().size();
                }
                stream.writeInt(totalNodeCount);
                int i = 0;
                for (PathWorld world : PathProvider.this.getWorlds()) {
                    for (PathNode node : world.getNodes()) {
                        node.index = i;
                        if (node.containsSwitcher()) {
                            if (node.getNames().isEmpty()) {
                                stream.writeUTF("");
                            } else {
                                stream.writeUTF("::traincarts::switchable::\n" + StringUtil.join((String)"\n", node.getNames()));
                            }
                        } else {
                            stream.writeUTF(StringUtil.join((String)"\n", node.getNames()));
                        }
                        stream.writeUTF(node.location.world);
                        stream.writeInt(node.location.x);
                        stream.writeInt(node.location.y);
                        stream.writeInt(node.location.z);
                        ++i;
                    }
                }
                for (PathWorld world : PathProvider.this.getWorlds()) {
                    for (PathNode node : world.getNodes()) {
                        stream.writeInt(node.getNeighbours().size());
                        for (PathConnection conn : node.getNeighbours()) {
                            conn.writeTo(stream);
                        }
                    }
                }
            }
        }.write();
        this.hasChanges = false;
    }

    public Collection<PathWorld> getWorlds() {
        return this.worlds.values();
    }

    public PathWorld getWorld(String worldName) {
        return this.worlds.computeIfAbsent(worldName, name -> new PathWorld(this, (String)name));
    }

    public PathWorld getWorld(World world) {
        return this.getWorld(world.getName());
    }

    public boolean nodeExistsOnAnyWorld(String name) {
        for (PathWorld world : this.getWorlds()) {
            if (world.getNodeByName(name) == null) continue;
            return true;
        }
        return false;
    }

    public PathNode tryFindNodeAgain(PathNodeSnapshot snapshot) {
        PathWorld world = this.getWorld(snapshot.getWorldName());
        if (world != null) {
            return world.tryFindNodeAgain(snapshot);
        }
        return null;
    }

    public void clearAll() {
        for (PathWorld world : this.getWorlds()) {
            world.clearAll();
        }
    }

    public void reroute() {
        for (PathWorld world : this.getWorlds()) {
            world.rerouteAll();
        }
    }

    public void rerouteFrom(List<String> destinationNames) {
        for (PathWorld world : this.getWorlds()) {
            world.rerouteFrom(destinationNames);
        }
    }

    public void stopRouting() {
        this.pendingDiscovery.clear();
        this.pendingNodes.clear();
        this.pendingOperations.clear();
        this.scheduledNodesSinceIdle.clear();
    }

    protected void markChanged() {
        this.hasChanges = true;
    }

    @Deprecated
    public static void schedule(PathNode startNode) {
        TrainCarts.plugin.getPathProvider().scheduleNode(startNode);
    }

    public void scheduleNode(PathNode startNode) {
        this.pendingNodes.add(startNode);
        this.scheduledNodesSinceIdle.add(startNode);
    }

    public void scheduleNodeIfNotRecentlyRouted(PathNode startNode) {
        if (this.scheduledNodesSinceIdle.add(startNode)) {
            this.pendingNodes.add(startNode);
        }
    }

    @Deprecated
    public static void discover(BlockLocation railLocation) {
        TrainCarts.plugin.getPathProvider().discoverFromRail(railLocation);
    }

    public void discoverFromRail(BlockLocation railLocation) {
        PathNode atRail;
        PathWorld world = this.getWorld(railLocation.world);
        if (world != null && (atRail = world.getNodeAtRail(railLocation)) != null) {
            this.pathNodesBeforeDiscovery.add(atRail.getSnapshot());
        }
        this.pendingDiscovery.add(railLocation);
    }

    public void discoverFromNode(PathNode node) {
        this.pathNodesBeforeDiscovery.add(node.getSnapshot());
        this.discoverFromRail(node.location);
    }

    public boolean isProcessing() {
        return !this.pendingDiscovery.isEmpty() || !this.pendingOperations.isEmpty() || !this.pendingNodes.isEmpty();
    }

    public Task stop() {
        this.addPendingNodes();
        if (!this.pendingOperations.isEmpty()) {
            this.getTrainCarts().log(Level.INFO, "Performing " + this.pendingOperations.size() + " pending path finding operations (can take a while)...");
            while (!this.pendingOperations.isEmpty()) {
                PathFindOperation operation = this.pendingOperations.poll();
                while (operation.next()) {
                }
            }
        }
        return super.stop();
    }

    public void run() {
        if (this.pendingOperations.isEmpty() && !this.pendingDiscovery.isEmpty()) {
            this.addNewlyDiscovered();
        }
        if (this.pendingOperations.isEmpty()) {
            this.addPendingNodes();
        }
        if (this.pendingOperations.isEmpty()) {
            this.scheduledNodesSinceIdle.clear();
            if (!this.sendersToNotifyOfCompletion.isEmpty()) {
                boolean hasMore;
                int maxUpdateMessages = 10;
                List updateMessages = this.pathNodesBeforeDiscovery.stream().sorted().map(snapshot -> snapshot.getUpdateMessage(this.tryFindNodeAgain((PathNodeSnapshot)snapshot))).filter(Objects::nonNull).limit(11L).collect(Collectors.toList());
                boolean bl = hasMore = updateMessages.size() > 10;
                if (hasMore) {
                    updateMessages = updateMessages.subList(0, 10);
                }
                this.pathNodesBeforeDiscovery.clear();
                ArrayList<CommandSender> senders = new ArrayList<CommandSender>(this.sendersToNotifyOfCompletion);
                this.sendersToNotifyOfCompletion.clear();
                for (CommandSender sender : senders) {
                    if (!(sender instanceof ConsoleCommandSender) && !(sender instanceof Player) || sender instanceof Player && !((Player)sender).isValid()) continue;
                    for (MessageBuilder message : updateMessages) {
                        message.send(sender);
                    }
                    if (hasMore) {
                        sender.sendMessage(ChatColor.YELLOW + "...and more changes");
                    }
                    sender.sendMessage(ChatColor.GREEN + "Train rerouting completed!");
                }
            }
            return;
        }
        long startTime = System.currentTimeMillis();
        while (!this.pendingOperations.isEmpty()) {
            PathFindOperation operation = this.pendingOperations.peek();
            boolean done = false;
            if (DEBUG_MODE) {
                this.getTrainCarts().log(Level.INFO, "DISCOVERING EVERYTHING FROM " + operation.startNode.getDisplayName() + " INTO " + operation.getJunctionName());
            }
            while (!(done = operation.next()) && System.currentTimeMillis() - startTime <= (long)this.maxProcessingPerTick) {
            }
            if (!done) break;
            this.pendingOperations.poll();
        }
        RailLookup.forceRecalculation();
    }

    private void addNewlyDiscovered() {
        BlockLocation railLocation;
        long startTime = System.currentTimeMillis();
        while ((railLocation = this.pendingDiscovery.poll()) != null) {
            RailType railType;
            Block railBlock;
            if (PathNode.get(railLocation) == null && (railBlock = railLocation.getBlock()) != null && (railType = RailType.getType(railBlock)) != RailType.NONE) {
                RailState initialState = RailState.getSpawnState(RailPiece.create(railType, railBlock));
                PathRoutingHandler.PathRouteEvent routeEvent = new PathRoutingHandler.PathRouteEvent(this, initialState.railWorld());
                routeEvent.resetToInitialState(initialState, initialState.loadRailLogic().getPath(), 0.0);
                for (PathRoutingHandler handler : this.handlers) {
                    handler.process(routeEvent);
                }
            }
            if (System.currentTimeMillis() - startTime <= (long)this.maxProcessingPerTick) continue;
        }
    }

    private void addPendingNodes() {
        if (!this.pendingNodes.isEmpty()) {
            LinkedHashSet<PathNode> newPending = new LinkedHashSet<PathNode>(this.pendingNodes);
            for (PathNode node : newPending) {
                Block startRail = node.location.getBlock();
                RailType startType = RailType.getType(startRail);
                if (startType == RailType.NONE) continue;
                if (node.containsSwitcher()) {
                    if (DEBUG_MODE) {
                        this.getTrainCarts().log(Level.INFO, "NODE " + node.getDisplayName() + " CONTAINS A SWITCHER, BRANCHING OFF");
                    }
                    for (RailJunction junc : startType.getJunctions(startRail)) {
                        RailState state = startType.takeJunction(startRail, junc);
                        if (state == null) continue;
                        this.scheduleNode(node, state, junc);
                    }
                    continue;
                }
                RailState state1 = new RailState();
                state1.setRailPiece(RailPiece.create(startType, startRail));
                state1.position().setLocation(startType.getSpawnLocation(startRail, BlockFace.NORTH));
                if (!RailType.loadRailInformation(state1)) continue;
                state1.loadRailLogic().getPath().snap(state1.position(), state1.railBlock());
                Block railBlock = state1.railBlock();
                List<RailJunction> junctions = state1.railPiece().getJunctions();
                if (junctions.isEmpty()) continue;
                RailState state2 = state1.clone();
                state2.position().invertMotion();
                state2.initEnterDirection();
                state1.loadRailLogic().getPath().move(state1, Double.MAX_VALUE);
                state2.loadRailLogic().getPath().move(state2, Double.MAX_VALUE);
                this.scheduleNode(node, state1, PathProvider.findBestJunction(junctions, railBlock, state1.position()));
                this.scheduleNode(node, state2, PathProvider.findBestJunction(junctions, railBlock, state2.position()));
            }
            this.pendingNodes.removeAll(newPending);
        }
    }

    private static RailJunction findBestJunction(List<RailJunction> junctions, Block railBlock, RailPath.Position position) {
        if (junctions.isEmpty()) {
            throw new IllegalArgumentException("Junctions list is empty");
        }
        RailJunction best = null;
        double bestDistanceSq = Double.MAX_VALUE;
        for (RailJunction junction : junctions) {
            double dist_sq = junction.position().distanceSquaredAtRail(railBlock, position);
            if (!(dist_sq < bestDistanceSq)) continue;
            bestDistanceSq = dist_sq;
            best = junction;
        }
        return best;
    }

    private void scheduleNode(PathNode node, RailState state, RailJunction junction) {
        if (!state.railPiece().offlineWorld().isLoaded()) {
            return;
        }
        try {
            this.pendingOperations.offer(new PathFindOperation(this, node, state, junction));
        }
        catch (Throwable t) {
            this.getTrainCarts().getLogger().log(Level.SEVERE, "Failed to schedule path finding operation for node at " + node.location, t);
        }
    }

    public void handleRouting(PathRoutingHandler.PathRouteEvent routeEvent) {
        for (PathRoutingHandler handler : this.handlers) {
            handler.process(routeEvent);
        }
    }

    public PathRoutingHandler.PathRouteEvent handleRouting(RailState railState, RailPath railPath, double currentDistance) {
        PathRoutingHandler.PathRouteEvent routeEvent = new PathRoutingHandler.PathRouteEvent(this, railState.railWorld());
        routeEvent.resetToInitialState(railState, railPath, currentDistance);
        this.handleRouting(routeEvent);
        return routeEvent;
    }

    private static class PathFindOperation {
        private final PathProvider provider;
        private final World world;
        private final TrackWalkingPoint p;
        private final PathNode startNode;
        private final String junctionName;

        public PathFindOperation(final PathProvider provider, final PathNode startNode, RailState state, RailJunction junction) {
            this.provider = provider;
            this.world = state.railWorld();
            this.junctionName = junction.name();
            this.startNode = startNode;
            this.p = new TrackWalkingPoint(state);
            this.p.setNavigator(new TrackWalkingPoint.Navigator<PathRoutingHandler.PathRouteEvent>(){
                final /* synthetic */ PathFindOperation this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public void navigate(PathRoutingHandler.PathRouteEvent event) {
                    for (PathRoutingHandler handler : event.provider().handlers) {
                        handler.process(event);
                    }
                    PathNode foundNode = event.getLastSetNode();
                    if (foundNode != null && !startNode.location.equals((Object)foundNode.location)) {
                        double totalDistance = ((PathFindOperation)this.this$0).p.movedTotal;
                        Location spawnPos = ((PathFindOperation)this.this$0).p.state.railType().getSpawnLocation(((PathFindOperation)this.this$0).p.state.railBlock(), ((PathFindOperation)this.this$0).p.state.position().getMotionFace());
                        startNode.addNeighbour(foundNode, totalDistance += spawnPos.distance(((PathFindOperation)this.this$0).p.state.positionLocation()), this.this$0.getJunctionName());
                        if (DEBUG_MODE) {
                            event.provider().getTrainCarts().log(Level.INFO, "MADE CONNECTION FROM " + startNode.getDisplayName() + " TO " + foundNode.getDisplayName());
                        }
                        event.abortNavigation();
                        return;
                    }
                }

                @Override
                public PathRoutingHandler.PathRouteEvent createNewEvent() {
                    return new PathRoutingHandler.PathRouteEvent(provider, this.this$0.world);
                }
            });
            this.p.setLoopFilter(true);
            Location spawnPos = state.railType().getSpawnLocation(state.railBlock(), state.position().getMotionFace());
            this.p.movedTotal += state.positionLocation().distance(spawnPos);
        }

        public String getJunctionName() {
            return this.junctionName;
        }

        public boolean next() {
            if (!this.p.state.railLookup().isValid()) {
                return true;
            }
            return !this.p.moveFull();
        }
    }
}

