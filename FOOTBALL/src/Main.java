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
            welcomeFrame.dispose(); // close welcome page
            createMainWindow();    // open main system
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(startBtn);
        welcomeFrame.add(btnPanel, BorderLayout.SOUTH);

        welcomeFrame.setVisible(true);
    }

    // ===== Main Window =====
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

    // ===== League Window =====
    private static void openLeagueWindow(LEAGUE league) {
        JFrame leagueFrame = new JFrame("League: " + league.getName_of_league());
        leagueFrame.setSize(1200, 650);
        leagueFrame.setLayout(new BorderLayout());
        leagueFrame.setLocationRelativeTo(null);

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

        // ... (rest of your existing event handling code for teams, players, results, possessions, matches, saving)

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
