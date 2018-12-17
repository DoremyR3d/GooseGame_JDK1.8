package com.dreamy.kata;

import com.dreamy.kata.controller.GameController;

import java.util.Scanner;

public class Application {

    private static volatile boolean turningOff = false;

    public static void main(String[] args) {
        System.out.println("Initializing Goose Game");
        GameController controller = new GameController();
        Scanner scanner = new Scanner(System.in);
        while(!turningOff) {
            String command = scanner.nextLine();
            System.out.println(controller.parseCommand(command));
        }
    }

    public static void turnOff() {
        turningOff = true;
    }
}
