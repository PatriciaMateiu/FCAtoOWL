package myalgorithm;

import au.com.bytecode.opencsv.CSVReader;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.util.List;
import java.util.*;

public class GUI extends JFrame implements ActionListener, ItemListener{

        private JComboBox jcb, jcb2, jcb3, jcb4;
        private JRadioButton s,l, s1, l1;
        private JLabel label0, label1, label2, label3, label4, jl, jl2, jl3, jl4, jl5, jl6, jl7, jl8, jl10, jl11, jl12;
        private JButton process_data, generate_lattice, create_ontology, reset, check, query, exit, generate_input, submit, submit2, submit3, submit4, OK, done, add, select, upload, generate_random, queryb;
        private JFrame jFrame, choiceFrame, newF, qF;
        private JTextField t1, t2, t3, t4, t5, t6;
        private RCA_algorithm rca_algorithm;
        private GenerateInput gi;
        private String category, name;
        private List<String> chosen = new ArrayList<>();
        private Map<String, String> aux = new HashMap<>();
        private JPanel jPanel, choicePanel;
        private String PD, GL, CO;
        private int nrPeople;
        private Map<String, Integer> achoices = new HashMap<>();
        private List<String> secondchoices = new ArrayList<>();
        private Map<String, Integer> rchoices = new HashMap<>();
        private JFileChooser fc;
        private JFrame nf2;
        private JFrame nf;
        private JPanel jp2;
        private List<JFrame> lattices = new ArrayList<>();
        private JTextArea jta;

