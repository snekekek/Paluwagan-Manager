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
       
    

    public Person(Person p) {
        this.name = p.name;
        this.phoneNumber = p.phoneNumber;
        this.emailAddress = p.emailAddress;

   
    }

    public void setName(String n) {name = n;}
    public void setPhoneNumber (String p) {phoneNumber = p;}
    public void setEmailAddress(String e) {emailAddress = e;}
    //public void setUsername(String u) {username = u;}
    //public void setPasscode(String pw) {passcode = pw;}


    public Person getPerson() {return this;}
    public String getName() {return name;}
    public String getPhoneNumber() {return phoneNumber;}
    public String getEmailAddress() {return emailAddress;}
   //public String getUsername() {return username;}
    //public String getPasscode() {return passcode;}
    

    public void setPerson(String n, String p, String e, String pw) {
        name = n;
        phoneNumber = p;
        emailAddress = e;

        
    }
        
    public boolean equals(Person P) {
        return name.equalsIgnoreCase(P.name) && phoneNumber.equalsIgnoreCase(P.phoneNumber) && emailAddress.equalsIgnoreCase(P.emailAddress);
    }



    public String toString() {return (name + " " + phoneNumber + " " + emailAddress + " ");}

}
