/*;
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.ppd2le;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.Color;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.time.Clock;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 *
 * @author Khloe
 */
public class MainFrame extends javax.swing.JFrame {
    

     // Declares a static PersonList object to hold all persons (lenders and borrowers)
    public static PersonList PArray = new PersonList();
    // Declares a Lender object to store the currently logged-in lender
    private Lender currentLoggedInLender;
    // Declares an ArrayList to store borrowers associated with the current lender
    private ArrayList<Borrower> currentLenderBorrowerList; 
    // Declares a Clock object for simulating time
    private Clock currentClock;
    /**
     * Creates new form MainFrame
     */
    
    // Constructor for MainFrame that takes an authenticated Lender object
    public MainFrame(Lender authenticatedLender) {
        this.currentLoggedInLender = authenticatedLender; // Sets the current logged-in lender
        this.currentLenderBorrowerList = new ArrayList<>(); // Initializes the lender-specific borrower list
        initComponents(); // Initializes GUI components
        this.currentClock = Clock.systemDefaultZone(); // Sets the default system clock
        
        // Loads borrowers specific to the logged-in lender
        loadLenderBorrowers(); 
        refreshTable(); // Refreshes the table with lender-specific borrowers
        jLabel8.setText("₱" + String.format("%.2f", calculateTotalPooledAmount())); // Updates the total pooled amount display
    }
    
    
    public Lender getCurrentLoggedInLender() {
        return currentLoggedInLender;
    }
    
    public MainFrame() {
        initComponents();
        this.currentClock = Clock.systemDefaultZone();
        //refreshTable();
    }
    
    // Helper method to load borrowers specific to the current logged-in lender from a CSV file
    private void loadLenderBorrowers() {
        String filename = "borrowers_" + currentLoggedInLender.getUsername() + ".csv"; // Constructs the filename based on the lender's username
        currentLenderBorrowerList = PersonList.readBorrowersFromCsv(filename); // Reads borrowers from the CSV file
        currentLoggedInLender.setAssociatedBorrowers(currentLenderBorrowerList); // Associates the loaded borrowers with the current lender
    }

    // Helper method to save borrowers specific to the current logged-in lender to a CSV file
    public void saveLenderBorrowers() {
        String filename = "borrowers_" + currentLoggedInLender.getUsername() + ".csv"; // Constructs the filename based on the lender's username
        PersonList.saveBorrowersToCsv(filename, currentLoggedInLender.getAssociatedBorrowers()); // Saves the lender's associated borrowers to the CSV file
    }
    
    // Refreshes the display table with all borrowers
    public void refreshTable() {
        // Calls refreshTable with no search term to display all borrowers
        refreshTable(null); 
    }
    // Refreshes the display table, optionally filtering by a search term
    public void refreshTable(String searchTerm) {
        String columns[] = {"Name", "Contribution this Cycle", "Total Contributed", "Next Payout Date", "Paid Monthly Contribution"}; // Defines table column headers
        DefaultTableModel model = new DefaultTableModel(columns, 0) { // Creates a new table model
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Makes only the "Paid Monthly Contribution" column editable
            }
        };

        List<Borrower> borrowersToDisplay;

