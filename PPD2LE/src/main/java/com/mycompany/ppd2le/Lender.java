/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ppd2le;

import java.util.ArrayList;
import java.util.List;

/**
 * The `Lender` class represents a lender in a system, extending the `Person` class.
 * It includes attributes specific to a lender, such as a username, password,
 * and a list of associated borrowers.
 * @author Khloe
 */
public class Lender extends Person{
    private String username, password;
    private ArrayList<Borrower> associatedBorrowers;

    // Constructor that initializes a Lender object from a Person object.
    Lender(Person p) {
        super();
        this.username=password="";
        this.associatedBorrowers = new ArrayList<>();
    }
    
    // Constructor that initializes a Lender object with name, phone number, and email address.
    Lender(String a, String b, String c) {
        super(a,b,c);
        this.associatedBorrowers = new ArrayList<>();
    }

    // Constructor that initializes a Lender object with name, phone number, email address, username, and password.
    Lender (String f, String p, String e, String u, String pw) {
        super(f,p, e);
        this.username = u;
        this.password = pw;
        this.associatedBorrowers = new ArrayList<>();
    }

    // Constructor that initializes a Lender object from a Person object, username, and password.
    Lender (Person p, String u, String pw) {
        super(p);
        this.username = u;
        this.password = pw;
        this.associatedBorrowers = new ArrayList<>();
    }
    
    // Returns the username of the lender.
    public String getUsername() {
        return username;
    }
    
    // Returns the password of the lender.
    public String getPassword() {
        return password;
    }
    
    // Returns the password of the lender.
    public String getPasswordHash() { 
        return password;
    }
    
    // Sets the username of the lender.
    public void setUsername(String u) {
        username = u;
    }
    
    // Sets the password of the lender.
    public void setPassword(String pw) {
        password = pw;
    }
    
    // Adds an associated borrower to the lender's list.
    public void addAssociatedBorrower(Borrower borrower) {
        if (this.associatedBorrowers == null) {
            this.associatedBorrowers = new ArrayList<>(); // Ensure initialization if null
        }
        this.associatedBorrowers.add(borrower);
    }

    // Returns the list of associated borrowers.
    public List<Borrower> getAssociatedBorrowers() {
        if (this.associatedBorrowers == null) {
            this.associatedBorrowers = new ArrayList<>(); // Ensure initialization if null
        }
        return associatedBorrowers;
    }
    
    // Sets the list of associated borrowers.
    public void setAssociatedBorrowers(ArrayList<Borrower> associatedBorrowers) {
        this.associatedBorrowers = associatedBorrowers;
    }

   
    // Returns a string representation of the Lender object, including superclass information.
    public String toString() {
        return super.toString() + "\n" + username + "\n" + password;
    }
    
}