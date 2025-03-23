package org.carpincho.queMeMareo.Manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class GameManager {

    private static GameManager instance;
    private List<Player> players;
    private Map<Player, ItemDisplayManager> playerEyes;
    private List<ItemDisplayManager> obstacles;
    private final Map<Player, Integer> playerLaps = new HashMap<>();
    private final Map<Player, Set<String>> playerQuadrants = new HashMap<>();
    private final Set<Player> winners = new HashSet<>();
    private final Map<Player, Integer> playerPoints = new HashMap<>();

    private final Random random;
    private final int maxObstacles = 10;
    private final double minEyeSize = 0.1;
    private final double eyeSizeDecrease = 0.05;
    private final int requiredLaps = 3;
    private final JavaPlugin plugin;
    private final PlayerManager playerManager;

    private final double eyeX = 10740;
    private final double eyeZ = -23822;
    private final double obstacleTargetY = -42;
    private final double minDistance = 5.0;


    private boolean gameActive = false;

    private GameManager(JavaPlugin plugin) {
        this.players = new ArrayList<>();
        this.playerEyes = new HashMap<>();
        this.obstacles = new ArrayList<>();
        this.random = new Random();
        this.plugin = plugin;
        this.playerManager = new PlayerManager(plugin);
    }


    private final List<Location[]> eyeAreas = Arrays.asList(
            //1 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10731, -41, -23815), new Location(Bukkit.getWorld("world"), 10747, -41, -23831)},
            //2 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10731, -41, -23797), new Location(Bukkit.getWorld("world"), 10747, -41, -23813)},
            //3 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10729, -41, -23813), new Location(Bukkit.getWorld("world"), 10713, -41, -23797)},
            //4 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10711, -41, -23813), new Location(Bukkit.getWorld("world"), 10695, -41, -23797)},
            //5 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10711, -41, -23815), new Location(Bukkit.getWorld("world"), 10695, -41, -23831)},
            //6 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10711, -41, -23833), new Location(Bukkit.getWorld("world"), 10695, -41, -23849)},
            //7 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10693, -41, -23797), new Location(Bukkit.getWorld("world"), 10678, -41, -23813)},
            //8 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10693, -41, -23795), new Location(Bukkit.getWorld("world"), 10678, -41, -23779)},
            //9 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10711, -41, -23795), new Location(Bukkit.getWorld("world"), 10695, -41, -23779)},
            //10 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10729, -41, -23795), new Location(Bukkit.getWorld("world"), 10713, -41, -23779)},
            //11 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10747, -41, -23795), new Location(Bukkit.getWorld("world"), 10731, -41, -23779)},
            //12 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10765, -41, -23795), new Location(Bukkit.getWorld("world"), 10749, -41, -23779)},
            //13 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10783, -41, -23795), new Location(Bukkit.getWorld("world"), 10767, -41, -23779)},
            //14 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10801, -41, -23795), new Location(Bukkit.getWorld("world"), 10785, -41, -23779)},
            //15 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10819, -41, -23795), new Location(Bukkit.getWorld("world"), 10803, -41, -23779)},
            //16 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10803, -41, -23761), new Location(Bukkit.getWorld("world"), 10819, -41, -23777)},
            //17 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10785, -41, -23761), new Location(Bukkit.getWorld("world"), 10801, -41, -23777)},
            //18 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10767, -41, -23777), new Location(Bukkit.getWorld("world"), 10783, -41, -23761)},
            //19 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10765, -41, -23761), new Location(Bukkit.getWorld("world"), 10749, -41, -23777)},
            //20 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10747, -41, -23777), new Location(Bukkit.getWorld("world"), 10731, -41, -23761)},
            //21 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10729, -41, -23777), new Location(Bukkit.getWorld("world"), 10713, -41, -23761)},
            //22 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10695, -41, -23761), new Location(Bukkit.getWorld("world"), 10711, -41, -23777)},
            //23 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10678, -41, -23777), new Location(Bukkit.getWorld("world"), 10693, -41, -23761)},
            //24 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10693, -41, -23743), new Location(Bukkit.getWorld("world"), 10678, -41, -23759)},
            //25 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10695, -41, -23743), new Location(Bukkit.getWorld("world"), 10711, -41, -23759)},
            //26 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10713, -41, -23743), new Location(Bukkit.getWorld("world"), 10729, -41, -23759)},
            //27 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10731, -41, -23759), new Location(Bukkit.getWorld("world"), 10747, -41, -23743)},
            //28 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10749, -41, -23743), new Location(Bukkit.getWorld("world"), 10765, -41, -23759)},
            //29 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10783, -41, -23743), new Location(Bukkit.getWorld("world"), 10767, -41, -23759)},
            //30 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10801, -41, -23759), new Location(Bukkit.getWorld("world"), 10785, -41, -23743)},
            //31 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10803, -41, -23743), new Location(Bukkit.getWorld("world"), 10819, -41, -23759)},
            //32 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10819, -41, -23741), new Location(Bukkit.getWorld("world"), 10803, -41, -23725)},
            //33 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10801, -41, -23741), new Location(Bukkit.getWorld("world"), 10785, -41, -23725)},
            //34 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10783, -41, -23741), new Location(Bukkit.getWorld("world"), 10767, -41, -23725)},
            //35 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10765, -41, -23741), new Location(Bukkit.getWorld("world"), 10749, -41, -23725)},

            //36 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10747, -41, -23741), new Location(Bukkit.getWorld("world"), 10731, -41, -23725)},

            //37 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10729, -41, -23741), new Location(Bukkit.getWorld("world"), 10713, -41, -23725)},

            //38 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10711, -41, -23741), new Location(Bukkit.getWorld("world"), 10695, -41, -23725)},

            //39 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10693, -41, -23741), new Location(Bukkit.getWorld("world"), 10678, -41, -23725)},

            //40 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10678, -41, -23833), new Location(Bukkit.getWorld("world"), 10693, -41, -23849)},

            //41 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10729, -41, -23833), new Location(Bukkit.getWorld("world"), 10713, -41, -23849)},

            //42 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10747, -41, -23833), new Location(Bukkit.getWorld("world"), 10731, -41, -23849)},

            //43 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10765, -41, -23833), new Location(Bukkit.getWorld("world"), 10749, -41, -23849)},

            //44 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10767, -41, -23833), new Location(Bukkit.getWorld("world"), 10783, -41, -23849)},

            //45 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10785, -41, -23833), new Location(Bukkit.getWorld("world"), 10801, -41, -23849)},

            //46 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10819, -41, -23833), new Location(Bukkit.getWorld("world"), 10803, -41, -23849)},

            //47 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10819, -41, -23867), new Location(Bukkit.getWorld("world"), 10803, -41, -23851)},

            //48 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10801, -41, -23867), new Location(Bukkit.getWorld("world"), 10785, -41, -23851)},

            //49 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10783, -41, -23867), new Location(Bukkit.getWorld("world"), 10767, -41, -23851)},

            //50 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10765, -41, -23867), new Location(Bukkit.getWorld("world"), 10749, -41, -23851)},

            //60 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10747, -41, -23867), new Location(Bukkit.getWorld("world"), 10731, -41, -23851)},

            //61 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10729, -41, -23867), new Location(Bukkit.getWorld("world"), 10713, -41, -23851)},

            //62 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10711, -41, -23867), new Location(Bukkit.getWorld("world"), 10695, -41, -23851)},

            //63 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10693, -41, -23867), new Location(Bukkit.getWorld("world"), 10678, -41, -23851)},

            //64 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10678, -41, -23869), new Location(Bukkit.getWorld("world"), 10693, -41, -23885)},

            //65 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10695, -41, -23869), new Location(Bukkit.getWorld("world"), 10711, -41, -23885)},

            //66 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10713, -41, -23869), new Location(Bukkit.getWorld("world"), 10729, -41, -23885)},

            //67 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10731, -41, -23869), new Location(Bukkit.getWorld("world"), 10747, -41, -23885)},

            //68 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10749, -41, -23869), new Location(Bukkit.getWorld("world"), 10765, -41, -23885)},

            //69 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10767, -41, -23869), new Location(Bukkit.getWorld("world"), 10783, -41, -23885)},

            //70 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10801, -41, -23885), new Location(Bukkit.getWorld("world"), 10785, -41, -23869)},

            //71 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10803, -41, -23885), new Location(Bukkit.getWorld("world"), 10819, -41, -23869)},

            //72 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10819, -41, -23887), new Location(Bukkit.getWorld("world"), 10803, -41, -23903)},

            //73 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10801, -41, -23903), new Location(Bukkit.getWorld("world"), 10785, -41, -23887)},

            //74 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10783, -41, -23903), new Location(Bukkit.getWorld("world"), 10767, -41, -23887)},

            //75 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10765, -41, -23903), new Location(Bukkit.getWorld("world"), 10749, -41, -23887)},

            //76 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10747, -41, -23903), new Location(Bukkit.getWorld("world"), 10731, -41, -23887)},

            //77 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10729, -41, -23903), new Location(Bukkit.getWorld("world"), 10713, -41, -23887)},

            //78 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10711, -41, -23903), new Location(Bukkit.getWorld("world"), 10695, -41, -23887)},

            //79 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10693, -41, -23887), new Location(Bukkit.getWorld("world"), 10678, -41, -23903)},

            new Location[]{new Location(Bukkit.getWorld("world"), 10819, -41, -23831), new Location(Bukkit.getWorld("world"), 10803, -41, -23815)},

            new Location[]{new Location(Bukkit.getWorld("world"), 10801, -41, -23831), new Location(Bukkit.getWorld("world"), 10785, -41, -23815)},

            new Location[]{new Location(Bukkit.getWorld("world"), 10783, -41, -23831), new Location(Bukkit.getWorld("world"), 10767, -41, -23815)},

            new Location[]{new Location(Bukkit.getWorld("world"), 10765, -41, -23831), new Location(Bukkit.getWorld("world"), 10749, -41, -23815)},

            new Location[]{new Location(Bukkit.getWorld("world"), 10729, -41, -23815), new Location(Bukkit.getWorld("world"), 10713, -41, -23831)},

            new Location[]{new Location(Bukkit.getWorld("world"), 10693, -41, -23831), new Location(Bukkit.getWorld("world"), 10678, -41, -23815)},

            new Location[]{new Location(Bukkit.getWorld("world"), 10765, -41, -23813), new Location(Bukkit.getWorld("world"), 10749, -41, -23797)},

            new Location[]{new Location(Bukkit.getWorld("world"), 10783, -41, -23813), new Location(Bukkit.getWorld("world"), 10767, -41, -23797)},

            new Location[]{new Location(Bukkit.getWorld("world"), 10801, -41, -23813), new Location(Bukkit.getWorld("world"), 10785, -41, -23797)},

            new Location[]{new Location(Bukkit.getWorld("world"), 10819, -41, -23813), new Location(Bukkit.getWorld("world"), 10803, -41, -23797)},

            new Location[]{new Location(Bukkit.getWorld("world"), 10711, -41, -23903), new Location(Bukkit.getWorld("world"), 10695, -41, -23887)}

    );

    private final List<Location[]> obstacleAreas = Arrays.asList(
            //1 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10731, -43, -23815), new Location(Bukkit.getWorld("world"), 10747, -41, -23831)},
            //2 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10731, -43, -23797), new Location(Bukkit.getWorld("world"), 10747, -41, -23813)},
            //3 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10729, -43, -23813), new Location(Bukkit.getWorld("world"), 10713, -41, -23797)},
            //4 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10711, -43, -23813), new Location(Bukkit.getWorld("world"), 10695, -41, -23797)},
            //5 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10711, -43, -23815), new Location(Bukkit.getWorld("world"), 10695, -41, -23831)},
            //6 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10711, -43, -23833), new Location(Bukkit.getWorld("world"), 10695, -41, -23849)},
            //7 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10693, -43, -23797), new Location(Bukkit.getWorld("world"), 10678, -41, -23813)},
            //8 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10693, -43, -23795), new Location(Bukkit.getWorld("world"), 10678, -41, -23779)},
            //9 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10711, -43, -23795), new Location(Bukkit.getWorld("world"), 10695, -41, -23779)},
            //10 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10729, -43, -23795), new Location(Bukkit.getWorld("world"), 10713, -41, -23779)},
            //11 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10747, -43, -23795), new Location(Bukkit.getWorld("world"), 10731, -41, -23779)},
            //12 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10765, -43, -23795), new Location(Bukkit.getWorld("world"), 10749, -41, -23779)},
            //13 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10783, -43, -23795), new Location(Bukkit.getWorld("world"), 10767, -41, -23779)},
            //14 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10801, -43, -23795), new Location(Bukkit.getWorld("world"), 10785, -41, -23779)},
            //15 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10819, -43, -23795), new Location(Bukkit.getWorld("world"), 10803, -41, -23779)},
            //16 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10803, -43, -23761), new Location(Bukkit.getWorld("world"), 10819, -41, -23777)},
            //17 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10785, -43, -23761), new Location(Bukkit.getWorld("world"), 10801, -41, -23777)},
            //18 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10767, -43, -23777), new Location(Bukkit.getWorld("world"), 10783, -41, -23761)},
            //19 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10765, -43, -23761), new Location(Bukkit.getWorld("world"), 10749, -41, -23777)},
            //20 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10747, -43, -23777), new Location(Bukkit.getWorld("world"), 10731, -41, -23761)},
            //21 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10729, -43, -23777), new Location(Bukkit.getWorld("world"), 10713, -41, -23761)},
            //22 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10695, -43, -23761), new Location(Bukkit.getWorld("world"), 10711, -41, -23777)},
            //23 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10678, -43, -23777), new Location(Bukkit.getWorld("world"), 10693, -41, -23761)},
            //24 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10693, -43, -23743), new Location(Bukkit.getWorld("world"), 10678, -41, -23759)},
            //25 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10695, -43, -23743), new Location(Bukkit.getWorld("world"), 10711, -41, -23759)},
            //26 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10713, -43, -23743), new Location(Bukkit.getWorld("world"), 10729, -41, -23759)},
            //27 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10731, -43, -23759), new Location(Bukkit.getWorld("world"), 10747, -41, -23743)},
            //28 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10749, -43, -23743), new Location(Bukkit.getWorld("world"), 10765, -41, -23759)},
            //29 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10783, -43, -23743), new Location(Bukkit.getWorld("world"), 10767, -41, -23759)},
            //30 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10801, -43, -23759), new Location(Bukkit.getWorld("world"), 10785, -41, -23743)},
            //31 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10803, -43, -23743), new Location(Bukkit.getWorld("world"), 10819, -41, -23759)},
            //32 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10819, -43, -23741), new Location(Bukkit.getWorld("world"), 10803, -41, -23725)},
            //33 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10801, -43, -23741), new Location(Bukkit.getWorld("world"), 10785, -41, -23725)},
            //34 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10783, -43, -23741), new Location(Bukkit.getWorld("world"), 10767, -41, -23725)},
            //35 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10765, -43, -23741), new Location(Bukkit.getWorld("world"), 10749, -41, -23725)},

            //36 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10747, -43, -23741), new Location(Bukkit.getWorld("world"), 10731, -41, -23725)},

            //37 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10729, -43, -23741), new Location(Bukkit.getWorld("world"), 10713, -41, -23725)},

            //38 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10711, -43, -23741), new Location(Bukkit.getWorld("world"), 10695, -41, -23725)},

            //39 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10693, -43, -23741), new Location(Bukkit.getWorld("world"), 10678, -41, -23725)},

            //40 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10678, -43, -23833), new Location(Bukkit.getWorld("world"), 10693, -41, -23849)},

            //41 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10729, -43, -23833), new Location(Bukkit.getWorld("world"), 10713, -41, -23849)},

            //42 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10747, -43, -23833), new Location(Bukkit.getWorld("world"), 10731, -41, -23849)},

            //43 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10765, -43, -23833), new Location(Bukkit.getWorld("world"), 10749, -41, -23849)},

            //44 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10767, -43, -23833), new Location(Bukkit.getWorld("world"), 10783, -41, -23849)},

            //45 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10785, -43, -23833), new Location(Bukkit.getWorld("world"), 10801, -41, -23849)},

            //46 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10819, -43, -23833), new Location(Bukkit.getWorld("world"), 10803, -41, -23849)},

            //47 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10819, -43, -23867), new Location(Bukkit.getWorld("world"), 10803, -41, -23851)},

            //48 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10801, -43, -23867), new Location(Bukkit.getWorld("world"), 10785, -41, -23851)},

            //49 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10783, -43, -23867), new Location(Bukkit.getWorld("world"), 10767, -41, -23851)},

            //50 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10765, -43, -23867), new Location(Bukkit.getWorld("world"), 10749, -41, -23851)},

            //60 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10747, -43, -23867), new Location(Bukkit.getWorld("world"), 10731, -41, -23851)},

            //61 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10729, -43, -23867), new Location(Bukkit.getWorld("world"), 10713, -41, -23851)},

            //62 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10711, -43, -23867), new Location(Bukkit.getWorld("world"), 10695, -41, -23851)},

            //63 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10693, -43, -23867), new Location(Bukkit.getWorld("world"), 10678, -41, -23851)},

            //64 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10678, -43, -23869), new Location(Bukkit.getWorld("world"), 10693, -41, -23885)},

            //65 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10695, -43, -23869), new Location(Bukkit.getWorld("world"), 10711, -41, -23885)},

            //66 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10713, -43, -23869), new Location(Bukkit.getWorld("world"), 10729, -41, -23885)},

            //67 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10731, -43, -23869), new Location(Bukkit.getWorld("world"), 10747, -41, -23885)},

            //68 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10749, -43, -23869), new Location(Bukkit.getWorld("world"), 10765, -41, -23885)},

            //69 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10767, -43, -23869), new Location(Bukkit.getWorld("world"), 10783, -41, -23885)},

            //70 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10801, -43, -23885), new Location(Bukkit.getWorld("world"), 10785, -41, -23869)},

            //71 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10803, -43, -23885), new Location(Bukkit.getWorld("world"), 10819, -41, -23869)},

            //72 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10819, -43, -23887), new Location(Bukkit.getWorld("world"), 10803, -41, -23903)},

            //73 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10801, -43, -23903), new Location(Bukkit.getWorld("world"), 10785, -41, -23887)},

            //74 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10783, -43, -23903), new Location(Bukkit.getWorld("world"), 10767, -41, -23887)},

            //75 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10765, -43, -23903), new Location(Bukkit.getWorld("world"), 10749, -41, -23887)},

            //76 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10747, -43, -23903), new Location(Bukkit.getWorld("world"), 10731, -41, -23887)},

            //77 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10729, -43, -23903), new Location(Bukkit.getWorld("world"), 10713, -41, -23887)},

            //78 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10711, -43, -23903), new Location(Bukkit.getWorld("world"), 10695, -41, -23887)},

            //79 listo
            new Location[]{new Location(Bukkit.getWorld("world"), 10693, -43, -23887), new Location(Bukkit.getWorld("world"), 10678, -41, -23903)},

            new Location[]{new Location(Bukkit.getWorld("world"), 10819, -43, -23831), new Location(Bukkit.getWorld("world"), 10803, -41, -23815)},

            new Location[]{new Location(Bukkit.getWorld("world"), 10801, -43, -23831), new Location(Bukkit.getWorld("world"), 10785, -41, -23815)},

            new Location[]{new Location(Bukkit.getWorld("world"), 10783, -43, -23831), new Location(Bukkit.getWorld("world"), 10767, -41, -23815)},

            new Location[]{new Location(Bukkit.getWorld("world"), 10765, -43, -23831), new Location(Bukkit.getWorld("world"), 10749, -41, -23815)},

            new Location[]{new Location(Bukkit.getWorld("world"), 10729, -43, -23815), new Location(Bukkit.getWorld("world"), 10713, -41, -23831)},

            new Location[]{new Location(Bukkit.getWorld("world"), 10693, -43, -23831), new Location(Bukkit.getWorld("world"), 10678, -41, -23815)},

            new Location[]{new Location(Bukkit.getWorld("world"), 10765, -43, -23813), new Location(Bukkit.getWorld("world"), 10749, -41, -23797)},

            new Location[]{new Location(Bukkit.getWorld("world"), 10783, -43, -23813), new Location(Bukkit.getWorld("world"), 10767, -41, -23797)},

            new Location[]{new Location(Bukkit.getWorld("world"), 10801, -43, -23813), new Location(Bukkit.getWorld("world"), 10785, -41, -23797)},

            new Location[]{new Location(Bukkit.getWorld("world"), 10819, -43, -23813), new Location(Bukkit.getWorld("world"), 10803, -41, -23797)},

            new Location[]{new Location(Bukkit.getWorld("world"), 10711, -43, -23903), new Location(Bukkit.getWorld("world"), 10695, -41, -23887)}

    );

    public static GameManager getInstance(JavaPlugin plugin) {
        if (instance == null) {
            instance = new GameManager(plugin);
        }
        return instance;
    }

    public void startGame() {
        if (gameActive) return;

        gameActive = true;
        playerLaps.clear();
        playerQuadrants.clear();
        winners.clear();

        for (Player player : Bukkit.getOnlinePlayers()) {
            playerManager.addPlayerToGame(player);
        }

        Bukkit.getLogger().info("Juego iniciado para los jugadores permitidos.");

        Bukkit.getLogger().info("Juego iniciado para todos los jugadores.");
        spawnEyeForPlayers();
        spawnObstacles();
        trackPlayers();
    }

    private void spawnEyeForPlayers() {
        World world = Bukkit.getWorld("world");
        if (world == null) return;

        for (Location[] area : eyeAreas) {
            Location firstPos = area[0];
            Location secondPos = area[1];

            double centerX = (firstPos.getX() + secondPos.getX()) / 2;
            double centerZ = (firstPos.getZ() + secondPos.getZ()) / 2;
            double centerY = firstPos.getY();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (winners.contains(player)) continue;

                ItemDisplayManager eye = new ItemDisplayManager(world, centerX, centerY, centerZ);
                eye.setItemStack(new ItemStack(Material.ENDER_EYE));
                eye.setSize(3.0);
                playerEyes.put(player, eye);


            }
        }
    }

    private void trackPlayers() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!gameActive || playerEyes.isEmpty()) {
                    cancel();
                    return;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (winners.contains(player)) continue;

                    ItemDisplayManager eye = playerEyes.get(player);
                    if (eye != null) {
                        eye.lookAt(player.getLocation());
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void playerCompletedLap(Player player) {
        if (winners.contains(player) || !playerEyes.containsKey(player)) return;

        Location loc = player.getLocation();
        String quadrant = getQuadrant(loc);

        if (quadrant == null) return;

        Set<String> visitedQuadrants = playerQuadrants.computeIfAbsent(player, k -> new HashSet<>());
        visitedQuadrants.add(quadrant);

        if (visitedQuadrants.size() == 4) {
            playerLaps.put(player, playerLaps.getOrDefault(player, 0) + 1);
            visitedQuadrants.clear();

            int laps = playerLaps.get(player);


            if (laps >= requiredLaps) {
                shrinkEye(player);
            }
        }
    }

    private void shrinkEye(Player player) {
        if (!playerEyes.containsKey(player)) return;

        ItemDisplayManager eye = playerEyes.get(player);
        double currentEyeSize = eye.getSize();

        if (currentEyeSize > minEyeSize) {
            currentEyeSize -= eyeSizeDecrease;
            eye.setSize(currentEyeSize);

        }

        if (currentEyeSize <= minEyeSize) {
            winners.add(player);
            eye.removeItemDisplay();
            playerEyes.remove(player);


            int currentPoints = playerPoints.getOrDefault(player, 0);
            playerPoints.put(player, currentPoints + 10);

            player.sendMessage("¡Tu ojo ha desaparecido! Has ganado 10 puntos.");
        }
    }

    public void removePoints(Player player, int amount) {
        int currentPoints = playerPoints.getOrDefault(player, 0);
        int newPoints = Math.max(0, currentPoints - amount);
        playerPoints.put(player, newPoints);

        player.sendMessage("Te han restado " + amount + " puntos. Ahora tienes: " + newPoints);
    }

    private void spawnObstacles() {
        World world = Bukkit.getWorld("world");
        if (world == null) return;

        obstacles.clear();

        for (Location[] area : obstacleAreas) {
            Location firstPos = area[0];
            Location secondPos = area[1];

            double minX = Math.min(firstPos.getX(), secondPos.getX());
            double maxX = Math.max(firstPos.getX(), secondPos.getX());
            double minZ = Math.min(firstPos.getZ(), secondPos.getZ());
            double maxZ = Math.max(firstPos.getZ(), secondPos.getZ());

            double x = minX + random.nextDouble() * (maxX - minX);
            double z = minZ + random.nextDouble() * (maxZ - minZ);
            double y = firstPos.getY() + 5;

            ItemDisplayManager itemDisplay = new ItemDisplayManager(world, x, y, z);
            itemDisplay.setItemStack(new ItemStack(Material.STICK));
            obstacles.add(itemDisplay);

            double fallSpeed = 0.3;
            double targetY = obstacleTargetY;

            new BukkitRunnable() {
                double currentY = y;

                @Override
                public void run() {
                    if (currentY > targetY) {
                        currentY -= fallSpeed;
                        itemDisplay.updatePosition(x, currentY, z);
                    } else {
                        cancel();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                itemDisplay.removeItemDisplay();
                                obstacles.remove(itemDisplay);

                                if (obstacles.isEmpty()) {
                                    spawnObstacles();
                                }
                            }
                        }.runTaskLater(plugin, 100L);
                    }
                }
            }.runTaskTimer(plugin, 0, 1);
        }
    }

    private String getQuadrant(Location loc) {
        double dx = loc.getX() - eyeX;
        double dz = loc.getZ() - eyeZ;

        if (Math.sqrt(dx * dx + dz * dz) > minDistance) return null;

        if (dx > 0 && dz > 0) return "NE";
        if (dx > 0 && dz < 0) return "SE";
        if (dx < 0 && dz > 0) return "NW";
        if (dx < 0 && dz < 0) return "SW";

        return null;
    }

    public void freezePlayer(Player player) {
        player.setWalkSpeed(0);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setWalkSpeed(0.2f);
            }
        }.runTaskLater(plugin, 60L);
    }

    public List<ItemDisplayManager> getObstacles() {
        return obstacles;
    }

    public void stopGame() {
        if (!gameActive) return;

        gameActive = false;
        playerManager.clearPlayers();


        for (ItemDisplayManager eye : new ArrayList<>(playerEyes.values())) {
            eye.removeItemDisplay();
        }
        playerEyes.clear();


        for (ItemDisplayManager obstacle : new ArrayList<>(obstacles)) {
            obstacle.removeItemDisplay();
        }
        obstacles.clear();


        Bukkit.getScheduler().cancelTasks(plugin);

        winners.clear();

        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage("¡El juego ha sido detenido!"));
    }
}