package myalgorithm;

import myalgorithm.galatea.Attribute;
import myalgorithm.galatea.Context;
import myalgorithm.galatea.Entity;
import myalgorithm.galatea.io.FcaFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseTextFiles {


    private String path;
    private String fileName;
    protected Context context;
    private Map<String, List<String>> map = new HashMap<>();
    private boolean symmetric;
    private boolean functional;
    private boolean inverseFunctional;
    private boolean reflexive;
    private boolean irreflexive;

    public ParseTextFiles(String path, String fileName, Context context) {
        this.path = path;
        this.fileName = fileName;
        this.context = context;
        this.functional = false;
        this.inverseFunctional = false;
        this.reflexive = false;
        this.irreflexive = false;
    }

    private boolean oneEntry(Map<String, Integer> test){
        for(Integer i : test.values()){
            if(i > 1){
                return false;
            }
        }
        return true;
    }



    public void parse() throws IOException {
        BufferedReader txtReader = new BufferedReader(new FileReader(path));

        List<String[]> entries = new ArrayList<>();
        String line;
        while ((line = txtReader.readLine()) != null) {
            String[] pair = line.split("[-;]");
            entries.add(pair);
        }

        int count = 0;
        int count1 = 0;

        Map<String, Integer> ftest = new HashMap<>();
        Map<String, Integer> iftest = new HashMap<>();

        for(String[] entry : entries) {
            Entity e = context.getEntity(entry[0]);
            if(ftest.containsKey(entry[0])){
                int a = ftest.get(entry[0]);
                ftest.remove(entry[0]);
                ftest.put(entry[0], a + 1);
            }
            else{
                ftest.put(entry[0], 1);
            }
            if(entry.length == 2){
                count++;
            }
            for(int i = 1; i < entry.length; i++){
                Attribute at = FcaFactory.createAttribute(fileName + "," + entry[i]);
                if(e != null){
                    context.getAttributes().add(at);
                    context.addPair(e, at);
                }
                if(map.containsKey(entry[0])){
                    List<String> a = map.get(entry[0]);
                    map.remove(entry[0]);
                    a.add(entry[i]);
                    map.put(entry[0], a);
                }
                else{
                    List<String> newl = new ArrayList<>();
                    newl.add(entry[i]);
                    map.put(entry[0], newl);
                }
                if(iftest.containsKey(entry[i])){
                    int a = iftest.get(entry[i]);
                    iftest.remove(entry[i]);
                    iftest.put(entry[i], a + 1);
                }
                else{
                    iftest.put(entry[i], 1);
                }
                if(entry[i].equals(entry[0])){
                    count1++;
                }
            }

        }

        if(count == entries.size()){
            if(oneEntry(ftest)) {
                functional = true;
            }
            if(oneEntry(iftest)){
                inverseFunctional = true;
            }
        }
        if(count1 == entries.size()){
            reflexive = true;
        }
        if(count1 == 0){
            irreflexive = true;
        }

        for(String key : map.keySet()){
            for(String res : map.get(key)){
                if(map.containsKey(res) && map.get(res).contains(key)){
                    symmetric = true;
                    break;
                }
            }
        }



        txtReader.close();
    }


    public boolean isSymmetric() {
        return symmetric;
    }

    public boolean isFunctional() {
        return functional;
    }

    public boolean isInverseFunctional() {
        return inverseFunctional;
    }

    public boolean isReflexive() {
        return reflexive;
    }

    public boolean isIrreflexive() {
        return irreflexive;
    }

    public Context getContext() {
        return context;
    }
}
