import soccer.PLAY.*;
import soccer.event.*;
import soccer.util.DATASAVE;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class Main {
    private static ArrayList<LEAGUE> leagues = new ArrayList<>();
    private static ArrayList<MATCH> matches = new ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createMainWindow);
    }

    private static void createMainWindow() {
        JFrame frame = new JFrame("⚽ Football League Manager");
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

        // === Actions ===
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

    private static void openLeagueWindow(LEAGUE league) {
        JFrame leagueFrame = new JFrame("League: " + league.getName_of_league());
        leagueFrame.setSize(1200, 650);
        leagueFrame.setLayout(new BorderLayout());

        // ===== Tables =====
        DefaultTableModel teamModel = new DefaultTableModel(new String[]{"Teams"}, 0);
        JTable teamTable = new JTable(teamModel);

        DefaultTableModel playerModel = new DefaultTableModel(new String[]{"Name","Age","Nationality","Goals"}, 0);
        JTable playerTable = new JTable(playerModel);

        DefaultTableModel standingsModel = new DefaultTableModel(new String[]{"Team","Points","Goals"}, 0);
        JTable standingsTable = new JTable(standingsModel);

        // Load existing teams
        for (TEAM t : league.getTeams()) {
            teamModel.addRow(new Object[]{t.getTeamName()});
            standingsModel.addRow(new Object[]{t.getTeamName(), t.getTeam_points(), t.getTeam_goal()});
        }

        JPanel centerPanel = new JPanel(new GridLayout(1, 3));
        centerPanel.add(new JScrollPane(teamTable));
        centerPanel.add(new JScrollPane(playerTable));
        centerPanel.add(new JScrollPane(standingsTable));

        // ===== Buttons =====
        JButton addTeamBtn = new JButton("➕ Add Team");
        JButton addPlayerBtn = new JButton("➕ Add Player");
        JButton resultBtn = new JButton("🏆 Record Result");
        JButton possessionBtn = new JButton("📊 Possession Stats");
        JButton viewMatchesBtn = new JButton("📖 View Matches");
        JButton saveBtn = new JButton("💾 Save League");

        JPanel btnPanel = new JPanel(new GridLayout(1,6,10,10));
        btnPanel.add(addTeamBtn);
        btnPanel.add(addPlayerBtn);
        btnPanel.add(resultBtn);
        btnPanel.add(possessionBtn);
        btnPanel.add(viewMatchesBtn);
        btnPanel.add(saveBtn);

        leagueFrame.add(centerPanel, BorderLayout.CENTER);
        leagueFrame.add(btnPanel, BorderLayout.SOUTH);

        // ===== Actions =====
        addTeamBtn.addActionListener(e -> {
            String teamName = JOptionPane.showInputDialog("Enter team name:");
            if (teamName != null && !teamName.isEmpty()) {
                TEAM t = new TEAM();
                t.setTeamName(teamName);
                league.addTeam(t);

                // sort teams by name
                league.getTeams().sort(Comparator.comparing(TEAM::getTeamName));

                // refresh tables
                teamModel.setRowCount(0);
                standingsModel.setRowCount(0);
                for (TEAM tm : league.getTeams()) {
                    teamModel.addRow(new Object[]{tm.getTeamName()});
                    standingsModel.addRow(new Object[]{tm.getTeamName(), tm.getTeam_points(), tm.getTeam_goal()});
                }
            }
        });

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
                JOptionPane.showMessageDialog(leagueFrame,"Select a team first!");
            }
        });

        // ✅ Show only players of selected team
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

        // ⚽ Record result with goal scorers
        resultBtn.addActionListener(e -> {
            if (league.getTeams().size() >= 2) {
                TEAM t1 = chooseTeam(leagueFrame, league);
                TEAM t2 = chooseTeam(leagueFrame, league);
                if (t1 != null && t2 != null && t1 != t2) {
                    String g1 = JOptionPane.showInputDialog("Goals by " + t1.getTeamName());
                    String g2 = JOptionPane.showInputDialog("Goals by " + t2.getTeamName());
                    int goals1 = Integer.parseInt(g1);
                    int goals2 = Integer.parseInt(g2);

                    // Select scorers
                    selectScorers(leagueFrame, t1, goals1, playerModel);
                    selectScorers(leagueFrame, t2, goals2, playerModel);

                    t1.setTeam_goal(t1.getTeam_goal()+goals1);
                    t2.setTeam_goal(t2.getTeam_goal()+goals2);

                    if (goals1 > goals2) t1.setTeam_points(t1.getTeam_points()+3);
                    else if (goals2 > goals1) t2.setTeam_points(t2.getTeam_points()+3);
                    else {
                        t1.setTeam_points(t1.getTeam_points()+1);
                        t2.setTeam_points(t2.getTeam_points()+1);
                    }

                    // sort standings by points
                    league.getTeams().sort((a,b) -> b.getTeam_points() - a.getTeam_points());

                    standingsModel.setRowCount(0);
                    for (TEAM team : league.getTeams()) {
                        standingsModel.addRow(new Object[]{team.getTeamName(), team.getTeam_points(), team.getTeam_goal()});
                    }

                    JOptionPane.showMessageDialog(leagueFrame,"Result saved!\n"+
                            t1.getTeamName()+": "+goals1+" goals\n"+
                            t2.getTeamName()+": "+goals2+" goals");
                }
            }
        });

        // 📊 Possession stats per match
        possessionBtn.addActionListener(e -> {
            if (league.getTeams().size() >= 2) {
                TEAM t1 = chooseTeam(leagueFrame, league);
                TEAM t2 = chooseTeam(leagueFrame, league);

                if (t1 == null || t2 == null || t1 == t2) {
                    JOptionPane.showMessageDialog(leagueFrame, "Please choose two different teams!");
                    return;
                }

                // find or create match
                MATCH match = null;
                for (MATCH m : matches) {
                    if ((m.getTeam1() == t1 && m.getTeam2() == t2) ||
                            (m.getTeam1() == t2 && m.getTeam2() == t1)) {
                        match = m;
                        break;
                    }
                }
                if (match == null) {
                    match = new MATCH(t1, t2);
                    matches.add(match);
                }

                // record possession
                String start = JOptionPane.showInputDialog("Start minute:");
                if (start == null) return;
                String end = JOptionPane.showInputDialog("End minute:");
                if (end == null) return;
                TEAM possessionTeam = chooseTeam(leagueFrame, league);
                if (possessionTeam == null) return;

                int s = Integer.parseInt(start);
                int en = Integer.parseInt(end);

                POSSESSION p = new POSSESSION();
                p.setStartTime(s);
                p.setEndTime(en);
                p.setTeam(possessionTeam);
                match.addPossession(p);

                JOptionPane.showMessageDialog(leagueFrame,
                        "Added possession for " + possessionTeam.getTeamName() +
                                " from " + s + " to " + en + " mins");

                // show stats so far
                JOptionPane.showMessageDialog(leagueFrame, match.getStats());
            }
        });

        // 📖 View Matches
        viewMatchesBtn.addActionListener(e -> {
            if (matches.isEmpty()) {
                JOptionPane.showMessageDialog(leagueFrame, "No matches recorded yet!");
                return;
            }

            StringBuilder sb = new StringBuilder("=== All Matches & Possession Stats ===\n\n");
            for (MATCH m : matches) {
                sb.append(m.getMatchName()).append("\n");
                sb.append(m.getStats()).append("\n");
            }

            JTextArea area = new JTextArea(sb.toString());
            area.setEditable(false);
            JScrollPane scroll = new JScrollPane(area);
            scroll.setPreferredSize(new Dimension(600, 400));
            JOptionPane.showMessageDialog(leagueFrame, scroll, "Match Review", JOptionPane.INFORMATION_MESSAGE);
        });

        saveBtn.addActionListener(e -> {
            DATASAVE.saveLeague(league, league.getName_of_league()+".json");
            JOptionPane.showMessageDialog(leagueFrame,"League saved to "+league.getName_of_league()+".json");
        });

        leagueFrame.setVisible(true);
    }

    // ✅ helper to choose goal scorers
    private static void selectScorers(JFrame parent, TEAM team, int goals, DefaultTableModel playerModel) {
        if (goals <= 0 || team.getPlayers().isEmpty()) return;

        for (int i=0; i<goals; i++) {
            String[] names = team.getPlayers().stream().map(PLAYER::getNamePlayer).toArray(String[]::new);
            String choice = (String) JOptionPane.showInputDialog(parent,
                    "Select scorer for goal " + (i+1) + " ("+team.getTeamName()+")",
                    "Goal Scorer", JOptionPane.QUESTION_MESSAGE, null, names, names[0]);
            if (choice != null) {
                for (PLAYER p : team.getPlayers()) {
                    if (p.getNamePlayer().equals(choice)) {
                        p.setGoals(p.getGoals()+1);
                    }
                }
            }
        }

        // refresh player table for that team
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
