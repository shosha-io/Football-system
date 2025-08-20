import soccer.PLAY.*;
import soccer.event.*;
import soccer.util.DATASAVE;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {
    private static ArrayList<LEAGUE> leagues = new ArrayList<>();
    private static ArrayList<MATCH> matches = new ArrayList<>();
    private static ArrayList<POSSESSION> possessions = new ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::showWelcomePage);
    }

    // ===== Welcome Page =====
    private static void showWelcomePage() {
        JFrame welcomeFrame = new JFrame("Football System");
        welcomeFrame.setSize(500, 300);
        welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        welcomeFrame.setLocationRelativeTo(null);
        welcomeFrame.setLayout(new BorderLayout());

        JLabel title = new JLabel("⚽ Welcome to Football System ⚽", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeFrame.add(title, BorderLayout.CENTER);

        JButton startBtn = new JButton("Start");
        startBtn.setFont(new Font("Arial", Font.PLAIN, 18));
        startBtn.addActionListener(e -> {
            welcomeFrame.dispose();
            createMainWindow();
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(startBtn);
        welcomeFrame.add(btnPanel, BorderLayout.SOUTH);

        welcomeFrame.setVisible(true);
    }

    // ===== Main Menu =====
    private static void createMainWindow() {
        JFrame frame = new JFrame("⚽ Football League Manager");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));

        JButton addLeagueBtn = new JButton("➕ Add League");
        JButton openLeagueBtn = new JButton("📂 Open League");
        JButton loadFileBtn = new JButton("📥 Load League File");
        JButton exitBtn = new JButton("❌ Exit");

        panel.add(addLeagueBtn);
        panel.add(openLeagueBtn);
        panel.add(loadFileBtn);
        panel.add(exitBtn);

        frame.add(panel, BorderLayout.CENTER);

        // Actions
        addLeagueBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(frame, "Enter league name:");
            if (name != null && !name.isEmpty()) {
                LEAGUE l = new LEAGUE();
                l.setName_of_league(name);
                leagues.add(l);
                JOptionPane.showMessageDialog(frame, "League created: " + name);
            }
        });

        openLeagueBtn.addActionListener(e -> {
            LEAGUE league = chooseLeague(frame);
            if (league != null) openLeagueWindow(league);
        });

        loadFileBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int option = chooser.showOpenDialog(frame);
            if (option == JFileChooser.APPROVE_OPTION) {
                String content = DATASAVE.loadLeagueFile(chooser.getSelectedFile().getAbsolutePath());
                JTextArea area = new JTextArea(content);
                area.setEditable(false);
                JScrollPane scroll = new JScrollPane(area);
                scroll.setPreferredSize(new Dimension(500, 400));
                JOptionPane.showMessageDialog(frame, scroll, "Loaded File", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        exitBtn.addActionListener(e -> System.exit(0));

        frame.setVisible(true);
    }

    // ===== League Window =====
    private static void openLeagueWindow(LEAGUE league) {
        JFrame leagueFrame = new JFrame("League: " + league.getName_of_league());
        leagueFrame.setSize(1200, 650);
        leagueFrame.setLayout(new BorderLayout());
        leagueFrame.setLocationRelativeTo(null);

        // Tables
        DefaultTableModel teamModel = new DefaultTableModel(new String[]{"Teams"}, 0);
        JTable teamTable = new JTable(teamModel);

        DefaultTableModel playerModel = new DefaultTableModel(new String[]{"Name", "Age", "Nationality", "Goals"}, 0);
        JTable playerTable = new JTable(playerModel);

        DefaultTableModel standingsModel = new DefaultTableModel(new String[]{"Team", "Points", "Goals"}, 0);
        JTable standingsTable = new JTable(standingsModel);

        // Load teams if already present
        refreshTeamTable(league, teamModel);
        refreshStandingsTable(league, standingsModel);

        JPanel centerPanel = new JPanel(new GridLayout(1, 3));
        centerPanel.add(new JScrollPane(teamTable));
        centerPanel.add(new JScrollPane(playerTable));
        centerPanel.add(new JScrollPane(standingsTable));

        // Buttons
        JButton addTeamBtn = new JButton("➕ Add Team");
        JButton addPlayerBtn = new JButton("➕ Add Player");
        JButton resultBtn = new JButton("🏆 Record Result");
        JButton possessionBtn = new JButton("📊 Possession Stats");
        JButton viewMatchesBtn = new JButton("📖 View Matches");
        JButton saveBtn = new JButton("💾 Save League");

        JPanel btnPanel = new JPanel(new GridLayout(1, 6, 10, 10));
        btnPanel.add(addTeamBtn);
        btnPanel.add(addPlayerBtn);
        btnPanel.add(resultBtn);
        btnPanel.add(possessionBtn);
        btnPanel.add(viewMatchesBtn);
        btnPanel.add(saveBtn);

        leagueFrame.add(centerPanel, BorderLayout.CENTER);
        leagueFrame.add(btnPanel, BorderLayout.SOUTH);

        // ====== Button Actions ======

        // Add Team
        addTeamBtn.addActionListener(e -> {
            String teamName = JOptionPane.showInputDialog("Enter team name:");
            if (teamName != null && !teamName.isEmpty()) {
                TEAM t = new TEAM();
                t.setTeamName(teamName);
                league.addTeam(t);
                refreshTeamTable(league, teamModel);
                refreshStandingsTable(league, standingsModel);
            }
        });

        // Add Player
        addPlayerBtn.addActionListener(e -> {
            int row = teamTable.getSelectedRow();
            if (row >= 0) {
                TEAM t = league.getTeams().get(row);
                String pname = JOptionPane.showInputDialog("Player name:");
                String age = JOptionPane.showInputDialog("Age:");
                String nat = JOptionPane.showInputDialog("Nationality:");
                PLAYER p = new PLAYER();
                p.setNamePlayer(pname);
                p.setAgePlayer(Integer.parseInt(age));
                p.setNationalityPlayer(nat);
                p.setGoals(0);
                t.add_players(p);
                playerModel.addRow(new Object[]{pname, age, nat, 0});
            } else {
                JOptionPane.showMessageDialog(leagueFrame, "Select a team first!");
            }
        });

        // Show players of selected team
        teamTable.getSelectionModel().addListSelectionListener(event -> {
            int row = teamTable.getSelectedRow();
            if (row >= 0 && row < league.getTeams().size()) {
                TEAM selectedTeam = league.getTeams().get(row);
                playerModel.setRowCount(0);
                for (PLAYER p : selectedTeam.getPlayers()) {
                    playerModel.addRow(new Object[]{p.getNamePlayer(), p.getAgePlayer(), p.getNationalityPlayer(), p.getGoals()});
                }
            }
        });

        // Record Result
        resultBtn.addActionListener(e -> {
            if (league.getTeams().size() >= 2) {
                TEAM t1 = chooseTeam(leagueFrame, league);
                TEAM t2 = chooseTeam(leagueFrame, league);
                if (t1 != null && t2 != null && t1 != t2) {
                    String g1 = JOptionPane.showInputDialog("Goals by " + t1.getTeamName());
                    String g2 = JOptionPane.showInputDialog("Goals by " + t2.getTeamName());
                    int goals1 = Integer.parseInt(g1);
                    int goals2 = Integer.parseInt(g2);

                    selectScorers(leagueFrame, t1, goals1, playerModel);
                    selectScorers(leagueFrame, t2, goals2, playerModel);

                    t1.setTeam_goal(t1.getTeam_goal() + goals1);
                    t2.setTeam_goal(t2.getTeam_goal() + goals2);

                    if (goals1 > goals2) t1.setTeam_points(t1.getTeam_points() + 3);
                    else if (goals2 > goals1) t2.setTeam_points(t2.getTeam_points() + 3);
                    else {
                        t1.setTeam_points(t1.getTeam_points() + 1);
                        t2.setTeam_points(t2.getTeam_points() + 1);
                    }

                    refreshStandingsTable(league, standingsModel);

                    JOptionPane.showMessageDialog(leagueFrame, "Result saved!\n" +
                            t1.getTeamName() + ": " + goals1 + " goals\n" +
                            t2.getTeamName() + ": " + goals2 + " goals");
                }
            }
        });

        // Possession Stats
        possessionBtn.addActionListener(e -> {
            if (league.getTeams().size() >= 2) {
                int totalTime = 0;
                while (totalTime < 90) {
                    TEAM team = chooseTeam(leagueFrame, league);
                    if (team == null) break;

                    String start = JOptionPane.showInputDialog("Start minute:");
                    String end = JOptionPane.showInputDialog("End minute:");
                    if (start == null || end == null) break;

                    int s = Integer.parseInt(start);
                    int en = Integer.parseInt(end);

                    POSSESSION p = new POSSESSION();
                    p.setStartTime(s);
                    p.setEndTime(en);
                    p.setTeam(team);
                    possessions.add(p);

                    totalTime += (en - s);
                    JOptionPane.showMessageDialog(leagueFrame, "Added possession for " + team.getTeamName());
                }

                TEAM t1 = league.getTeams().get(0);
                TEAM t2 = league.getTeams().get(1);
                String stats = POSSESSION.showStatsText(possessions, t1, t2);
                JOptionPane.showMessageDialog(leagueFrame, stats);
            }
        });

        // View Matches
        viewMatchesBtn.addActionListener(e -> {
            JTextArea area = new JTextArea();
            for (MATCH m : matches) {
                area.append(m.toString() + "\n");
            }
            JOptionPane.showMessageDialog(leagueFrame, new JScrollPane(area), "All Matches", JOptionPane.INFORMATION_MESSAGE);
        });

        // Save League
        saveBtn.addActionListener(e -> {
            DATASAVE.saveLeague(league, league.getName_of_league() + ".json");
            JOptionPane.showMessageDialog(leagueFrame, "League saved to " + league.getName_of_league() + ".json");
        });

        leagueFrame.setVisible(true);
    }

    // Refresh Teams Table (alphabetical order)
    private static void refreshTeamTable(LEAGUE league, DefaultTableModel teamModel) {
        teamModel.setRowCount(0);
        league.getTeams().stream()
                .sorted(Comparator.comparing(TEAM::getTeamName, String.CASE_INSENSITIVE_ORDER))
                .forEach(t -> teamModel.addRow(new Object[]{t.getTeamName()}));
    }

    // Refresh Standings Table (points DESC, then goals DESC)
    private static void refreshStandingsTable(LEAGUE league, DefaultTableModel standingsModel) {
        standingsModel.setRowCount(0);
        league.getTeams().stream()
                .sorted(Comparator.comparingInt(TEAM::getTeam_points).reversed()
                        .thenComparing(Comparator.comparingInt(TEAM::getTeam_goal).reversed()))
                .forEach(t -> standingsModel.addRow(new Object[]{t.getTeamName(), t.getTeam_points(), t.getTeam_goal()}));
    }

    // Select goal scorers
    private static void selectScorers(JFrame parent, TEAM team, int goals, DefaultTableModel playerModel) {
        if (goals <= 0 || team.getPlayers().isEmpty()) return;
        for (int i = 0; i < goals; i++) {
            String[] names = team.getPlayers().stream().map(PLAYER::getNamePlayer).toArray(String[]::new);
            String choice = (String) JOptionPane.showInputDialog(parent,
                    "Select scorer for goal " + (i + 1) + " (" + team.getTeamName() + ")",
                    "Goal Scorer", JOptionPane.QUESTION_MESSAGE, null, names, names[0]);
            if (choice != null) {
                for (PLAYER p : team.getPlayers()) {
                    if (p.getNamePlayer().equals(choice)) {
                        p.setGoals(p.getGoals() + 1);
                    }
                }
            }
        }

        playerModel.setRowCount(0);
        for (PLAYER p : team.getPlayers()) {
            playerModel.addRow(new Object[]{p.getNamePlayer(), p.getAgePlayer(), p.getNationalityPlayer(), p.getGoals()});
        }
    }

    private static LEAGUE chooseLeague(JFrame frame) {
        if (leagues.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No leagues yet!");
            return null;
        }
        String[] names = leagues.stream().map(LEAGUE::getName_of_league).toArray(String[]::new);
        String choice = (String) JOptionPane.showInputDialog(frame, "Choose a league:",
                "Select League", JOptionPane.QUESTION_MESSAGE, null, names, names[0]);
        for (LEAGUE l : leagues) if (l.getName_of_league().equals(choice)) return l;
        return null;
    }

    private static TEAM chooseTeam(JFrame frame, LEAGUE league) {
        if (league.getTeams().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No teams in this league!");
            return null;
        }
        String[] names = league.getTeams().stream().map(TEAM::getTeamName).toArray(String[]::new);
        String choice = (String) JOptionPane.showInputDialog(frame, "Choose a team:",
                "Select Team", JOptionPane.QUESTION_MESSAGE, null, names, names[0]);
        for (TEAM t : league.getTeams()) if (t.getTeamName().equals(choice)) return t;
        return null;
    }
}
