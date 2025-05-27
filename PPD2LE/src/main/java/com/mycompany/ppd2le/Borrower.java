/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ppd2le;

import java.time.LocalDate;

/**
 *
 * @author Khloe
 */
public class Borrower extends Person{
    private double regularContributionAmount; // The fixed amount contributed each cycle
    private double totalContributed;          // The sum of all contributions made by this participant
    private boolean hasReceivedPayout;        // True if this participant has already received the pot
    private LocalDate nextContributionDueDate;
    private boolean hasPaidCurrentMonthContribution;// The date when the next contribution is due

    // Constructor for creating a new participant with initial values
    public Borrower(String name, String phoneNumber, String emailAddress, double regularContributionAmount) {
        super(name, phoneNumber, emailAddress);
        this.regularContributionAmount = regularContributionAmount;
        this.totalContributed = 0;
        this.hasReceivedPayout = false; // Initially, no one has received
        this.nextContributionDueDate = null;
        this.hasPaidCurrentMonthContribution = false;
    }

    // Constructor for loading existing participant data
    public Borrower(String name, String phoneNumber, String emailAddress,
                       double regularContributionAmount, double totalContributed,
                       boolean hasReceivedPayout, LocalDate nextContributionDueDate, boolean hasPaidCurrentMonthContribution) {
        super(name, phoneNumber, emailAddress);
        this.regularContributionAmount = regularContributionAmount;
        this.totalContributed = totalContributed;
        this.hasReceivedPayout = hasReceivedPayout;
        this.nextContributionDueDate = nextContributionDueDate;
        this.hasPaidCurrentMonthContribution = hasPaidCurrentMonthContribution;
    }

    // --- Getters ---
    public double getRegularContributionAmount() {
        return regularContributionAmount;
    }

    public double getTotalContributed() {
        return totalContributed;
    }

    public boolean hasReceivedPayout() {
        return hasReceivedPayout;
    }

    public LocalDate getNextContributionDueDate() {
        return nextContributionDueDate;
    }
    
    public boolean getHasPaidCurrentMonthContribution() {
        return hasPaidCurrentMonthContribution;
    }

    // --- Setters ---
    public void setRegularContributionAmount(double regularContributionAmount) {
        this.regularContributionAmount = regularContributionAmount;
    }
    
    public void setTotalContributed(double totalContribution) {
        this.totalContributed = totalContribution;
    }

    // Method to record a contribution
    public void recordContribution() {
        this.totalContributed += this.regularContributionAmount;
        
    }

    // Method to set that the participant has received their payout
    public void setHasReceivedPayout(boolean hasReceivedPayout) {
        this.hasReceivedPayout = hasReceivedPayout;
    }

    public void setNextContributionDueDate(LocalDate nextContributionDueDate) {
        this.nextContributionDueDate = nextContributionDueDate;
    }
    
    public void setHasPaidCurrentMonthContribution(boolean hasPaidCurrentMonthContribution) {
        this.hasPaidCurrentMonthContribution = hasPaidCurrentMonthContribution;
    }

    @Override
    public String toString() {
        return super.toString() +
               "\n  Regular Contribution: ₱" + String.format("%.2f", regularContributionAmount) +
               "\n  Total Contributed: ₱" + String.format("%.2f", totalContributed) +
               "\n  Has Received Payout: " + (hasReceivedPayout ? "Yes" : "No") +
               "\n  Next Due Date: " + (nextContributionDueDate != null ? nextContributionDueDate.toString() : "N/A");
    }
    
}
