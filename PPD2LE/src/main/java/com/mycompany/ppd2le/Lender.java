/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ppd2le;

/**
 *
 * @author Khloe
 */
public class Lender extends Person{
    private double liquidCash;

    Lender(Person p) {
        super();
        liquidCash = 0;
    }

    Lender (String f, String p, String e, double c) {
        super(f,p, e);
        liquidCash = c;
    }

    Lender (Person p, double c) {
        super(p);
        liquidCash = c;
    }

    public void setLiquidCash (double c) {liquidCash = c;}

    public double getEmpType () {return liquidCash;}

    public String toString() {
        return super.toString() + "\n" + liquidCash;
    }
    
}
