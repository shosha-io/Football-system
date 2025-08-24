import soccer.PLAY.LEAGUE;
import soccer.PLAY.PLAYER;
import soccer.PLAY.TEAM;
import soccer.event.MATCH;
import soccer.event.POSSESSION;
import soccer.util.DATASAVE;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class Main {
    private static ArrayList<LEAGUE> leagues = new ArrayList<>();

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

        JLabel title = new JLabel(" ⚽ Welcome to Football System ⚽ ", SwingConstants.CENTER);
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
        JButton possessionBtn = new JButton("📊 Record Possession");
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

        // ===== Button Actions =====

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
                TEAM t = league.getTeams().stream()
                        .sorted(Comparator.comparing(TEAM::getTeamName, String.CASE_INSENSITIVE_ORDER))
                        .toList().get(row);
                String pname = JOptionPane.showInputDialog("Player name:");
                String age = JOptionPane.showInputDialog("Age:");
                String nat = JOptionPane.showInputDialog("Nationality:");
                try {
                    PLAYER p = new PLAYER();
                    p.setNamePlayer(pname);
                    p.setAgePlayer(Integer.parseInt(age));
                    p.setNationalityPlayer(nat);
                    p.setGoals(0);
                    t.add_players(p);
                    refreshPlayersTable(t, playerModel);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(leagueFrame, "Invalid input!");
                }
            } else {
                JOptionPane.showMessageDialog(leagueFrame, "Select a team first!");
            }
        });

        // Show players when team selected
        teamTable.getSelectionModel().addListSelectionListener(event -> {
            int row = teamTable.getSelectedRow();
            if (row >= 0 && row < league.getTeams().size()) {
                TEAM selectedTeam = league.getTeams().stream()
                        .sorted(Comparator.comparing(TEAM::getTeamName, String.CASE_INSENSITIVE_ORDER))
                        .toList().get(row);
                refreshPlayersTable(selectedTeam, playerModel);
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
                    int goals1, goals2;
                    try {
                        goals1 = Integer.parseInt(g1);
                        goals2 = Integer.parseInt(g2);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(leagueFrame, "Invalid number!");
                        return;
                    }

                    MATCH match = new MATCH(t1, t2);

                    selectScorers(leagueFrame, t1, goals1);
                    selectScorers(leagueFrame, t2, goals2);

                    t1.setTeam_goal(t1.getTeam_goal() + goals1);
                    t2.setTeam_goal(t2.getTeam_goal() + goals2);

                    if (goals1 > goals2) t1.setTeam_points(t1.getTeam_points() + 3);
                    else if (goals2 > goals1) t2.setTeam_points(t2.getTeam_points() + 3);
                    else {
                        t1.setTeam_points(t1.getTeam_points() + 1);
                        t2.setTeam_points(t2.getTeam_points() + 1);
                    }

                    league.addMatch(match);

                    refreshStandingsTable(league, standingsModel);
                    refreshPlayersTable(t1, playerModel);
                    refreshPlayersTable(t2, playerModel);

                    JOptionPane.showMessageDialog(leagueFrame,
                            "Result saved for " + match.getMatchName() + "!\n" +
                                    t1.getTeamName() + ": " + goals1 + " goals\n" +
                                    t2.getTeamName() + ": " + goals2 + " goals");
                }
            }
        });

        // Record Possession (with 90+ break)
        possessionBtn.addActionListener(e -> {
            if (league.getTeams().size() >= 2) {
                TEAM t1 = chooseTeam(leagueFrame, league);
                TEAM t2 = chooseTeam(leagueFrame, league);
                if (t1 != null && t2 != null && t1 != t2) {
                    MATCH match = new MATCH(t1, t2);

                    int currentTime = 0;
                    while (true) {
                        // Show team names instead of objects
                        String[] teamNames = {t1.getTeamName(), t2.getTeamName()};
                        String chosenName = (String) JOptionPane.showInputDialog(
                                leagueFrame,
                                "Which team had possession?",
                                "Possession",
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                teamNames,
                                teamNames[0]
                        );

                        if (chosenName == null) break; // cancel pressed

                        // Find team by name
                        TEAM chosenTeam = chosenName.equals(t1.getTeamName()) ? t1 : t2;

                        int start = Integer.parseInt(JOptionPane.showInputDialog(
                                "Start minute of possession (current: " + currentTime + ")"));
                        int end = Integer.parseInt(JOptionPane.showInputDialog("End minute of possession"));

                        POSSESSION pos = new POSSESSION();
                        pos.setTeam(chosenTeam);
                        pos.setStartTime(start);
                        pos.setEndTime(end);

                        match.addPossession(pos);

                        currentTime = end;
                        if (end >= 90) {
                            JOptionPane.showMessageDialog(leagueFrame, "Match finished (90+ minutes).");
                            break;
                        }
                    }

                    league.addMatch(match);
                    JOptionPane.showMessageDialog(leagueFrame,
                            "=== POSSESSION STATS ===\n" + match.getStats());
                }
            }
        });



        // View Matches
        viewMatchesBtn.addActionListener(e -> {
            JTextArea area = new JTextArea();
            for (MATCH m : league.getMatches()) {
                area.append(m.getMatchName() + "\n");
                area.append(m.getStats() + "\n\n");
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

    // ===== Refresh Helpers =====
    private static void refreshTeamTable(LEAGUE league, DefaultTableModel teamModel) {
        teamModel.setRowCount(0);
        league.getTeams().stream()
                .sorted(Comparator.comparing(TEAM::getTeamName, String.CASE_INSENSITIVE_ORDER))
                .forEach(t -> teamModel.addRow(new Object[]{t.getTeamName()}));
    }

    private static void refreshStandingsTable(LEAGUE league, DefaultTableModel standingsModel) {
        standingsModel.setRowCount(0);
        league.getTeams().stream()
                .sorted(
                        Comparator.comparingInt(TEAM::getTeam_points).reversed()
                                .thenComparing(Comparator.comparingInt(TEAM::getTeam_goal).reversed())
                                .thenComparing(TEAM::getTeamName, String.CASE_INSENSITIVE_ORDER)
                )
                .forEach(t -> standingsModel.addRow(new Object[]{t.getTeamName(), t.getTeam_points(), t.getTeam_goal()}));
    }

    private static void refreshPlayersTable(TEAM team, DefaultTableModel playerModel) {
        playerModel.setRowCount(0);
        team.getPlayers().stream()
                .sorted(Comparator.comparing(PLAYER::getNamePlayer, String.CASE_INSENSITIVE_ORDER))
                .forEach(p -> playerModel.addRow(new Object[]{p.getNamePlayer(), p.getAgePlayer(), p.getNationalityPlayer(), p.getGoals()}));
    }

    // ===== Scorer Selection =====
    private static void selectScorers(JFrame parent, TEAM team, int goals) {
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
    }

    // ===== Choose Helpers =====
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
        String[] names = league.getTeams().stream().map(TEAM::getTeamName).sorted(String.CASE_INSENSITIVE_ORDER).toArray(String[]::new);
        String choice = (String) JOptionPane.showInputDialog(frame, "Choose a team:",
                "Select Team", JOptionPane.QUESTION_MESSAGE, null, names, names[0]);
        for (TEAM t : league.getTeams()) if (t.getTeamName().equals(choice)) return t;
        return null;
    }
}
