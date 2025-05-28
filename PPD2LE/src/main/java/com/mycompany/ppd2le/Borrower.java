/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ppd2le;

import java.time.LocalDate;

/**
 * The `Borrower` class represents a participant in a lending system, extending the `Person` class.
 * It manages details related to a borrower's contributions and payout status.
 * @author Khloe
 */
public class Borrower extends Person{
    private double regularContributionAmount; // The fixed amount contributed each cycle
    private double totalContributed;          // The sum of all contributions made by this participant
    private boolean hasReceivedPayout;        // True if this participant has already received the pot
    private LocalDate nextContributionDueDate;
    private boolean hasPaidCurrentMonthContribution;// The date when the next contribution is due

    // Constructor for creating a new participant with initial values.
    public Borrower(String name, String phoneNumber, String emailAddress, double regularContributionAmount) {
        super(name, phoneNumber, emailAddress);
        this.regularContributionAmount = regularContributionAmount;
        this.totalContributed = 0;
        this.hasReceivedPayout = false; // Initially, no one has received
        this.nextContributionDueDate = null;
        this.hasPaidCurrentMonthContribution = true; 
    }

    // Constructor for loading existing participant data.
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
    // Returns the regular contribution amount.
    public double getRegularContributionAmount() {
        return regularContributionAmount;
    }

    // Returns the total amount contributed.
    public double getTotalContributed() {
        return totalContributed;
    }

    // Checks if the borrower has received a payout.
    public boolean hasReceivedPayout() {
        return hasReceivedPayout;
    }

    // Returns the next contribution due date.
    public LocalDate getNextContributionDueDate() {
        return nextContributionDueDate;
    }
    
    // Returns whether the current month's contribution has been paid.
    public boolean getHasPaidCurrentMonthContribution() {
        return hasPaidCurrentMonthContribution;
    }

    // --- Setters ---
    // Sets the regular contribution amount.
    public void setRegularContributionAmount(double regularContributionAmount) {
        this.regularContributionAmount = regularContributionAmount;
    }
    
    // Sets the total amount contributed.
    public void setTotalContributed(double totalContribution) {
        this.totalContributed = totalContribution;
    }

    // Method to record a regular contribution, adding it to the total and setting the payment status.
    public void recordContribution() {
        this.totalContributed += this.regularContributionAmount;
        this.hasPaidCurrentMonthContribution = true;
        
    }
    
    // Method to add a specified amount to the total contributed.
    public void addContribution(double amount) {
        if (amount > 0) {
            this.totalContributed += amount;
            this.hasPaidCurrentMonthContribution = true;
        }
    }

    // Method to set that the participant has received their payout.
    public void setHasReceivedPayout(boolean hasReceivedPayout) {
        this.hasReceivedPayout = hasReceivedPayout;
    }

    // Sets the next contribution due date.
    public void setNextContributionDueDate(LocalDate nextContributionDueDate) {
        this.nextContributionDueDate = nextContributionDueDate;
    }
    
    // Sets whether the current month's contribution has been paid.
    public void setHasPaidCurrentMonthContribution(boolean hasPaidCurrentMonthContribution) {
        this.hasPaidCurrentMonthContribution = hasPaidCurrentMonthContribution;
    }

    @Override
    // Returns a string representation of the Borrower object, including personal details and contribution information.
    public String toString() {
        return super.toString() +
               "\n  Regular Contribution: ₱" + String.format("%.2f", regularContributionAmount) +
               "\n  Total Contributed: ₱" + String.format("%.2f", totalContributed) +
               "\n  Has Received Payout: " + (hasReceivedPayout ? "Yes" : "No") +
               "\n  Next Due Date: " + (nextContributionDueDate != null ? nextContributionDueDate.toString() : "N/A");
    }
    
}