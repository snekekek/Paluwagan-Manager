/*;
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.ppd2le;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.Color;
import java.awt.Component;
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

/**
 *
 * @author Khloe
 */
public class MainFrame extends javax.swing.JFrame {
    

    public static PersonList PArray = new PersonList();
    private Clock currentClock;
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        this.currentClock = Clock.systemDefaultZone();
        refreshTable();
    }
    
    public void refreshTable() {
        // Call refreshTable with no search term to display all borrowers
        refreshTable(null); 
    }
    
    public void refreshTable(String searchTerm) {
        String columns[] = {"Name", "Contribution this Cycle", "Total Contributed", "Next Payout Date", "Paid Monthly Contribution"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; 
            }
        };

        List<Borrower> borrowersToDisplay;

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            borrowersToDisplay = MainFrame.PArray.listBorrowers();
        } else {
            borrowersToDisplay = MainFrame.PArray.searchBorrowers(searchTerm);
        }

        if (borrowersToDisplay != null) {
            for (Borrower participant : borrowersToDisplay) { 
                

                String nextDueDateStatus;
                if (participant.getNextContributionDueDate() != null && 
                    participant.getNextContributionDueDate().isEqual(LocalDate.MAX)) {
                    nextDueDateStatus = "Paid Out (Current Cycle)"; 
                } 
                    else if (participant.getNextContributionDueDate() != null) {
                        nextDueDateStatus = participant.getNextContributionDueDate().toString();
                } 
                    else {
                        nextDueDateStatus = "N/A (No due date)"; 
                }
                
                String paidStatus = participant.getHasPaidCurrentMonthContribution() ? "PAID" : "NOT PAID"; 

                Object[] rowData = {
                    participant.getName(),
                    String.format("₱%.2f", participant.getRegularContributionAmount()),
                    String.format("₱%.2f", participant.getTotalContributed()),
                    nextDueDateStatus,
                    paidStatus 
                };
                model.addRow(rowData);
            }

            jTable1.setModel(new DefaultTableModel());
            jTable1.setModel(model);

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
                        c.setBackground(Color.GREEN);
                        c.setForeground(Color.BLACK);
                    } else {
                        c.setBackground(Color.RED);
                        c.setForeground(Color.WHITE); 
                    }
                    return c;
                }
            });


            if (MainFrame.PArray.listBorrowers() == null || MainFrame.PArray.listBorrowers().isEmpty()) {
                participantField.setText("0");
            } else {
                participantField.setText(String.valueOf(MainFrame.PArray.listBorrowers().size()));
            }
        }
        updateNextRecipientDisplay();
    }
    
    // In MainFrame.java

    private Borrower determineNextRecipient() {
        Borrower nextRecipient = null;
        LocalDate earliestEligibleDueDate = null; 

        // The simulatedCurrentDate is still useful for logging/context but not for filtering eligible recipients in this logic.
        LocalDate simulatedCurrentDate = LocalDate.now(currentClock);

        System.out.println("\n--- Starting determineNextRecipient (Simulated Date: " + simulatedCurrentDate + ") ---");

        if (MainFrame.PArray != null && MainFrame.PArray.listPersons() != null) {
            for (Person p : MainFrame.PArray.listPersons()) {
                if (p instanceof Borrower) {
                    Borrower participant = (Borrower) p;

                    System.out.println("    Checking participant: " + participant.getName() +
                                       ", Current Due Date: " + participant.getNextContributionDueDate());

                    // Check for eligibility:
                    // 1. Due date must not be null or LocalDate.MAX (meaning not paid out for current cycle)
                    // 2. Has not already received payout in this cycle
                    // Removed the condition: (participant.getNextContributionDueDate().isBefore(simulatedCurrentDate) || participant.getNextContributionDueDate().isEqual(simulatedCurrentDate))
                    if (participant.getNextContributionDueDate() != null &&
                        !participant.getNextContributionDueDate().isEqual(LocalDate.MAX) &&
                        !participant.hasReceivedPayout()) { // Ensure participant has not received payout in current cycle

                        // If this is the first eligible participant found, or
                        // If this participant has an earlier eligible due date than the current earliest, or
                        // If dates are equal, use alphabetical tie-breaker
                        if (earliestEligibleDueDate == null || 
                            participant.getNextContributionDueDate().isBefore(earliestEligibleDueDate)) {

                            nextRecipient = participant;
                            earliestEligibleDueDate = participant.getNextContributionDueDate();
                            System.out.println("        New earliest eligible: " + nextRecipient.getName() + " on " + earliestEligibleDueDate);
                        } else if (participant.getNextContributionDueDate().isEqual(earliestEligibleDueDate)) {
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
        }
        System.out.println("--- End determineNextRecipient. Final nextRecipient: " + (nextRecipient != null ? nextRecipient.getName() : "N/A") + " ---");
        return nextRecipient;
    }
    
    private void updateNextRecipientDisplay() {
        Borrower nextRecipient = determineNextRecipient();
        if (nextRecipient != null) {
            jLabel9.setText(nextRecipient.getName());
        } else {
            jLabel9.setText("N/A");
        }
    }
    
    private void startNewCycle() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to start a new cycle?\n"
            + "This will set due dates for each participant, starting 1 month from now and incrementing monthly.",
            "Confirm New Cycle",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("\n--- Starting New Cycle ---");

            LocalDate currentDueDate = LocalDate.now(currentClock).plusMonths(1);

            if (MainFrame.PArray != null && MainFrame.PArray.listPersons() != null) {
                for (Person p : MainFrame.PArray.listPersons()) {
                    if (p instanceof Borrower) {
                        Borrower participant = (Borrower) p;

                        participant.setNextContributionDueDate(currentDueDate);
                        participant.recordContribution(); // This increments totalContributed

                        // Removed this line: participant.setHasPaidCurrentMonthContribution(false);
                        participant.setHasReceivedPayout(false); // Ensure borrower is eligible for payout in new cycle

                        // Increment for the next participant's initial due date
                        currentDueDate = currentDueDate.plusMonths(1);
                        System.out.println("    Next participant's due date will be: " + currentDueDate);
                    }
                }
            }
            refreshTable();
            JOptionPane.showMessageDialog(this, "New cycle started! Due dates assigned sequentially and contributions recorded.", "New Cycle", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("--- New Cycle Started ---");
        }
    }
    
    private double calculateTotalPooledAmount() {
        double total = 0;
        if (MainFrame.PArray != null && MainFrame.PArray.listPersons() != null) {
            for (Person p : MainFrame.PArray.listPersons()) {
                if (p instanceof Borrower) {
                    Borrower participant = (Borrower) p;
                    total += participant.getRegularContributionAmount();
                }
            }
        }
        return total;
    }
    
    public void setClock(Clock clock) {

        this.currentClock = clock;

        refreshTable(); // Refresh table to show updated status based on new date

        updateNextRecipientDisplay(); // Update recipient display e

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
        jMenu2 = new javax.swing.JMenu();
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

        jMenuBar1.add(jMenu6);

        jMenu2.setText("Signout");
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
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(nameField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(numberField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3))
                                .addGap(38, 38, 38)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(emailField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(dueField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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
        
        int selectedRow = jTable1.getSelectedRow();

            if (selectedRow >= 0) {
                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                String selectedBorrowerName = model.getValueAt(selectedRow, 0).toString();

                Borrower retrievedBorrower = null;
                if (MainFrame.PArray != null && MainFrame.PArray.listPersons() != null) {
                    for (Person p : MainFrame.PArray.listPersons()) {
                        if (p instanceof Borrower) {
                            Borrower b = (Borrower) p;
                            if (b.getName().equals(selectedBorrowerName)) {
                                retrievedBorrower = b;
                                break;
                            }
                        }
                    }
                }

                if (retrievedBorrower != null) {
                    EditPersonFrame editPerson = new EditPersonFrame(this, retrievedBorrower);
                    editPerson.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a participant to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
                }
            } 
            else {
                JOptionPane.showMessageDialog(this, "Please select a participant to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
         int selectedRow = jTable1.getSelectedRow();

        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            String selectedBorrowerName = model.getValueAt(selectedRow, 0).toString();

            Borrower retrievedBorrower = null;
            if (MainFrame.PArray != null && MainFrame.PArray.listPersons() != null) {
                for (Person p : MainFrame.PArray.listPersons()) {
                    if (p instanceof Borrower) {
                        Borrower b = (Borrower) p;
                        if (b.getName().equals(selectedBorrowerName)) {
                            retrievedBorrower = b;
                            break;
                        }
                    }
                }
            }

            if (retrievedBorrower != null) {
                if (retrievedBorrower.getNextContributionDueDate() != null &&
                    retrievedBorrower.getNextContributionDueDate().isEqual(LocalDate.MAX)) { 
                    JOptionPane.showMessageDialog(this,
                            retrievedBorrower.getName() + " has already received an early payout for the current cycle.",
                            "Payout Status",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to give an early payout to " + retrievedBorrower.getName() + "?\n"
                        + "They will be removed from the *current* cycle's recipient selection.",
                        "Confirm Early Payout",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    System.out.println("\n--- Payout Action for: " + retrievedBorrower.getName() + " ---");
                    System.out.println("    Before Payout: " + retrievedBorrower.getName() + "'s Due Date: " + retrievedBorrower.getNextContributionDueDate());
                    
                    retrievedBorrower.setNextContributionDueDate(LocalDate.MAX); 
                    
                    System.out.println("    After Payout: " + retrievedBorrower.getName() + "'s New Due Date: " + retrievedBorrower.getNextContributionDueDate());

                    refreshTable(); 

                    JOptionPane.showMessageDialog(this,
                            retrievedBorrower.getName() + " has received an early payout and is removed from the current cycle.",
                            "Early Payout Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error: Could not retrieve borrower details for " + selectedBorrowerName, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a participant to give an early payout.", "Selection Error", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a borrower from the table to add contribution.", "No Borrower Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String borrowerName = (String) jTable1.getValueAt(selectedRow, 0);
        Borrower selectedBorrower = (Borrower) MainFrame.PArray.searchPerson(borrowerName);

        if (selectedBorrower != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to record a contribution for " + selectedBorrower.getName() + "?",
                "Confirm Contribution",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                selectedBorrower.recordContribution(); // Updates totalContributed and sets hasPaidCurrentMonthContribution to true

                if (selectedBorrower.getNextContributionDueDate() != null) {
                    selectedBorrower.setNextContributionDueDate(selectedBorrower.getNextContributionDueDate().plusMonths(1));
                } else {
                    selectedBorrower.setNextContributionDueDate(LocalDate.now(currentClock).plusMonths(1));
                }

                refreshTable(); // Refresh the entire table's data model

                
                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                int viewRow = jTable1.convertRowIndexToView(selectedRow); 
                if (viewRow != -1) { 
                    model.fireTableRowsUpdated(viewRow, viewRow);
                }

                jLabel8.setText("₱" + String.valueOf(calculateTotalPooledAmount())); // Update pooled amount display
                JOptionPane.showMessageDialog(this, selectedBorrower.getName() + "'s contribution recorded and due date advanced!", "Contribution Successful", JOptionPane.INFORMATION_MESSAGE);
            }
        } 
        
        else {
            JOptionPane.showMessageDialog(this, "Selected borrower not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        
        int selectedRow = jTable1.getSelectedRow();

    
        if (selectedRow >= 0) {
            
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            String selectedBorrowerName = model.getValueAt(selectedRow, 0).toString();

            Borrower retrievedBorrower = null;
            if (MainFrame.PArray != null && MainFrame.PArray.listPersons() != null) {
                for (Person p : MainFrame.PArray.listPersons()) {
                    if (p instanceof Borrower) {
                        Borrower b = (Borrower) p;
                        if (b.getName().equals(selectedBorrowerName)) {
                            retrievedBorrower = b;
                            break;
                        }
                    }
                }
            }

            // If a Borrower object was successfully retrieved
            if (retrievedBorrower != null) {
                
            }
            int confirm = javax.swing.JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + retrievedBorrower.getName() + "?",
                "Confirm Deletion",
                javax.swing.JOptionPane.YES_NO_OPTION);

            if (confirm == javax.swing.JOptionPane.YES_OPTION) {
                MainFrame.PArray.deletePerson(retrievedBorrower);
                

 
                refreshTable();
                nameField.setText("");
                numberField.setText("");
                emailField.setText("");

                javax.swing.JOptionPane.showMessageDialog(this,
                    retrievedBorrower.getName() + " has been deleted.",
                    "Deletion Successful",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
            
            else
                
            {
                System.out.println("Error: Could not retrieve borrower details for " + selectedBorrowerName);
            }
        }
        
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        AddPersonFrame addPerson = new AddPersonFrame(this);
        addPerson.setVisible(true);
        refreshTable();
        
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
            int selectedRow = jTable1.getSelectedRow();

    
        if (selectedRow >= 0) {
            
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            String selectedBorrowerName = model.getValueAt(selectedRow, 0).toString();

            Borrower retrievedBorrower = null;
            if (MainFrame.PArray != null && MainFrame.PArray.listPersons() != null) {
                for (Person p : MainFrame.PArray.listPersons()) {
                    if (p instanceof Borrower) {
                        Borrower b = (Borrower) p;
                        if (b.getName().equals(selectedBorrowerName)) {
                            retrievedBorrower = b;
                            break;
                        }
                    }
                }
            }

            // If a Borrower object was successfully retrieved
            if (retrievedBorrower != null) {
                
                nameField.setText(retrievedBorrower.getName());
                numberField.setText(retrievedBorrower.getPhoneNumber());
                emailField.setText(retrievedBorrower.getEmailAddress());
                dueField.setText(String.valueOf(retrievedBorrower.getNextContributionDueDate()));

            } else {
                System.out.println("Error: Could not retrieve borrower details for " + selectedBorrowerName);
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        startNewCycle();
       jLabel8.setText("₱" + String.valueOf(calculateTotalPooledAmount()));
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        String searchTerm = jTextField1.getText();
        refreshTable(searchTerm);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
        jTextField1.setText("");
        refreshTable(null);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        this.currentClock = Clock.offset(this.currentClock, Duration.ofDays(30)); // Advance simulated clock by 30 days
        setClock(this.currentClock); // This will call refreshTable() and updateNextRecipientDisplay()
        JOptionPane.showMessageDialog(MainFrame.this, "Simulated current date is now approximately 1 month in the future.", "Debug Clock Changed", JOptionPane.INFORMATION_MESSAGE);
        
        // Reset hasPaidCurrentMonthContribution for all borrowers to false
        if (MainFrame.PArray != null && MainFrame.PArray.listPersons() != null) {
            for (Person p : MainFrame.PArray.listPersons()) {
                if (p instanceof Borrower) {
                    Borrower participant = (Borrower) p;
                    participant.setHasPaidCurrentMonthContribution(false); // Reset status
                }
            }
        }
        
        Borrower recipientToPay = determineNextRecipient();
        if (recipientToPay != null) {
            // Perform the automatic payout for this recipient
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Simulated date advanced. Do you want to automatically pay out " + recipientToPay.getName() + "?\n"
                    + "Their status will be updated to 'Paid Out (Current Cycle)'.",
                    "Automatic Payout Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                recipientToPay.setNextContributionDueDate(LocalDate.MAX); // Mark as paid out
                recipientToPay.setHasReceivedPayout(true); // Mark as having received payout for this cycle
                refreshTable(); // Refresh table to show the 'Paid Out' status
                updateNextRecipientDisplay(); // Re-evaluate and update the next recipient display
                JOptionPane.showMessageDialog(this,
                        recipientToPay.getName() + " has been automatically paid out.",
                        "Automatic Payout Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } 
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        this.currentClock = Clock.offset(this.currentClock, Duration.ofDays(1)); // Advance simulated clock by 1 day
        setClock(this.currentClock); // This will call refreshTable() and updateNextRecipientDisplay()
        JOptionPane.showMessageDialog(MainFrame.this, "Simulated current date is now 1 day in the future.", "Debug Clock Changed", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

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