    public GUI(){
            this.rca_algorithm = new RCA_algorithm();
            this.gi = new GenerateInput();
            jPanel = new JPanel();
            jPanel.setLayout(new FlowLayout());
            jPanel.setBackground(Color.LIGHT_GRAY);
            JScrollPane scrollPane = new JScrollPane(jPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            jta = new JTextArea();
            add(jPanel);
            process_data = new JButton("Process data");
            s = new JRadioButton("predefined");
            l = new JRadioButton("random");
            ButtonGroup bg = new ButtonGroup();
            bg.add(s);
            bg.add(l);
            generate_lattice = new JButton("Generate lattice");
            create_ontology = new JButton("Create ontology");
            label0 = new JLabel("Select the input category");
            label1 = new JLabel();
            label1.setSize(400, 100);
            label2 = new JLabel();
            label2.setSize(400, 100);
            label3 = new JLabel();
            label3.setSize(400, 100);
            label4 = new JLabel();
            label4.setSize(400, 100);
            reset = new JButton("Reset");
            exit = new JButton("EXIT");
            queryb = new JButton("Query");
            exit.setForeground(Color.red);
            upload = new JButton("Upload");
            upload.setForeground(Color.blue);
            submit = new JButton("Submit");
            submit2 = new JButton("Submit");
            submit3 = new JButton("Submit");
            submit4 = new JButton("Submit");
            generate_random = new JButton("Generate random input");
            OK = new JButton("OK");
            done = new JButton("done");
            add = new JButton("add");
            generate_input = new JButton("Generate input");
            select = new JButton("Select");
            fc = new JFileChooser();
            fc.setMultiSelectionEnabled(true);
            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            t1 = new JTextField(5);
            t2 = new JTextField(5);
            t3 = new JTextField(5);
            check = new JButton("Check execution time");
            query = new JButton("Query an ontology");
            PD = "";
            GL = "";
            CO = "";
            newF = new JFrame();
            qF = new JFrame();

            jcb = new JComboBox();
            jcb2 = new JComboBox();


            process_data.addActionListener(this);
            generate_lattice.addActionListener(this);
            create_ontology.addActionListener(this);
            reset.addActionListener(this);
            check.addActionListener(this);
            query.addActionListener(this);
            s.addActionListener(this);
            l.addActionListener(this);
            exit.addActionListener(this);
            generate_input.addActionListener(this);
            submit.addActionListener(this);
            submit2.addActionListener(this);
            submit3.addActionListener(this);
            submit4.addActionListener(this);
            OK.addActionListener(this);
            done.addActionListener(this);
            add.addActionListener(this);
            select.addActionListener(this);
            upload.addActionListener(this);
            generate_random.addActionListener(this);
            queryb.addActionListener(this);

            jPanel.add(reset);
            jPanel.add(label0);
            jPanel.add(s);
            jPanel.add(l);
            jPanel.add(label2);
            jPanel.add(label1);
            jPanel.add(jcb);
            jcb.setVisible(false);
            jPanel.add(label3);
            jPanel.add(generate_random);
            jPanel.add(process_data);
            jPanel.add(generate_lattice);
            jPanel.add(create_ontology);
            process_data.setEnabled(false);
            generate_lattice.setEnabled(false);
            create_ontology.setEnabled(false);
            jPanel.add(upload);
            jPanel.add(reset);
            jPanel.add(exit);
            jPanel.add(check);
            jPanel.add(query);
            jPanel.add(label4);
            jPanel.add(jcb2);
            jcb2.setVisible(false);
            check.setEnabled(false);
            List<String> res = new ArrayList<>();
            getFiles(new File("src/main/java/myalgorithm/examples/"), res);
            for (String s : res) {
                jcb2.addItem(s);
            }
    }

    private JTable generateTable(String file){
        int index = 0;
        List<String[]> r;
        String[][] table = null;

        String[] columns = null;

        int rows = 0;
        int cols = 0;

        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            r = reader.readAll();
            if(!r.isEmpty()) {
                rows = r.size() - 1;
                cols = r.get(0).length;
                table = new String[rows][cols];

                columns = r.get(0)[0].split(";");

                for (int i = 1; i < r.size(); i++) {
                    String[] elems = r.get(i)[0].split(";");
                    table[index++] = elems;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(table != null) {

            JTable jTable = new JTable(table, columns);

            TableColumnModel columnModel = jTable.getColumnModel();

            for (int j = 0; j < columnModel.getColumnCount(); j++) {
                if (table[0][j].length() == 1) {
                    columnModel.getColumn(j).setMinWidth(50);
                    columnModel.getColumn(j).setMaxWidth(600 / columnModel.getColumnCount() + 10);
                    columnModel.getColumn(j).setPreferredWidth(50);
                } else {
                    columnModel.getColumn(j).setMinWidth(120);
                    columnModel.getColumn(j).setMaxWidth(120);
                    columnModel.getColumn(j).setPreferredWidth(100);
                }
            }

            jTable.setFillsViewportHeight(true);

            return jTable;
        }
        return null;
    }

    private void getFiles(File f, List<String> res){
        if(!f.isDirectory()){
            if(f.getPath().contains(".owl") && f.getPath().contains("ontology/")) {
                res.add(f.getPath());
            }
        }
        else{
            Arrays.stream(Objects.requireNonNull(f.listFiles())).forEach(fi -> getFiles(fi, res));
        }
    }


    @Override
    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();
        String files[];
        List<List<String>> data = new ArrayList<>();
        long start, finish, timeElapsed;

        if(action.equals("Reset")){
            System.out.println("Reset");
            for(JFrame lat : lattices){
                if(lat.isShowing()) {
                    lat.dispose();
                }
            }
            if(newF.isShowing()) {
                newF.dispose();
            }
            if(qF.isShowing()){
                qF.dispose();
            }
            dispose();
            createAndShowGUI();
        }

        if(action.equals("EXIT")){

            this.dispose();
            System.exit(0);
        }

        if(action.equals("Upload")) {
            nf2 = new JFrame();
            jp2 = new JPanel();
            nf2.add(jp2);
            t5 = new JTextField(10);
            JButton set_directory = new JButton("Set directory");
            set_directory.addActionListener(this);
            jl11 = new JLabel("Choose a name for the new input directory");
            jp2.add(jl11);
            jp2.add(t5);
            jp2.add(set_directory);
            nf2.pack();
            nf2.setSize(300, 100);
            nf2.setVisible(true);
        }
        if(action.equals("Set directory")){
            String name = t5.getText();
            File fi = new File("src/main/java/myalgorithm/examples/predefined/" + name);
            if(!fi.exists()){
                fi.mkdir();
            }
            nf2.dispose();
            nf = new JFrame();
            JPanel jp = new JPanel();
            nf.add(jp);
            jp.add(fc);
            nf.setVisible(true);
            nf.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            FileInputStream in = null;
            int x = fc.showOpenDialog(null);
            if (x == JFileChooser.APPROVE_OPTION) {
                File[] ff = fc.getSelectedFiles();
                FileOutputStream out = null;

                for (File f : ff) {
                    File filex = new File("src/main/java/myalgorithm/examples/predefined/" + name + "/" + f.getName());
                    try {
                        in = new FileInputStream(f);
                        out = new FileOutputStream(filex);
                        int n;
                        while ((n = in.read()) != -1) {
                            out.write(n);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    System.out.println("File Copied");

                }
            }
            nf.dispose();
        }

        if(action.equals("predefined")|| action.equals("random")) {
            category = action;
            String selected = category + " category was selected";
            label2.setForeground(Color.DARK_GRAY);
            label2.setText(selected);
            jPanel.remove(generate_random);
            jPanel.remove(label0);
            jPanel.remove(s);
            jPanel.remove(l);
            jPanel.remove(upload);
            File file = new File("src/main/java/myalgorithm/examples/" + category + "/");
            String[] directories = file.list(new FilenameFilter() {
                @Override
                public boolean accept(File current, String name) {
                    return new File(current, name).isDirectory();
                }
            });
            label1.setText("Select an input directory");
            for(String d : directories){
                jcb.addItem(d);
            }
            jcb.setVisible(true);
            process_data.setEnabled(true);
        }

        if(action.equals("Generate random input")){
            choiceFrame = new JFrame();
            choicePanel = new JPanel();
            t1.setText("");
            jl = new JLabel("Choose the total number of individuals");
            choicePanel.add(jl);
            choicePanel.add(t1);
            choicePanel.add(submit);
            choiceFrame.add(choicePanel);
            choiceFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            choiceFrame.pack();
            choiceFrame.setSize(520, 400);
            choiceFrame.setVisible(true);
        }
        if(ae.getSource() == submit) {
            nrPeople = Integer.parseInt(t1.getText());
            choicePanel.remove(t1);
            choicePanel.remove(submit);
            jl.setText(nrPeople + " people in the input file");
            jl.setForeground(Color.gray);
            jl2 = new JLabel("Choose a category");
            choicePanel.add(jl2);
            jcb3 = new JComboBox();
            jcb3.addItem("female");
            jcb3.addItem("male");
            choicePanel.add(jcb3);
            t2.setText("");
            jl3 = new JLabel("Choose how many from this category");
            choicePanel.add(jl3);
            choicePanel.add(t2);
            choicePanel.add(submit2);
        }

        if(ae.getSource() == submit2){
            int nrGender = Integer.parseInt(t2.getText());
            int nrF, nrM;
            if(jcb3.getItemAt(jcb3.getSelectedIndex()).toString().equals("female")){
                nrF = nrGender;
                nrM = nrPeople - nrF;
            }
            else{
                nrM = nrGender;
                nrF = nrPeople - nrM;
            }
            choicePanel.remove(jcb3);
            choicePanel.remove(jl2);
            choicePanel.remove(jl3);
            choicePanel.remove(t2);
            choicePanel.remove(jl);
            jl4 = new JLabel("The file will include " + nrF + " female inidivuals and " + nrM + " male individuals");
            choicePanel.add(jl4);
            choicePanel.remove(submit2);
            achoices.put("female", nrF);
            achoices.put("male", nrM);

            jl8 = new JLabel("Choose the attributes for a second lattice (suggested : jobs)");
            choicePanel.add(jl8);
            choicePanel.add(t3);
            choicePanel.add(add);
            jl10 = new JLabel("Press OK once you have finished adding the attributes:");
            choicePanel.add(OK);
            choiceFrame.pack();
            choiceFrame.setSize(520, 400);


        }
        if(ae.getSource() == add){
            String s = t3.getText();
            secondchoices.add(s);
            t3.setText("");
        }
        if(ae.getSource() == OK){
            choicePanel.remove(jl8);
            choicePanel.remove(t3);
            choicePanel.remove(jl10);
            choicePanel.remove(add);
            choicePanel.remove(OK);
            jl5 = new JLabel("Choose an object property");
            choicePanel.add(jl5);
            jcb4 = new JComboBox();
            jcb4.addItem("hasSibling");
            jcb4.addItem("hasChild");
            jcb4.addItem("marriedTo");
            choicePanel.add(jcb4);
            jl6 = new JLabel("Choose the number of objects related to this object property");
            t4 = new JTextField(5);
            choicePanel.add(jl6);
            t4.setText("");
            choicePanel.add(t4);
            choicePanel.add(submit3);
            choicePanel.add(done);
            jl7 = new JLabel("Press done when you finished adding all the needed object properties");
            choicePanel.add(jl7);
            choiceFrame.pack();
            choiceFrame.setSize(520, 400);

        }
        if(ae.getSource() == submit3){
            String selected = jcb4.getItemAt(jcb4.getSelectedIndex()).toString();
            int nr = Integer.parseInt(t4.getText());
            t4.setText("");
            jcb4.removeItem(selected);
            rchoices.put(selected, nr);

        }
        if(ae.getSource() == done){
            choiceFrame.dispose();
            try {
                gi.generate(nrPeople, achoices, rchoices, secondchoices);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //process_data.setEnabled(true);
            String infoMessage = "You have generated a family input directory, which will be found at " + "\n" + "src/main/java/myalgorithm/examples/random/family" + (gi.nextInputFileIndex(new File("src/main/java/myalgorithm/examples/random/")) - 1) + " ." + "\n" + "To continue the algorithm, choose this input directory and press button 'Process data' ";
            JOptionPane.showMessageDialog(null, infoMessage, "Success: input generation", JOptionPane.INFORMATION_MESSAGE);


        }
        if (action.equals("Process data")) {
            start = System.currentTimeMillis();
            System.out.println("Processing data...");

            name = jcb.getItemAt(jcb.getSelectedIndex()).toString();

            String selected = " & " + name + " category was selected";
            jPanel.remove(label1);
            jPanel.remove(jcb);
            label3.setForeground(Color.DARK_GRAY);
            label3.setText(selected);
            JPanel newPanel = new JPanel();
            newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.Y_AXIS));
            File f = new File("src/main/java/myalgorithm/examples/" + category + "/" + name + "/");
            files = f.list();
            String mainp = new String();
            String ff = new String();
            BufferedReader br = null, bra = null;
            BufferedWriter bw = null;
            for(String fle : files) {
                if (fle.contains("classes.csv")) {
                    String path = "src/main/java/myalgorithm/examples/" + category + "/" + name + "/" + fle;
                    mainp = path;
                    ff = fle;
                    chosen.add(mainp);

                    JTable jTable = generateTable(path);
                    JScrollPane sp = new JScrollPane(jTable);
                    sp.setPreferredSize(new Dimension(900, 300));
                    newPanel.add(new JLabel(fle));
                    newPanel.add(sp, BorderLayout.CENTER);
                }
            }

            for(String fle : files) {
                    if (!fle.contains(name) && fle.contains(".csv") && !fle.contains("classes")) {
                        String path = "src/main/java/myalgorithm/examples/" + category + "/" + name + "/" + fle;
                        boolean flg = false;
                        for (int i = path.lastIndexOf('/') + 1; i < path.length(); i++) {
                            char ch = path.charAt(i);
                            if (Character.isUpperCase(ch)) {
                                flg = true;
                            }
                        }

                        if (!flg) {
                            chosen.add(path);
                        } else {
                            File auxF = new File("src/main/java/myalgorithm/examples/" + category + "/" + name + "/" + fle);
                            List<String> auxData = new ArrayList<>();
                            try {
                                bra = new BufferedReader(new InputStreamReader(new FileInputStream(auxF)));
                                String line = null;
                                for (line = bra.readLine(); line != null; line = bra.readLine()) {
                                    auxData.add(line);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (!auxData.isEmpty()) {
                                data.add(auxData);
                            }
                        }
                        JTable jTable = generateTable(path);
                        if(jTable != null) {
                            JScrollPane sp = new JScrollPane(jTable);
                            sp.setPreferredSize(new Dimension(900, 300));
                            newPanel.add(new JLabel(fle));
                            newPanel.add(sp, BorderLayout.CENTER);
                        }
                    } else if (category.equals("random") && fle.contains(".txt")) {
                        String sub = fle.substring(0, fle.length() - 4);
                        aux.put(sub, "src/main/java/myalgorithm/examples/" + category + "/" + name + "/" + fle);
                    }
            }

            try {
                File oldF = new File("src/main/java/myalgorithm/examples/" + category + "/" + name + "/", ff);
                br = new BufferedReader(new InputStreamReader(new FileInputStream(oldF)));



                List<String> range = new ArrayList<>();
                Map<String, FileWriter> mapp = new HashMap<>();
                String s = br.readLine();
                for (List<String> list : data) {
                    String firstElem = list.get(0);
                    String[] elems = firstElem.split(";");
                    FileWriter fileWriter = new FileWriter("src/main/java/myalgorithm/examples/" + category + "/" + name + "/" + elems[0] + ".txt");
                    mapp.put(elems[0], fileWriter);
                    for (int i = 1; i < elems.length; i++) {
                        range.add(elems[0]);
                    }
                }
                    for (List<String> list : data) {
                        String firstElem = list.get(0);
                        String[] felems = firstElem.split(";");
                        for (int index = 1; index < list.size(); index++) {
                            String elem = list.get(index);
                            String[] elems = elem.split(";");
                            FileWriter fw = mapp.get(felems[0]);
                            for (int i = 1; i < elems.length; i++) {
                                if (elems[i].equals("X")) {
                                    fw.append(elems[0] + "-" + felems[i] + "\n");
                                }
                            }
                    }
                }

                for(String str : mapp.keySet()){
                    FileWriter fileWriter = mapp.get(str);
                    aux.put(str, "src/main/java/myalgorithm/examples/" + category + "/" + name + "/" + str + ".txt");
                    fileWriter.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            finish = System.currentTimeMillis();
            timeElapsed = finish - start;
            PD += "Data processed in " + (double) timeElapsed/1000 + " seconds";
            System.out.println(PD);

            process_data.setEnabled(false);
            generate_lattice.setEnabled(true);
            check.setEnabled(true);
            JScrollPane scrollBar = new JScrollPane(newPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            newF.add(scrollBar, BorderLayout.CENTER);
            newF.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            newF.pack();
            newF.setSize(900,600);
            newF.setVisible(true);

        }

        if (action.equals("Generate lattice")) {
            start = System.currentTimeMillis();
            System.out.println("Generating lattice...");
            try {
                rca_algorithm.generateGraphLattice(chosen, aux,"classes.csv");
                this.lattices = rca_algorithm.getLattices();
                for(JFrame lat : lattices){
                    lat.setVisible(true);
                }
                finish = System.currentTimeMillis();
                timeElapsed = finish - start;
                GL += "Lattice generated in " + (double) timeElapsed/1000 + " seconds";
                System.out.println(GL);

                generate_lattice.setEnabled(false);
                create_ontology.setEnabled(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (action.equals("Create ontology")) {
            start = System.currentTimeMillis();
            System.out.println("Creating ontology...");
            try {
                rca_algorithm.createOntology(category + "/" +  name, name);
                finish = System.currentTimeMillis();
                timeElapsed = finish - start;
                CO += "Ontology created in " + (double) timeElapsed/1000 + " seconds\n";
                System.out.println(CO);
                create_ontology.setEnabled(false);
            } catch (OWLOntologyCreationException | OWLOntologyStorageException | InterruptedException e) {
                e.printStackTrace();
            }
            String infoMessage = "The ontology file was successfully created! You can find it in examples/" + category + "/" + name + "/" + "ontology";
            JOptionPane.showMessageDialog(null, infoMessage, "Success: ontology", JOptionPane.INFORMATION_MESSAGE);
        }

        if(action.equals("Check execution time")){
            String infoMessage = PD + "\n" + GL + "\n" + CO + "\n";
            JOptionPane.showMessageDialog(null, infoMessage, "Execution time", JOptionPane.INFORMATION_MESSAGE);
        }

        if(action.equals("Query an ontology")) {
            label4.setText("Select an ontology file to query :");
            jcb2.setVisible(true);
            jPanel.add(select);
            if(qF.isShowing()){
                qF.dispose();
            }
        }
        if(action.equals("Select")){
            jPanel.remove(select);
            jl12 = new JLabel("Please write the query string in the text field");
            jPanel.add(jl12);
            t6 = new JTextField(15);
            jPanel.add(t6);
            jPanel.add(queryb);
            pack();
            setSize(600, 300);
        }
        if(action.equals("Query")){
            String chosen = jcb2.getItemAt(jcb2.getSelectedIndex()).toString();
            String querys = t6.getText();
            String result = "";
            try {
                result = rca_algorithm.queryOntology(chosen, querys);
            } catch (OWLOntologyCreationException | OWLOntologyStorageException e) {
                e.printStackTrace();

            }
            jcb2.setVisible(false);
            JPanel jP = new JPanel();
            qF = new JFrame();
            qF.add(jP);
            JLabel jLabel = new JLabel("Results of the entered query");
            jP.add(jLabel);
            jta = new JTextArea();
            jta.setText(result);
            jta.getCaret().setVisible(false);
            jP.add(jta);
            jPanel.remove(t6);
            jPanel.remove(queryb);
            jPanel.remove(label4);
            jPanel.remove(jl12);
            pack();
            setSize(600, 300);
            t6.setText("");
            qF.pack();
            qF.setSize(500, 500);
            qF.setVisible(true);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == s) {
            if (e.getStateChange() == 1) {
                label2.setText(category + " category was selected");
            }
        }
        else {
            if (e.getStateChange() == 1) {
                label2.setText(category + " category was selected");
            }
        }
    }

    public void createAndShowGUI(){
        jFrame = new GUI();

        jFrame.pack();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(600,300);

        jFrame.setVisible(true);
    }

    public static void main(){


    }

}
