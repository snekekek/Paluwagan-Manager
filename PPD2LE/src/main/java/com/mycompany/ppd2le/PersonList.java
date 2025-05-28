/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ppd2le;

/**
 * The `PersonList` class manages a collection of `Person` objects,
 * including `Lender` and `Borrower` subclasses. It provides functionalities
 * for adding, searching, deleting, editing, and saving/loading person data
 * to/from CSV files.
 * @author Khloe
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class PersonList {
    private ArrayList<Person> personList;

    // Constructor to initialize an empty PersonList.
    public PersonList() {
        this.personList = new ArrayList<>();
    }

    // Add a Person to the list.
    public void addPerson(Person person) {
        this.personList.add(person);
    }

    // Add a Lender to the list.
     public void addLender(Lender lender) {
        this.personList.add(lender);
    }

    // Add a Borrower to the list.
    public void addBorrower(Borrower borrower) {
        this.personList.add(borrower);
    }

    // Search for a Person by name (case-insensitive).
    public Person searchPerson(String name) {
        for (Person person : this.personList) {
            if (person.getName().equalsIgnoreCase(name)) {
                return person;
            }
        }
        return null;
    }
    
    // Search in a list of borrowers based on a search term (case-insensitive, by name).
    public List<Borrower> searchBorrowers(String searchTerm) {
        List<Borrower> results = new ArrayList<>();
        String lowerSearchTerm = searchTerm.toLowerCase();
        for (Person person : this.personList) {
            if (person instanceof Borrower) {
                Borrower borrower = (Borrower) person;
                if (borrower.getName().toLowerCase().contains(lowerSearchTerm)) { // add paid status, contribution, email, phonenumber
                    results.add(borrower);
                }
            }
        }
        return results;
    }
    
    // Search for a Borrower by name (case-insensitive).
    public Borrower searchBorrowerByName(String name) {
        for (Person p : personList) {
            if (p instanceof Borrower && p.getName().equalsIgnoreCase(name)) {
                return (Borrower) p;
            }
        }
        return null;
    }
    
    // Search for a Lender by username (case-insensitive).
    public Lender searchLenderByUsername(String username) {
        for (Person person : this.personList) {
            if (person instanceof Lender) {
                Lender lender = (Lender) person;
                if (lender.getUsername().equalsIgnoreCase(username)) {
                    return lender;
                }
            }
        }
        return null; // Lender not found
    }
    
    // Clears all persons from the list.
    public void clearAllPersons() {
        this.personList.clear();
    }

    // Retrieve a Person from the list based on equality.
        public Person retrievePerson(Person person) {
        for (Person p : this.personList) {
            if (p.equals(person)) {
                return p;
            }
        }
        return null;
    }

     // Search for Persons by a search term (case-insensitive, by name).
    public List<Person> searchPersons(String searchTerm) {
        List<Person> results = new ArrayList<>();
        String lowerSearchTerm = searchTerm.toLowerCase();
        for (Person person : this.personList) {
            if (person.getName().toLowerCase().contains(lowerSearchTerm)) {
                results.add(person);
            }
        }
        return results;
    }

    // Get a list of all Persons.
    public ArrayList<Person> listPersons() {
        return this.personList;
    }

     // Get a list of all Lenders.
    public List<Lender> listLenders() {
        List<Lender> lenderList = new ArrayList<>();
        for (Person person : this.personList) {
            if (person instanceof Lender) {
                lenderList.add((Lender) person);
            }
        }
        return lenderList;
    }

    // Get a list of all Borrowers.
    public List<Borrower> listBorrowers() {
        List<Borrower> borrowerList = new ArrayList<>();
        for (Person person : this.personList) {
            if (person instanceof Borrower) {
                borrowerList.add((Borrower) person);
            }
        }
        return borrowerList;
    }

    // Delete a Person from the list by object reference.
    public void deletePerson(Person p) {
        personList.remove(p);
    }


    // Delete a Person from the list by index.
    public void deletePerson(int index) {
        if (index >= 0 && index < this.personList.size()) {
            this.personList.remove(index);
        } else {
            System.out.println("Invalid index.");
        }
    }

    // Edit a Person in the list by index with a new Person object.
    public void editPerson(int index, Person newPerson) {
        if (index >= 0 && index < this.personList.size()) {
            this.personList.set(index, newPerson);
        } else {
            System.out.println("Invalid index.");
        }
    }
    
    // Updates a Borrower in the list based on their original name.
    public boolean updateBorrower(String originalName, Borrower updatedBorrower) {
        for (int i = 0; i < personList.size(); i++) {
            Person p = personList.get(i);
            if (p instanceof Borrower) {
                Borrower existingBorrower = (Borrower) p;
                if (existingBorrower.getName().equalsIgnoreCase(originalName)) {
                    personList.set(i, updatedBorrower);
                    return true;
                }
            }
        }
        return false;
    }
    
    // Reads Borrower data from a specified file and returns an ArrayList of Borrower objects.
    public static ArrayList<Borrower> readBorrowersFromFile(String filename) {
        ArrayList<Borrower> borrowers = new ArrayList<>();
        java.io.File file = new java.io.File(filename); // Use java.io.File to avoid conflict

        if (!file.exists()) {
            System.out.println("Borrower data file " + filename + " not found. Starting with empty borrower data for this lender.");
            return borrowers; // Return empty list if file doesn't exist
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split by comma outside quotes

                // A Borrower entry is assumed to have 8 parts as per its constructor
                if (parts.length >= 8) {
                    try {
                        String name = unescapeCsv(parts[0]).trim();
                        String phoneNumber = unescapeCsv(parts[1]).trim();
                        String emailAddress = unescapeCsv(parts[2]).trim();
                        double regularContributionAmount = Double.parseDouble(unescapeCsv(parts[3]).trim());
                        double totalContributed = Double.parseDouble(unescapeCsv(parts[4]).trim());
                        boolean hasReceivedPayout = Boolean.parseBoolean(unescapeCsv(parts[5]).trim());
                        
                        // Handle LocalDate parsing, allowing "null" string
                        String nextDueDateString = unescapeCsv(parts[6]).trim();
                        LocalDate nextContributionDueDate = null;
                        if (!nextDueDateString.equalsIgnoreCase("null") && !nextDueDateString.isEmpty()) {
                            nextContributionDueDate = LocalDate.parse(nextDueDateString);
                        }
                        
                        boolean hasPaidCurrentMonthContribution = Boolean.parseBoolean(unescapeCsv(parts[7]).trim());

                        Borrower borrower = new Borrower(name, phoneNumber, emailAddress,
                                                         regularContributionAmount, totalContributed, 
                                                         hasReceivedPayout, nextContributionDueDate, 
                                                         hasPaidCurrentMonthContribution);
                        borrowers.add(borrower);
                    } catch (NumberFormatException | DateTimeParseException e) {
                        System.err.println("Error parsing Borrower data in line: " + line + " - " + e.getMessage());
                    } catch (Exception e) {
                         System.err.println("An unexpected error occurred while parsing borrower line: " + line + " - " + e.getMessage());
                         e.printStackTrace(); // For debugging
                    }
                } else {
                    System.err.println("Skipping malformed BORROWER line (less than 8 parts): " + line);
                }
            }
        } catch (FileNotFoundException e) {
            // This block will be reached if the file doesn't exist, even after initial check,
            // but the `if (!file.exists())` handles it cleanly before `try-with-resources`.
            System.out.println("Borrower data file " + filename + " not found (during try-catch).");
        } catch (IOException e) {
            System.err.println("Error reading from borrower file " + filename + ": " + e.getMessage());
        }
        return borrowers;
    }

    // Saves Lender data from the personList to a specified CSV file.
    public void saveToCsv(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            for (Person person : this.personList) {
                if (person instanceof Lender) {
                    Lender lender = (Lender) person;
                    // Format: name,phoneNumber,emailAddress,username,passwordHash
                    String line = escapeCsv(lender.getName()) + "," +
                                  escapeCsv(lender.getPhoneNumber()) + "," +
                                  escapeCsv(lender.getEmailAddress()) + "," +
                                  escapeCsv(lender.getUsername()) + "," +
                                  escapeCsv(lender.getPasswordHash()) + "\n"; // Use getPasswordHash()
                    writer.append(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing to file " + filename + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
     
    // Escapes a string for CSV format by enclosing in quotes and doubling internal quotes if necessary.
    public static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        // If the value contains comma, double quote, or newline, enclose in double quotes
        // and escape any existing double quotes by doubling them
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    // Unescapes a string from CSV format by removing surrounding quotes and un-doubling internal quotes.
    public static String unescapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.length() > 1 && value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
            value = value.replace("\"\"", "\"");
        }
        return value;
    }

    // New static method to save only a list of Borrowers to a specific CSV file.
    public static void saveBorrowersToCsv(String filename, List<Borrower> borrowers) {
        try (FileWriter writer = new FileWriter(filename)) {
            // CSV Header for Borrowers
            writer.append("NAME,PHONE_NUMBER,EMAIL_ADDRESS,REGULAR_CONTRIBUTION_AMOUNT,TOTAL_CONTRIBUTED,HAS_RECEIVED_PAYOUT,NEXT_CONTRIBUTION_DUE_DATE,HAS_PAID_CURRENT_MONTH_CONTRIBUTION\n");

            for (Borrower borrower : borrowers) {
                String nextDueDateString = (borrower.getNextContributionDueDate() != null) ?
                                           (borrower.getNextContributionDueDate().isEqual(LocalDate.MAX) ? "MAX_DATE" : borrower.getNextContributionDueDate().toString()) :
                                           "N/A";
                writer.append(escapeCsv(borrower.getName())).append(",")
                      .append(escapeCsv(borrower.getPhoneNumber())).append(",")
                      .append(escapeCsv(borrower.getEmailAddress())).append(",")
                      .append(String.format("%.2f", borrower.getRegularContributionAmount())).append(",")
                      .append(String.format("%.2f", borrower.getTotalContributed())).append(",")
                      .append(String.valueOf(borrower.hasReceivedPayout())).append(",")
                      .append(escapeCsv(nextDueDateString)).append(",")
                      .append(String.valueOf(borrower.getHasPaidCurrentMonthContribution()))
                      .append("\n");
            }
            System.out.println("Borrower data successfully saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving borrower data to CSV file: " + e.getMessage());
        }
    }

    // New static method to read only a list of Borrowers from a specific CSV file.
    public static ArrayList<Borrower> readBorrowersFromCsv(String filename) {
        ArrayList<Borrower> borrowers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isFirstLine = true; // Flag to skip the header

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip the header row
                    continue;
                }

                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (parts.length >= 8) { // Expecting at least 8 parts for a Borrower
                    try {
                        String name = unescapeCsv(parts[0]).trim();
                        String phoneNumber = unescapeCsv(parts[1]).trim();
                        String emailAddress = unescapeCsv(parts[2]).trim();
                        double regularContributionAmount = Double.parseDouble(unescapeCsv(parts[3]).trim());
                        double totalContributed = Double.parseDouble(unescapeCsv(parts[4]).trim());
                        boolean hasReceivedPayout = Boolean.parseBoolean(unescapeCsv(parts[5]).trim());
                        
                        LocalDate nextContributionDueDate;
                        String nextDueDateString = unescapeCsv(parts[6]).trim();
                        if (nextDueDateString.equalsIgnoreCase("MAX_DATE")) {
                            nextContributionDueDate = LocalDate.MAX;
                        } else if (nextDueDateString.equalsIgnoreCase("N/A") || nextDueDateString.isEmpty()) {
                            nextContributionDueDate = null;
                        } else {
                            nextContributionDueDate = LocalDate.parse(nextDueDateString);
                        }
                        
                        boolean hasPaidCurrentMonthContribution = Boolean.parseBoolean(unescapeCsv(parts[7]).trim());

                        Borrower borrower = new Borrower(name, phoneNumber, emailAddress,
                                                         regularContributionAmount, totalContributed, 
                                                         hasReceivedPayout, nextContributionDueDate, 
                                                         hasPaidCurrentMonthContribution);
                        borrowers.add(borrower);
                    } catch (NumberFormatException | DateTimeParseException e) {
                        System.err.println("Error parsing Borrower data in line: " + line + " - " + e.getMessage());
                    }
                } else {
                    System.err.println("Skipping malformed BORROWER line (less than 8 parts): " + line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Borrower data file " + filename + " not found. Starting with empty borrower data for this lender.");
        } catch (IOException e) {
            System.err.println("Error reading from borrower file " + filename + ": " + e.getMessage());
        }
        return borrowers;
    }
}