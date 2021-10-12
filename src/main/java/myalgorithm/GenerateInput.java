package myalgorithm;


import au.com.bytecode.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GenerateInput {

    protected int nextInputFileIndex(File f){
        if(!f.isDirectory()){
            return -1;
        }
        int count = -1;
        File[] files = f.listFiles();
        for(File g : files){
            if(g.isDirectory()){
                int number = 0;
                String name = g.getName();
                for(int i = 0; i < name.length(); i++){
                    if(Character.isDigit(name.charAt(i))){
                        number = number * 10 + Integer.parseInt(String.valueOf(name.charAt(i)));
                    }
                }
                if(number > count){
                    count = number;
                }
            }
        }
        return count + 1;
    }



    public void generate(int cnt, Map<String, Integer> achoices, Map<String, Integer> rchoices, List<String> secondchoices) throws IOException{

        File f = new File("src/main/java/myalgorithm/examples/random/");
        int a = nextInputFileIndex(f);

        String pathToCsv = "src/main/java/myalgorithm/examples/random/family/input/names_gender.csv";
        List<String> files = new ArrayList<>();
        files.add("hasSibling");
        files.add("hasChild");
        files.add("marriedTo");
        CSVReader csvReader = new CSVReader(new FileReader(pathToCsv));
        String [] nextLine;

        File f2 = new File("src/main/java/myalgorithm/examples/random/family" + a);
        f2.mkdir();
        FileWriter csvWriter1 = new FileWriter("src/main/java/myalgorithm/examples/random/family" + a + "/classes.csv");
        FileWriter csvWriter2 = new FileWriter("src/main/java/myalgorithm/examples/random/family" + a + "/attributes.csv");
        List<FileWriter> fileWriters = new ArrayList<>();
        Map<String, FileWriter> fws = new HashMap<>();
        for(String s : files){
            FileWriter fw = new FileWriter("src/main/java/myalgorithm/examples/random/family" + a + "/" + s + ".txt", false);
            fileWriters.add(fw);
            fws.put(s, fw);
        }

        csvWriter1.append(";");
        csvWriter1.append("person");
        for(String s : achoices.keySet()){
            csvWriter1.append(";");
            csvWriter1.append(s);
        }
        csvWriter1.append("\n");

        int co = 0;
        int[] counts = new int[achoices.keySet().size()];
        Arrays.fill(counts, 0);
        String aux = null;
        Map<String, String> people_classes = new HashMap<>();
        while ((nextLine = csvReader.readNext()) != null)
        {
            for(String token : nextLine)
            {
                if(token.length() > 1 && Character.isUpperCase(token.charAt(0))){
                    aux = token;
                }
                else{
                    if(token.length() == 1 && Character.isUpperCase(token.charAt(0))){
                        if(!people_classes.containsKey(aux) && co < cnt) {
                            if(token.charAt(0) == 'F' && counts[0] < achoices.get("female")) {
                                people_classes.put(aux, token);
                                co++;
                                counts[0]++;
                            }
                            if(token.charAt(0) == 'M' && counts[1] < achoices.get("male")) {
                                people_classes.put(aux, token);
                                co++;
                                counts[1]++;
                            }
                        }
                    }
                }
            }
        }

        Random rand = new Random();

        Map<String, String> marriedTo = new HashMap<>();
        Map<String, List<String>> hasSibling = new HashMap<>();
        Map<String, List<String>> hasChild = new HashMap<>();

        Set<String> keys = people_classes.keySet();
        List<Integer> chosen1 = new ArrayList<>();
        List<String> keyList = new ArrayList<>(keys);
            int count = rchoices.getOrDefault("marriedTo", 0);
            for(int i = 0; i < count; i++){
            int rand_int1;
            do{
                rand_int1 = rand.nextInt(keys.size());
            }while(chosen1.contains(rand_int1));
            int rand_int2;
            do{
                rand_int2 = rand.nextInt(keys.size());
            }while(rand_int1 == rand_int2 || chosen1.contains(rand_int2));

            marriedTo.put(keyList.get(rand_int1), keyList.get(rand_int2));
            marriedTo.put(keyList.get(rand_int2), keyList.get(rand_int1));

            chosen1.add(rand_int1);
            chosen1.add(rand_int2);

        }

        List<Integer> chosen2 = new ArrayList<>();

        int count2 = rchoices.getOrDefault("hasSibling", 0);
            for(int i = 0; i < count2; i++){
            //how many siblings --> up to 5
            int rand_int0 = rand.nextInt(2) + 1;
            int rand_int1;
            do{
                rand_int1 = rand.nextInt(keys.size());
            }while(chosen2.contains(rand_int1));
            chosen2.add(rand_int1);
            List<Integer> chosen_aux = new ArrayList<>();
            List<String> siblings = new ArrayList<>();
            for(int j = 0; j < rand_int0; j++){
                int rand_int2;
                do{
                    rand_int2 = rand.nextInt(keys.size());
                }while(chosen_aux.contains(rand_int2) || (marriedTo.keySet().contains(keyList.get(rand_int1)) && marriedTo.get(keyList.get(rand_int1)).equals(keyList.get(rand_int2))));
                siblings.add(keyList.get(rand_int2));
                chosen_aux.add(rand_int2);
                chosen2.add(rand_int2);
            }
            hasSibling.put(keyList.get(rand_int1), siblings);

        }

        int count3 = rchoices.getOrDefault("hasChild", 0);
            for(int i = 0; i < count3; i++){
            int rand_int0 = rand.nextInt(2) + 1;
            int rand_int1;
            do{
                rand_int1 = rand.nextInt(keys.size());
            }while(chosen2.contains(rand_int1));
            chosen2.add(rand_int1);
            List<Integer> chosen_aux = new ArrayList<>();
            List<String> children = new ArrayList<>();
            for(int j = 0; j < rand_int0; j++){
                int rand_int2;
                do{
                    rand_int2 = rand.nextInt(keys.size());
                }while(chosen_aux.contains(rand_int2) || (marriedTo.containsKey(keyList.get(rand_int1)) && marriedTo.get(keyList.get(rand_int1)).equals(keyList.get(rand_int2))) || (hasSibling.containsKey(keyList.get(rand_int1)) && hasSibling.get(keyList.get(rand_int1)).contains(keyList.get(rand_int2))));
                children.add(keyList.get(rand_int2));
                chosen_aux.add(rand_int2);
            }
            hasChild.put(keyList.get(rand_int1), children);
        }

            if(rchoices.keySet().contains("marriedTo")){
                for(String s : marriedTo.keySet()) {
                    fws.get("marriedTo").append(s + "-" + marriedTo.get(s) + "\n");
                }
            }
            for(String s : hasSibling.keySet()){
                fws.get("hasSibling").append(s + "-");
            for(String si : hasSibling.get(s)){
                fws.get("hasSibling").append(si + ";");
            }
            fws.get("hasSibling").append("\n");
            for(String si : hasSibling.get(s)){
                fws.get("hasSibling").append(si + "-");
                fws.get("hasSibling").append(s + ";");
                for(String sii : hasSibling.get(s)){
                    if(!sii.equals(si)){
                        fws.get("hasSibling").append(sii + ";");
                    }
                }
                fws.get("hasSibling").append("\n");
            }
        }

            for(String s : hasChild.keySet()){
                fws.get("hasChild").append(s + "-");
            for(String ch : hasChild.get(s)){
                fws.get("hasChild").append(ch + ";");
            }
            fws.get("hasChild").append("\n");
        }

            for(String s : keys){
            csvWriter1.append(s);
            csvWriter1.append(";X;");

            if(people_classes.get(s).equals("F")){
                csvWriter1.append("X; ");
            }
            else{
                csvWriter1.append(" ;X");
            }
            csvWriter1.append("\n");

        }
            for(String str : secondchoices){
            csvWriter2.append(";");
            csvWriter2.append(str);
        }
        if(!secondchoices.isEmpty()) {
            csvWriter2.append("\n");
            for (String s : keys) {
                int rand_int = rand.nextInt(secondchoices.size());
                csvWriter2.append(s);
                for (int i = 0; i < rand_int; i++) {
                    csvWriter2.append("; ");
                }
                csvWriter2.append(";X");
                for (int i = 0; i < secondchoices.size() - rand_int - 1; i++) {
                    csvWriter2.append("; ");
                }
                csvWriter2.append("\n");
            }
        }


            csvReader.close();
            csvWriter1.flush();
            csvWriter2.flush();
            for(FileWriter fw : fileWriters){
                fw.flush();
            }
            csvWriter1.close();
            csvWriter2.close();
            for(FileWriter fw : fileWriters){
                fw.close();
            }


    }

}
