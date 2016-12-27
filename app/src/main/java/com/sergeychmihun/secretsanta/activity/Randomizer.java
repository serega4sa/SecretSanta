package com.sergeychmihun.secretsanta.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Sergey.Chmihun on 12/26/2016.
 */
public class Randomizer {
    private int [] result;
    private String[] names;
    private int count = 0;
    private ArrayList<ReceiverInfo> members;
    private HashMap<ReceiverInfo, ReceiverInfo> results = new HashMap<>();

    public HashMap<ReceiverInfo, ReceiverInfo> getResults() {
        return results;
    }

    Randomizer(ArrayList<ReceiverInfo> members) {
        this.members = new ArrayList<>(members);
        result = new int [this.members.size()];
        names = new String [this.members.size()];
    }

    /** Build list of names and start random engine */
    public void run(){
        for (int i = 0; i < members.size(); i++) {
            names[i] = members.get(i).getName();
        }

        randomEngine();
    }

    /** Launch randomizing engine that generates random number from determined range and assign it to person */
    public void randomEngine(){
        ArrayList<Integer> list = new ArrayList<>(members.size());
        for(int i = 1; i <= members.size(); i++) {
            list.add(i);
        }

        Random rand = new Random();

        while(list.size() > 0) {
            for (int i = 0; i < members.size(); i++){
                int index = rand.nextInt(list.size());

                result[i] = list.remove(index);
                System.out.println(result[i]);
            }
        }
        check();
    }

    /** Checking that person doesn't get himself as Secret Santa */
    public void check(){
        for (int i = 0; i < members.size(); i++) {
            if (result[i] != (i + 1)) {
                count++;
            }
        }

        if (count == members.size()){
            writeResults();
        } else {
            System.out.println("Repeated numbers, initializing new process...");
            count = 0;
            randomEngine();
        }
    }

    /** Build map where key is receiver, value is his Secret Santa */
    public void writeResults() {
        for (int i = 0; i < members.size(); i++) {
            results.put(members.get(i), members.get(result[i] - 1));
        }
    }
}
