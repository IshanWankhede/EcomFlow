package com.ecomflow;

import com.ecomflow.gui.MainApp;
import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  EcomFlow — Smart E-Commerce Management System  ");
        System.out.println("=================================================");
        System.out.println("Launching JavaFX Graphical User Interface...");
        
        Application.launch(MainApp.class, args);
    }
}