        // Determines which borrowers to display based on the search term
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            borrowersToDisplay = currentLoggedInLender.getAssociatedBorrowers(); // Displays all borrowers if no search term
        } else {
            borrowersToDisplay = new ArrayList<>(); // Initializes a new list for filtered borrowers
            String lowerSearchTerm = searchTerm.toLowerCase(); // Converts search term to lowercase for case-insensitive search
            for (Borrower borrower : currentLoggedInLender.getAssociatedBorrowers()) { // Iterates through the lender's borrowers
                if (borrower.getName().toLowerCase().contains(lowerSearchTerm)) { // Checks if borrower's name contains the search term
                    borrowersToDisplay.add(borrower); // Adds matching borrower to the display list
                }
            }
        }

        // Populates the table model with borrower data
        if (borrowersToDisplay != null) {
            for (Borrower participant : borrowersToDisplay) {
                String nextDueDateStatus;
                // Determines the status for "Next Payout Date" column
                if (participant.getNextContributionDueDate() != null &&
                    participant.getNextContributionDueDate().isEqual(LocalDate.MAX)) {
                    nextDueDateStatus = "Paid Out (Current Cycle)"; // If date is MAX, means paid out
                } else if (participant.getNextContributionDueDate() != null) {
                    nextDueDateStatus = participant.getNextContributionDueDate().toString(); // Displays the actual date
                } else {
                    nextDueDateStatus = "N/A (No due date)"; // If no due date
                }

                String paidStatus = participant.getHasPaidCurrentMonthContribution() ? "PAID" : "NOT PAID"; // Determines paid status

                Object[] rowData = {
                    participant.getName(),
                    String.format("₱%.2f", participant.getRegularContributionAmount()),
                    String.format("₱%.2f", participant.getTotalContributed()),
                    nextDueDateStatus,
                    paidStatus
                };
                model.addRow(rowData); // Adds a row to the table model
            }

            jTable1.setModel(new DefaultTableModel()); // Clears the existing table model
            jTable1.setModel(model); // Sets the new table model

            // Configures cell renderers for alignment and color coding
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

            jTable1.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
            jTable1.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
            jTable1.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
            jTable1.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    String status = (String) value;
                    if ("PAID".equals(status)) {
                        c.setBackground(Color.GREEN); // Sets background to green if "PAID"
                        c.setForeground(Color.BLACK); // Sets foreground to black
                    } else {
                        c.setBackground(Color.RED); // Sets background to red if "NOT PAID"
                        c.setForeground(Color.WHITE); // Sets foreground to white
                    }
                    setHorizontalAlignment(SwingConstants.CENTER); // Centers the text
                    return c;
                }
            });

            // Updates the participant count display
            if (currentLoggedInLender.getAssociatedBorrowers().isEmpty()) {
                participantField.setText("0");
            } else {
                participantField.setText(String.valueOf(currentLoggedInLender.getAssociatedBorrowers().size()));
            }
        }
        updateNextRecipientDisplay(); // Updates the "Next Recipient" display
        jLabel8.setText("₱" + String.format("%.2f", calculateTotalPooledAmount())); // Updates the total pooled amount display
    }
    


    // Determines the next eligible borrower to receive a payout
    private Borrower determineNextRecipient() {
        Borrower nextRecipient = null;
        LocalDate earliestEligibleDueDate = null;

        LocalDate simulatedCurrentDate = LocalDate.now(currentClock); // Gets the current simulated date

        System.out.println("\n--- Starting determineNextRecipient (Simulated Date: " + simulatedCurrentDate + ") ---");

        // Iterates through the lender's associated borrowers
        if (currentLoggedInLender.getAssociatedBorrowers() != null) {
            for (Borrower participant : currentLoggedInLender.getAssociatedBorrowers()) {
                System.out.println("    Checking participant: " + participant.getName() +
                                       ", Current Due Date: " + participant.getNextContributionDueDate());

                // Checks if the participant is eligible (has a due date, not MAX, and hasn't received payout)
                if (participant.getNextContributionDueDate() != null &&
                    !participant.getNextContributionDueDate().isEqual(LocalDate.MAX) &&
                    !participant.hasReceivedPayout()) {
                    // Finds the participant with the earliest eligible due date
                    if (earliestEligibleDueDate == null ||
                        participant.getNextContributionDueDate().isBefore(earliestEligibleDueDate)) {

                        nextRecipient = participant;
                        earliestEligibleDueDate = participant.getNextContributionDueDate();
                        System.out.println("        New earliest eligible: " + nextRecipient.getName() + " on " + earliestEligibleDueDate);
                    } else if (participant.getNextContributionDueDate().isEqual(earliestEligibleDueDate)) {
                        // Tie-breaker: if due dates are equal, chooses alphabetically
                        if (nextRecipient != null && participant.getName().compareToIgnoreCase(nextRecipient.getName()) < 0) {
                            nextRecipient = participant;
                            System.out.println("        Tie-breaker: " + nextRecipient.getName() + " chosen alphabetically.");
                        }
                    }
                } else {
                    System.out.println("        " + participant.getName() + " is NOT eligible (due date null/MAX, or has received payout).");
                }
            }
        }
        System.out.println("--- End determineNextRecipient. Final nextRecipient: " + (nextRecipient != null ? nextRecipient.getName() : "N/A") + " ---");
        return nextRecipient; // Returns the determined next recipient
    }
    
    // Updates the display for the next recipient
    private void updateNextRecipientDisplay() {
        Borrower nextRecipient = determineNextRecipient(); // Determines the next recipient
        if (nextRecipient != null) {
            jLabel9.setText(nextRecipient.getName()); // Sets the label to the recipient's name
        } else {
            jLabel9.setText("N/A"); // Sets to "N/A" if no recipient
        }
    }
    // Starts a new cycle for all participants
    private void startNewCycle() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to start a new cycle?\n"
            + "This will set due dates for each participant, starting 1 month from now and incrementing monthly.",
            "Confirm New Cycle",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) { // If user confirms
            System.out.println("\n--- Starting New Cycle ---");

            LocalDate currentDueDate = LocalDate.now(currentClock).plusMonths(1); // Sets the starting due date to one month from now

            // Iterates through associated borrowers
            if (currentLoggedInLender.getAssociatedBorrowers() != null) {
                for (Borrower participant : currentLoggedInLender.getAssociatedBorrowers()) {
                    participant.setNextContributionDueDate(currentDueDate); // Sets the next contribution due date
                    participant.recordContribution(); // Records a contribution (resets hasPaidCurrentMonthContribution, updates total contributed)

                    participant.setHasReceivedPayout(false); // Resets payout status for the new cycle

                    currentDueDate = currentDueDate.plusMonths(1); // Increments the due date for the next participant
                    System.out.println("    Next participant's due date will be: " + currentDueDate);
                }
            }
            refreshTable(); // Refreshes the table
            saveLenderBorrowers(); // Saves the updated borrower data
            JOptionPane.showMessageDialog(this, "New cycle started! Due dates assigned sequentially and contributions recorded.", "New Cycle", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("--- New Cycle Started ---");
        }
    }
    
    // Calculates the total pooled amount from all participants' regular contributions
    private double calculateTotalPooledAmount() {
        double total = 0;
        if (currentLoggedInLender.getAssociatedBorrowers() != null) {
            for (Borrower participant : currentLoggedInLender.getAssociatedBorrowers()) {
                total += participant.getRegularContributionAmount(); // Adds each participant's regular contribution
            }
        }
        return total; // Returns the total
    }
    // Sets the current clock for simulated time
    public void setClock(Clock clock) {
        this.currentClock = clock; // Sets the clock
        refreshTable(); // Refreshes the table
        updateNextRecipientDisplay(); // Updates the next recipient display
    }
    
    // Static method to write lender data to a CSV file
    public static void WriteToFile() {
        // Calls the saveToCsv method in PersonList to handle writing the data
        PArray.saveToCsv("LenderPaluwaganList.csv");
    }
    
    // Helper method to unescape CSV values (remove quotes and unescape double quotes)
    private static String unescapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.length() > 1 && value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
            value = value.replace("\"\"", "\"");
        }
        return value;
    }
    
    // Static method to read lender data from a CSV file
    public static void ReadFromFile() {
        PArray.clearAllPersons(); // Clears existing person data to ensure a clean slate

        String filePath = "LenderPaluwaganList.csv";
        File file = new File(filePath);

        if (!file.exists()) { // Checks if the file exists
            System.out.println("Lender data file " + filePath + " not found. Starting with empty lender data.");
            return; // Exits if file doesn't exist
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) { // Reads the file line by line
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Splits by comma outside quotes

                if (parts.length >= 5) { // Ensures sufficient parts for a lender
                    try {
                        String name = PersonList.unescapeCsv(parts[0]).trim();
                        String phoneNumber = PersonList.unescapeCsv(parts[1]).trim();
                        String emailAddress = PersonList.unescapeCsv(parts[2]).trim();
                        String username = PersonList.unescapeCsv(parts[3]).trim();
                        String passwordHash = PersonList.unescapeCsv(parts[4]).trim();

                        Lender lender = new Lender(name, phoneNumber, emailAddress, username, passwordHash);

                        // Loads associated borrowers for this lender
                        String borrowerFilename = username + "_borrowers.csv";
                        // Assuming PersonList has a static method or you have a PersonList instance to call readBorrowersFromFile
                        // For simplicity, let's assume readBorrowersFromFile is a static method in PersonList for this context
                        ArrayList<Borrower> associatedBorrowers = PersonList.readBorrowersFromFile(borrowerFilename);
                        lender.setAssociatedBorrowers(associatedBorrowers); // Sets the associated borrowers

                        PArray.addLender(lender); // Adds the newly created Lender to the global PArray

                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing numeric data in lender line: " + line + " - " + e.getMessage());
                    } catch (Exception e) {
                        System.err.println("Error parsing lender line: " + line + " - " + e.getMessage());
                        e.printStackTrace(); // Prints stack trace for detailed debugging
                    }
                } else {
                    System.err.println("Skipping malformed lender line (less than 5 parts): " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading lender file " + filePath + ": " + e.getMessage());
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jScrollPane2 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        jSeparator6 = new javax.swing.JSeparator();
        jSeparator7 = new javax.swing.JSeparator();
        jButton4 = new javax.swing.JButton();
        jScrollBar1 = new javax.swing.JScrollBar();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JSeparator();
        jButton5 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JSeparator();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        participantField = new javax.swing.JLabel();
        numberField = new javax.swing.JLabel();
        nameField = new javax.swing.JLabel();
        emailField = new javax.swing.JLabel();
        dueField = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();

        jMenu3.setText("jMenu3");

        jMenu4.setText("jMenu4");

        jScrollPane2.setViewportView(jEditorPane1);

        jButton4.setText("Add Payment");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(new java.awt.Dimension(962, 495));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Name", "Contribution this Cycle", "Total Contributed", "Next Payout Date", "Paid Monthly Contribution"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Name:");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Phone Number:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Next Payout:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Email Address:");

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jButton1.setText("Update Record");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Payout");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Add Contribution");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton5.setBackground(javax.swing.UIManager.getDefaults().getColor("TitlePane.closePressedBackground"));
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("Remove Record");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Search");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Total Pooled Amount: ");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Next Recipient: ");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Number of Active Participants:");

        jButton8.setText("Add Participant");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setBackground(javax.swing.UIManager.getDefaults().getColor("TitlePane.closePressedBackground"));
        jButton9.setForeground(new java.awt.Color(255, 255, 255));
        jButton9.setText("Exit Program");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("₱0.0");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        participantField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        participantField.setText("0");

        numberField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        nameField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        emailField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        dueField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jButton10.setBackground(javax.swing.UIManager.getDefaults().getColor("TitlePane.closePressedBackground"));
        jButton10.setForeground(new java.awt.Color(255, 255, 255));
        jButton10.setText("Clear");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jMenu6.setText("Cycle");

        jMenuItem1.setText("Start New Cycle");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem1);

        jMenuItem4.setText("Clear List");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem4);

        jMenuBar1.add(jMenu6);

        jMenu2.setText("Account");

        jMenuItem5.setText("Lender Account Details");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuItem6.setText("Signout");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem6);

        jMenuBar1.add(jMenu2);

        jMenu1.setText("Change Date");

        jMenuItem2.setText("Add 1 Month");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText("Add 1 Day");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextField1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 789, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3))
                                .addGap(38, 38, 38)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(emailField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(dueField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(nameField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(numberField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(jButton5))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton3))
                                .addGap(32, 32, 32))))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(43, 43, 43)
                                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(78, 78, 78))
                            .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 524, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(34, 34, 34)
                                .addComponent(participantField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6))
                                .addGap(90, 90, 90)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addContainerGap())))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(numberField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(16, 16, 16)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(dueField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3)
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton5)))
                .addGap(0, 0, 0)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(participantField))
                .addGap(26, 26, 26)
                .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton8)
                    .addComponent(jButton9))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6)
                    .addComponent(jButton10))
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 445, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        // Action listener for the "Update Record" button
        
         int selectedRow = jTable1.getSelectedRow(); // Gets the selected row index

        if (selectedRow >= 0) { // If a row is selected
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            String selectedBorrowerName = model.getValueAt(selectedRow, 0).toString(); // Gets the name of the selected borrower

            Borrower retrievedBorrower = null;
            // Searches for the borrower within the current lender's associated borrowers
            if (currentLoggedInLender != null && currentLoggedInLender.getAssociatedBorrowers() != null) {
                for (Borrower b : currentLoggedInLender.getAssociatedBorrowers()) {
                    if (b.getName().equals(selectedBorrowerName)) {
                        retrievedBorrower = b;
                        break;
                    }
                }
            }

            if (retrievedBorrower != null) { // If borrower is found
                // Opens the EditPersonFrame to allow updating borrower details
                EditPersonFrame editPerson = new EditPersonFrame(this, retrievedBorrower);
                editPerson.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Could not find the selected participant. Please ensure they are in your list.", "Data Error", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a participant to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
         // Action listener for the "Payout" button (early payout)
         // Early Payout
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow >= 0) { // If a row is selected
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            String selectedBorrowerName = model.getValueAt(selectedRow, 0).toString();
            
            // Finds the Borrower object in the current lender's list
            Borrower retrievedBorrower = null;
            for (Borrower b : currentLoggedInLender.getAssociatedBorrowers()) {
                if (b.getName().equals(selectedBorrowerName)) {
                    retrievedBorrower = b;
                    break;
                }
            }

            if (retrievedBorrower != null) { // If borrower is found
                // Checks if the borrower has already received an early payout
                if (retrievedBorrower.getNextContributionDueDate() != null && retrievedBorrower.getNextContributionDueDate().isEqual(LocalDate.MAX)) {
                    JOptionPane.showMessageDialog(this, retrievedBorrower.getName() + " has already received an early payout for the current cycle.", "Payout Status", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                // Confirms the early payout with the user
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to give an early payout to " + retrievedBorrower.getName() + "?\n" + "They will be removed from the *current* cycle's recipient selection.", "Confirm Early Payout", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) { // If confirmed
                    System.out.println("\n--- Payout Action for: " + retrievedBorrower.getName() + " ---");
                    System.out.println(" Before Payout: " + retrievedBorrower.getName() + "'s Due Date: " + retrievedBorrower.getNextContributionDueDate());
                    retrievedBorrower.setNextContributionDueDate(LocalDate.MAX); // Marks as paid out
                    System.out.println(" After Payout: " + retrievedBorrower.getName() + "'s New Due Date: " + retrievedBorrower.getNextContributionDueDate());
                    refreshTable(); // Refreshes the table
                    saveLenderBorrowers(); // Saves changes
                    JOptionPane.showMessageDialog(this, retrievedBorrower.getName() + " has received an early payout and is removed from the current cycle.", "Early Payout Successful", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selected borrower not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a borrower from the table.", "No Borrower Selected", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
         // Action listener for the "Add Contribution" button
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) { // If no row is selected
            JOptionPane.showMessageDialog(this, "Please select a participant from the table to add contribution.", "No Participant Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Borrower selectedBorrower = null;
        // Retrieves the Borrower object directly from the currentLoggedInLender's list
        if (currentLoggedInLender != null && currentLoggedInLender.getAssociatedBorrowers() != null) {
            try {
                selectedBorrower = currentLoggedInLender.getAssociatedBorrowers().get(selectedRow);
            } catch (IndexOutOfBoundsException e) {
                JOptionPane.showMessageDialog(this, "Error: Selected participant data not found in list.", "Data Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (selectedBorrower != null) { // If a borrower is selected
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to record a contribution for " + selectedBorrower.getName() + "?",
                    "Confirm Contribution",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) { // If confirmed
                selectedBorrower.recordContribution(); // Records the contribution (updates totalContributed and sets hasPaidCurrentMonthContribution to true)

                // Removed the logic for advancing nextContributionDueDate

                saveLenderBorrowers(); // Saves the changes to the file
                refreshTable(); // Refreshes the entire table's data model

                // Ensures the table re-renders the specific row or the entire table
                SwingUtilities.invokeLater(() -> {
                    jTable1.revalidate();
                    jTable1.repaint();
                });

                jLabel8.setText("₱" + String.valueOf(calculateTotalPooledAmount())); // Updates pooled amount display
                JOptionPane.showMessageDialog(this, selectedBorrower.getName() + "'s contribution recorded!", "Contribution Successful", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selected participant not found. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        // Action listener for the "Remove Record" button
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow >= 0) { // If a row is selected
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            String selectedBorrowerName = model.getValueAt(selectedRow, 0).toString();
            
            // Finds the Borrower object in the current lender's list
            Borrower retrievedBorrower = null;
            for (Borrower b : currentLoggedInLender.getAssociatedBorrowers()) {
                if (b.getName().equals(selectedBorrowerName)) {
                    retrievedBorrower = b;
                    break;
                }
            }

            if (retrievedBorrower != null) { // If borrower is found
                // Confirms removal with the user
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to remove " + retrievedBorrower.getName() + " from the participants list?\n" +
                    "This action cannot be undone.", 
                    "Confirm Removal", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) { // If confirmed
                    // Removes the borrower from the lender's associated borrowers list
                    currentLoggedInLender.getAssociatedBorrowers().remove(retrievedBorrower); 
                    refreshTable(); // Refreshes the table to reflect the removal
                    saveLenderBorrowers(); // Saves changes to the lender's borrower file
                    JOptionPane.showMessageDialog(this, retrievedBorrower.getName() + " has been successfully removed.", "Participant Removed", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selected borrower not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a borrower from the table.", "No Borrower Selected", JOptionPane.WARNING_MESSAGE);
        }
        
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        // Action listener for the "Add Participant" button
        AddPersonFrame addPerson = new AddPersonFrame(this); // Opens the AddPersonFrame
        addPerson.setVisible(true);
        refreshTable(); // Refreshes the table after adding a person
        
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        // Action listener for the "Exit Program" button
       saveLenderBorrowers(); // Saves borrower data before exiting
       System.exit(0); // Exits the program
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        // Mouse click listener for the table
            int selectedRow = jTable1.getSelectedRow();
        if (selectedRow != -1) { // If a row is selected
            Borrower selectedBorrower = null;
            if (currentLoggedInLender != null && currentLoggedInLender.getAssociatedBorrowers() != null) {
                try {
                    selectedBorrower = currentLoggedInLender.getAssociatedBorrowers().get(selectedRow); // Gets the selected borrower object
                } catch (IndexOutOfBoundsException e) {
                    JOptionPane.showMessageDialog(this, "Error: Could not retrieve selected participant details.", "Data Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (selectedBorrower != null) { // If borrower is found
                // Populates the detail fields on the right panel with borrower details
                nameField.setText(selectedBorrower.getName());
                numberField.setText(selectedBorrower.getPhoneNumber());
                emailField.setText(selectedBorrower.getEmailAddress());
                if (selectedBorrower.hasReceivedPayout()) {
                    dueField.setText("Already Paid Out"); // Displays "Already Paid Out" if payout received
                } else {
                    dueField.setText(selectedBorrower.getNextContributionDueDate() != null ? selectedBorrower.getNextContributionDueDate().toString() : "N/A");
                }

            } else {
                // Clears fields if no borrower is found
                nameField.setText("");
                numberField.setText("");
                emailField.setText("");
                dueField.setText("");
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        //Start new cylce
        startNewCycle(); // Calls the method to start a new cycle
        jLabel8.setText("₱" + String.valueOf(calculateTotalPooledAmount())); // Updates total pooled amount
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        //Search
        String searchTerm = jTextField1.getText(); // Gets the search term from the text field
        refreshTable(searchTerm); // Refreshes the table with the search term
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
        //Clear button
        jTextField1.setText("");
        refreshTable(null);// Refreshes the table to show all records
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        //Add 1 month to time (time advance)
        this.currentClock = Clock.offset(this.currentClock, Duration.ofDays(30)); // Advances simulated clock by 30 days
        setClock(this.currentClock); // Updates the clock and refreshes display
        JOptionPane.showMessageDialog(MainFrame.this, "Simulated time advanced by 30 days.", "Time Advanced", JOptionPane.INFORMATION_MESSAGE);
        
        // Resets hasPaidCurrentMonthContribution for all borrowers associated with the current lender to false
        if (currentLoggedInLender != null && currentLoggedInLender.getAssociatedBorrowers() != null) {
            for (Borrower participant : currentLoggedInLender.getAssociatedBorrowers()) {
                participant.setHasPaidCurrentMonthContribution(false); // Resets status for next month's contribution
            }
        }
        
        // Saves the updated hasPaidCurrentMonthContribution status
        saveLenderBorrowers();

        Borrower recipientToPay = determineNextRecipient(); // Determines if a recipient is due for payout
        if (recipientToPay != null) {
            // Performs the automatic payout for this recipient
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Simulated date advanced. Do you want to automatically pay out " + recipientToPay.getName() + "?\n"
                    + "Their status will be updated to 'Paid Out (Current Cycle)'.",
                    "Automatic Payout Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) { // If confirmed
                recipientToPay.setNextContributionDueDate(LocalDate.MAX); // Marks as paid out
                recipientToPay.setHasReceivedPayout(true); // Marks as having received payout for this cycle
                
                refreshTable(); // Refreshes table to show the 'Paid Out' status
                updateNextRecipientDisplay(); // Re-evaluates and updates the next recipient display
                saveLenderBorrowers(); // Saves changes after payout
                
                JOptionPane.showMessageDialog(this,
                        recipientToPay.getName() + " has been automatically paid out.",
                        "Automatic Payout Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            }   
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        //Add 1 day
        this.currentClock = Clock.offset(this.currentClock, Duration.ofDays(1)); // Advances simulated clock by 1 day
        setClock(this.currentClock); // Updates the clock and refreshes display
        JOptionPane.showMessageDialog(MainFrame.this, "Simulated current date is now 1 day in the future.", "Debug Clock Changed", JOptionPane.INFORMATION_MESSAGE);

        // Checks if a recipient is due for payout, similar to jMenuItem2 (monthly advance)
        Borrower recipientToPay = determineNextRecipient();
        if (recipientToPay != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Simulated date advanced. Do you want to automatically pay out " + recipientToPay.getName() + "?\n"
                    + "Their status will be updated to 'Paid Out (Current Cycle)'.",
                    "Automatic Payout Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) { // If confirmed
                recipientToPay.setNextContributionDueDate(LocalDate.MAX); // Marks as paid out
                recipientToPay.setHasReceivedPayout(true); // Marks as having received payout for this cycle
                
                refreshTable(); // Refreshes table to show the 'Paid Out' status
                updateNextRecipientDisplay(); // Re-evaluates and updates the next recipient display
                saveLenderBorrowers(); // Saves changes after payout
                
                JOptionPane.showMessageDialog(this,
                        recipientToPay.getName() + " has been automatically paid out.",
                        "Automatic Payout Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            }   
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        // Clear All Data (for current lender's borrowers)
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear all data for this lender? This action cannot be undone.", "Confirm Clear Data", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) { // If confirmed
            currentLoggedInLender.getAssociatedBorrowers().clear(); // Clears the list of associated borrowers
            saveLenderBorrowers(); // Saves the empty list to file
            refreshTable(); // Refreshes the table
            jLabel8.setText("₱0.0"); // Resets total pooled amount display
            participantField.setText("0"); // Resets participant count display
            jLabel9.setText("N/A"); // Resets next recipient display
            
            nameField.setText(""); // Clears detail fields
            numberField.setText("");
            emailField.setText("");
            dueField.setText("");
            JOptionPane.showMessageDialog(this, "All data for " + currentLoggedInLender.getUsername() + " has been cleared.", "Data Cleared", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        saveLenderBorrowers(); // Saves borrower data before closing
        System.exit(0); // Exits the program
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        // Action listener for the "Signout" menu item
        // Hide the current MainFrame
        this.dispose(); // Closes the current MainFrame
        
        // Open a new LogIn frame
        LogIn loginFrame = new LogIn();
        loginFrame.setVisible(true);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // TODO add your handling code here:
        // Action listener for the "Lender Account Details" menu item
        AccountFrame accountFrame = new AccountFrame(currentLoggedInLender); // Opens the AccountFrame for the current lender
        accountFrame.setVisible(true);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        FlatLightLaf.setup();

        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ReadFromFile();
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel dueField;
    private javax.swing.JLabel emailField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel nameField;
    private javax.swing.JLabel numberField;
    private javax.swing.JLabel participantField;
    // End of variables declaration//GEN-END:variables
}
