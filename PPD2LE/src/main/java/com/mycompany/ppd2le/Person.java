package com.mycompany.ppd2le;

public class Person {
    private String name, phoneNumber, emailAddress;
    

    public Person() {
        name=phoneNumber=emailAddress="";
    }

    public Person(String n, String p, String e) {
        name = n;
        phoneNumber = p;
        emailAddress = e;

        
    }
       
    // Copy constructor
    public Person(Person p) {
        this.name = p.name;
        this.phoneNumber = p.phoneNumber;
        this.emailAddress = p.emailAddress;

   
    }

    // Sets the name of the person
    public void setName(String n) {name = n;}
    // Sets the phone number of the person
    public void setPhoneNumber (String p) {phoneNumber = p;}
    // Sets the email address of the person
    public void setEmailAddress(String e) {emailAddress = e;}



    // Returns the current Person object
    public Person getPerson() {return this;}
    // Returns the name of the person
    public String getName() {return name;}
    // Returns the phone number of the person
    public String getPhoneNumber() {return phoneNumber;}
    // Returns the email address of the person
    public String getEmailAddress() {return emailAddress;}

    


    public void setPerson(String n, String p, String e) {
        name = n;
        phoneNumber = p;
        emailAddress = e;

        
    }
        
    // Compares if two Person objects are equal based on name, phone number, and email address
    public boolean equals(Person P) {
        return name.equalsIgnoreCase(P.name) && phoneNumber.equalsIgnoreCase(P.phoneNumber) && emailAddress.equalsIgnoreCase(P.emailAddress);
    }


    // Returns a string representation of the Person object
    public String toString() {return (name + " " + phoneNumber + " " + emailAddress + " ");}

}