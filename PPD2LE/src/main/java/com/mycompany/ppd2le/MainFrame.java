/*;
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.ppd2le;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Khloe
 */
public class MainFrame extends javax.swing.JFrame {
    

    public static PersonList PArray = new PersonList();
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        refreshTable();
    }
    
    public void refreshTable() {
    // Updated columns to reflect paluwagan context
        String columns[] = {"Name", "Contribution per Cycle", "Total Contributed", "Payout Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        // Assuming PArray is a PersonList instance and contains Participant objects
        if (MainFrame.PArray != null && MainFrame.PArray.listPersons() != null) { //
            for (Person p : MainFrame.PArray.listPersons()) { //
                // Check if the person is a Participant (formerly Borrower)
                if (p instanceof Borrower) {
                    Borrower participant = (Borrower) p; // Cast to Participant

                    Object[] rowData = {
                        participant.getName(), //
                        String.format("₱%.2f", participant.getRegularContributionAmount()), // Format as currency
                        String.format("₱%.2f", participant.getTotalContributed()), // Format as currency
                        participant.hasReceivedPayout() ? "Received" : "Pending" // Display Payout Status
                    };
                    model.addRow(rowData);
                }
            }

            jTable1.setModel(model);

            // Center renderers for numeric/status columns
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer(); //
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER); //

            // I Center column 2,3,4
            jTable1.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Contribution per Cycle
            jTable1.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Total Contributed
            jTable1.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Payout Status

            // Update the participant count
            if (MainFrame.PArray.listBorrowers() == null || MainFrame.PArray.listBorrowers().isEmpty()) { //
                participantField.setText("0"); //
            } else {

                participantField.setText(String.valueOf(MainFrame.PArray.listBorrowers().size())); 
            }
        }
        
        updateNextRecipientDisplay();
    }
    
    private Borrower determineNextRecipient() {
        Borrower nextRecipient = null;
        LocalDate earliestDueDate = null;

        if (MainFrame.PArray != null && MainFrame.PArray.listPersons() != null) {
            for (Person p : MainFrame.PArray.listPersons()) {
                if (p instanceof Borrower) {
                    Borrower participant = (Borrower) p;
                    // Only consider participants who haven't received a payout
                    if (!participant.hasReceivedPayout()) { //
                        if (nextRecipient == null) {
                            nextRecipient = participant;
                            earliestDueDate = participant.getNextContributionDueDate(); //
                        } else {
                            // If comparing by due date:
                            if (participant.getNextContributionDueDate() != null &&
                                (earliestDueDate == null || participant.getNextContributionDueDate().isBefore(earliestDueDate))) { //
                                nextRecipient = participant;
                                earliestDueDate = participant.getNextContributionDueDate(); //
                            }
                            // Add a tie-breaker if due dates are the same (e.g., by name)
                            else if (participant.getNextContributionDueDate() != null &&
                                     earliestDueDate != null &&
                                     participant.getNextContributionDueDate().isEqual(earliestDueDate)) { //
                                // Example tie-breaker: alphabetical order
                                if (participant.getName().compareToIgnoreCase(nextRecipient.getName()) < 0) {
                                    nextRecipient = participant;
                                }
                            }
                        }
                    }
                }
            }
        }
        return nextRecipient;
    }
    
    private void updateNextRecipientDisplay() {
        Borrower nextRecipient = determineNextRecipient();
        if (nextRecipient != null) {
            jLabel9.setText(nextRecipient.getName()); 
        } else {
            jLabel9.setText("N/A (All received or no participants)");
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
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu5 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

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
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Name", "Contribution per Cycle", "Total Contributed", "Payout Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true
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

        jButton3.setText("Add Payment");
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
        jLabel9.setText("NAMEHERE");

        participantField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        participantField.setText("0");

        numberField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        nameField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        emailField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        dueField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jMenu1.setText("Switch Mode");

        jMenu5.setText("Borrower Mode");
        jMenu1.add(jMenu5);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Signout");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jTextField1)
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
                                .addGap(40, 40, 40)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(jButton5))))
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
                                .addGap(16, 16, 16)
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
                    .addComponent(jButton6))
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
                // Check if they are already "paid out" for the current cycle
                if (retrievedBorrower.getNextContributionDueDate() != null &&
                    retrievedBorrower.getNextContributionDueDate().isAfter(LocalDate.now().plusYears(50))) {
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

                    retrievedBorrower.setNextContributionDueDate(LocalDate.MAX); 



                    refreshTable(); 
                    

                    JOptionPane.showMessageDialog(this,
                            retrievedBorrower.getName() + " has received an early payout and is removed from the current cycle.",
                            "Early Payout Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } 
            
                else {
                    JOptionPane.showMessageDialog(this, "Error: Could not retrieve borrower details for " + selectedBorrowerName, "Error", JOptionPane.ERROR_MESSAGE);
                }
        } 
        
            else {
                JOptionPane.showMessageDialog(this, "Please select a participant to give an early payout.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            }
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        
        
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

                // --- For setting the due date: ---
                // This depends on how you store/manage due dates for borrowers.
                // 1. If Borrower has a 'dueDate' field (e.g., of type String or java.util.Date):
                //    You would retrieve it and set it.
                //    Example:
                //    if (yourDueDateField != null) {
                //        yourDueDateField.setText(retrievedBorrower.getDueDate().toString()); // Assuming getDueDate()
                //    }

                // 2. If you don't have a dueDate field on Borrower yet, you'd add one:
                //    Example in Borrower.java: private java.time.LocalDate dueDate; // Or java.util.Date
                //    Add getter/setter methods for it.

                // 3. To set a *new* due date or open a dialog for it:
                //    You could open a new dialog here, passing 'retrievedBorrower' to it
                //    so the dialog can update the specific borrower's due date.
                //    Example:
                //    DueDateDialog dialog = new DueDateDialog(this, retrievedBorrower);
                //    dialog.setVisible(true);

                // For now, I'll print to console for demonstration purposes:
                System.out.println("Selected Borrower Details:");
                System.out.println("Name: " + retrievedBorrower.getName());
                System.out.println("Email: " + retrievedBorrower.getEmailAddress());
                System.out.println("Phone Number: " + retrievedBorrower.getPhoneNumber());
                // System.out.println("Current Due Date: " + (retrievedBorrower.getDueDate() != null ? retrievedBorrower.getDueDate().toString() : "Not set"));

            } else {
                System.out.println("Error: Could not retrieve borrower details for " + selectedBorrowerName);
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

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
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenuBar jMenuBar1;
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
