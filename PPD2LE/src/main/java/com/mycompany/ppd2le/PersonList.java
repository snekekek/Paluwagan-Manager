/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ppd2le;

/**
 *
 * @author Khloe
 */
import java.util.ArrayList;
import java.util.List;

public class PersonList {
    private ArrayList<Person> personList;

    public PersonList() {
        this.personList = new ArrayList<>();
    }

    // Add a Person to the list
    public void addPerson(Person person) {
        this.personList.add(person);
    }

    // Add a Lender to the list
     public void addLender(Lender lender) {
        this.personList.add(lender);
    }

    // Add a Borrower to the list
    public void addBorrower(Borrower borrower) {
        this.personList.add(borrower);
    }

    // Search for a Person by name
    public Person searchPerson(String name) {
        for (Person person : this.personList) {
            if (person.getName().equalsIgnoreCase(name)) {
                return person;
            }
        }
        return null;
    }

    // Retrieve a Person from the list
        public Person retrievePerson(Person person) {
        for (Person p : this.personList) {
            if (p.equals(person)) {
                return p;
            }
        }
        return null;
    }

     // Search for Persons by a search term
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

    // Get a list of all Persons
    public ArrayList<Person> listPersons() {
        return this.personList;
    }

     // Get a list of all Lenders
    public List<Lender> listLenders() {
        List<Lender> lenderList = new ArrayList<>();
        for (Person person : this.personList) {
            if (person instanceof Lender) {
                lenderList.add((Lender) person);
            }
        }
        return lenderList;
    }

    // Get a list of all Borrowers
    public List<Borrower> listBorrowers() {
        List<Borrower> borrowerList = new ArrayList<>();
        for (Person person : this.personList) {
            if (person instanceof Borrower) {
                borrowerList.add((Borrower) person);
            }
        }
        return borrowerList;
    }

    // Search for a Person by index
    
    public void deletePerson(Person p) {
        personList.remove(p);
    }


    // Delete a Person from the list by index
    public void deletePerson(int index) {
        if (index >= 0 && index < this.personList.size()) {
            this.personList.remove(index);
        } else {
            System.out.println("Invalid index.");
        }
    }

    // Edit a Person in the list by index
    public void editPerson(int index, Person newPerson) {
        if (index >= 0 && index < this.personList.size()) {
            this.personList.set(index, newPerson);
        } else {
            System.out.println("Invalid index.");
        }
    }
    
    public boolean updateBorrower(String originalName, Borrower updatedBorrower) {
        for (int i = 0; i < personList.size(); i++) {
            Person p = personList.get(i);
            if (p instanceof Borrower) {
                Borrower existingBorrower = (Borrower) p;
                if (existingBorrower.getName().equalsIgnoreCase(originalName)) {
                    
                    existingBorrower.setName(updatedBorrower.getName());
                    existingBorrower.setPhoneNumber(updatedBorrower.getPhoneNumber());
                    existingBorrower.setEmailAddress(updatedBorrower.getEmailAddress());
                    existingBorrower.setRegularContributionAmount(updatedBorrower.getRegularContributionAmount());
                    existingBorrower.setTotalContributed(updatedBorrower.getTotalContributed());
                    existingBorrower.setHasReceivedPayout(updatedBorrower.hasReceivedPayout());
                    return true;
                }
            }
        }
        return false; // Borrower not found
    }

    // Convert the list to a string
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.personList.size(); i++) {
            sb.append("Person [").append(i + 1).append("] ").append(this.personList.get(i)).append("\n");
        }
        return sb.toString();
    }

     public String toStringToFile() {
        StringBuilder sb = new StringBuilder();
        for (Person person : this.personList) {
            if (person instanceof Lender) {
                Lender lender = (Lender) person;
                sb.append("LENDER,")
                  .append(lender.getName()).append(",")
                  .append(lender.getPhoneNumber()).append(",")
                  .append(lender.getEmailAddress()).append("\n"); 
            }
            else if (person instanceof Borrower) {
                Borrower borrower = (Borrower) person;
                sb.append("BORROWER,")
                  .append(borrower.getName()).append(",")
                  .append(borrower.getPhoneNumber()).append(",")
                  .append(borrower.getEmailAddress()).append(",")
                  .append(borrower.getRegularContributionAmount()).append(",")
                  .append(borrower.getTotalContributed()).append(",")
                  .append(borrower.hasReceivedPayout())
                  .append("\n");
            }
            else {
                sb.append("PERSON,")
                  .append(person.getName()).append(",")
                  .append(person.getPhoneNumber()).append(",")
                  .append(person.getEmailAddress()).append("\n");
            }
        }
        return sb.toString();
    }
}

