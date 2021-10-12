package myalgorithm;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import myalgorithm.galatea.*;
import myalgorithm.galatea.composite.Facet;
import myalgorithm.galatea.io.ParseCSVContext;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.ManchesterSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class RCA_algorithm {

    private List<ConceptLattice> conceptLattices = new ArrayList<>();
    private ConceptLattice mainLattice;
    private List<Context> contexts = new ArrayList<>();
    private Context mainContext;
    Map<Facet, List<String>> objProp = new HashMap<>();
    Map<Facet, RelationalAttribute> relationalAttributeHashMap= new HashMap<>();
    private List<JFrame> lattices = new ArrayList<>();
    private Map<String, Integer> ent = new HashMap<>();
    private List<String> symmetric = new ArrayList<>();
    private List<String> asymmetric = new ArrayList<>();
    private List<String> functional = new ArrayList<>();
    private List<String> inverseFunctional = new ArrayList<>();
    private List<String> reflexive = new ArrayList<>();
    private List<String> irreflexive = new ArrayList<>();

    private ShortFormProvider shortFormProvider;
    private OWLOntology o;
    private OWLOntologyManager manager;
    private OWLReasoner reasoner;
    private Context processData(String path) throws IOException {

        ParseCSVContext parseCSVContext = new ParseCSVContext(path);
        parseCSVContext.parse();
        Context context = parseCSVContext.getContext();
        return context;
    }

    private Context processTextFile(String path, String name, Context context) throws IOException {
        ParseTextFiles parseTextFiles = new ParseTextFiles(path, name, context);
        parseTextFiles.parse();
        if(parseTextFiles.isSymmetric()){
            symmetric.add(name);
        }
        if(parseTextFiles.isFunctional()){
            functional.add(name);
        }

        if(parseTextFiles.isInverseFunctional()){
            inverseFunctional.add(name);
        }

        if(parseTextFiles.isReflexive()){
            reflexive.add(name);
        }

        if(parseTextFiles.isIrreflexive()){
            irreflexive.add(name);
        }

        if(!symmetric.contains(name)){
            asymmetric.add(name);
        }
        return parseTextFiles.getContext();
    }


    public List<JFrame> getLattices() {
        return lattices;
    }

    private void generateConceptLattice(Context c){

            ConceptLattice conceptLattice = new ConceptLattice();

            Concept topConcept = new Concept();
            topConcept.setExtent(c.getEntities());
            Concept bottomConcept = new Concept();
            bottomConcept.setIntent(c.getAttributes());


            conceptLattice.setTop(topConcept);
            conceptLattice.setBottom(bottomConcept);

            Map<Set<Entity>, Set<Attribute>> pairs = c.getPairs();

            for (Set<Entity> set : pairs.keySet()) {
                Concept con = new Concept();

                con.setExtent(set);
                con.setIntent(pairs.get(set));
                if (con.getExtent().size() == c.getEntities().size()) {
                    conceptLattice.setTop(con);
                } else {
                    conceptLattice.addConcept(con);
                }
            }
            for(Concept con : conceptLattice){
                if (con.getSimplifiedIntent2(conceptLattice.getTop().getIntent()).size() == 1) {
                    conceptLattice.getTop().addChild(con);
                    con.addParent(conceptLattice.getTop());
                }

            }

            for (Concept c1 : conceptLattice) {
                for (Concept c2 : conceptLattice) {
                    if (!c2.getIntent().isEmpty() && c1.getIntent().containsAll(c2.getIntent()) && (c1.getIntent().size() - c2.getIntent().size()) > 0 && (c1.getIntent().size() - c2.getIntent().size()) <= 2) {
                        c1.addParent(c2);
                        c2.addChild(c1);
                    }
                }
                if (c1.getAllChildren().size() == 0) {
                    c1.addChild(bottomConcept);
                    bottomConcept.addParent(c1);
                }
            }

            Map<Concept, List<Concept>> toRemove = new HashMap<>();
            for (Concept c0 : conceptLattice) {
                List<Concept> newList = new ArrayList<>();
                for (Concept c1 : c0.getParents()) {
                    for(Concept c2 : c0.getParents()){
                        if(!c1.equals(c2) && c1.getParents().contains(c2)){
                            newList.add(c2);
                        }
                    }
                }
                toRemove.put(c0, newList);
            }

            for (Concept c0 : conceptLattice){
                if(!toRemove.get(c0).isEmpty()){
                    for(Concept rem : toRemove.get(c0)){
                        c0.removeParent(rem);
                        rem.removeChild(c0);
                    }
                }
            }
            conceptLattices.add(conceptLattice);

            if(c.equals(mainContext)){
                mainLattice = conceptLattice;
            }


    }

    public JFrame createGraph(ConceptLattice cLattice){
        Lattice lattice = new Lattice();

        //top concept
        String top;
        if(cLattice.getTop().getIntent().isEmpty()) {
            top = "owl:Thing";
        }
        else{
            top = cLattice.getTop().getIntent().toString();
        }
        //bottom concept
        lattice.addVertex(top);
        lattice.addVertex("owl:Nothing");

        for(Concept c : cLattice){
            String intent = c.getSimplifiedIntent2(cLattice.getTop().getIntent()).toString();
            lattice.addVertex(intent);
        }

        for(Concept c : cLattice){
            String intent = c.getSimplifiedIntent2(cLattice.getTop().getIntent()).toString();

            if(c.getChildren().isEmpty()){
                lattice.addEdge(intent + "owl:Nothing", intent, "owl:Nothing");
            }
            else {
                for (Concept d : c.getChildren()) {
                    String intent2 = d.getSimplifiedIntent2(cLattice.getTop().getIntent()).toString();
                    lattice.addEdge(intent + intent2, intent, intent2);
                }
            }

            if(c.getParents().contains(cLattice.getTop())){
                lattice.addEdge(intent + top, top, intent);
            }
        }

        if(cLattice.getTop().getChildren().isEmpty()) {
            lattice.addEdge(top + "owl:Nothing", top, "owl:Nothing");
        }

        Layout<Integer, String> layout = new CircleLayout(lattice.getGraph());

        layout.setSize(new Dimension(1000,800));


        BasicVisualizationServer<Integer, String> vv = new BasicVisualizationServer<Integer, String>(layout);
        vv.setPreferredSize(new Dimension(1000, 1000));
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());


        JFrame frame = new JFrame("Lattice");
        frame.getContentPane().add(vv);
        frame.pack();
        return frame;

    }

    public void generateGraphLattice(List<String> paths, Map<String, String> auxiliary, String name) throws IOException {

        for(String path : paths) {
            Context context = processData(path);
            contexts.add(context);
            if(path.contains(name)){
                mainContext = context;
            }
        }

        if(!auxiliary.isEmpty()){
            for(String key : auxiliary.keySet()){
                Context c = processTextFile(auxiliary.get(key), key, mainContext);
                mainContext = c;
            }
        }

        for(Context c : contexts) {
            if(c != mainContext) {
                generateConceptLattice(c);
            }
        }

        List<RelationalAttribute> newRa = new ArrayList<>();
        Map<RelationalAttribute, Set<Entity>> newPairs = new HashMap<>();
        for(Attribute a : mainContext.getAttributes()){
            if(a instanceof Facet){
                String aux = ((Facet) a).getValue();
                List<String> l = new ArrayList<>();
                for(Entity entity : mainContext.getEntities(a)){
                    l.add(entity.getName());
                }
                objProp.put((Facet) a, l);
                    for (ConceptLattice cl : conceptLattices) {
                        Concept c = cl.correspondingConcept(aux);
                        if (cl != mainLattice && c != null) {
                            RelationalAttribute ra = new RelationalAttribute(((Facet) a).getType(), c);
                            relationalAttributeHashMap.put((Facet) a, ra);
                            newRa.add(ra);
                            if(!mainContext.getEntities(a).isEmpty()){
                                if(newPairs.containsKey(ra)){
                                    Set<Entity> set = newPairs.get(ra);
                                    newPairs.remove(ra);
                                    set.addAll(mainContext.getEntities(a));
                                    newPairs.put(ra, set);
                                }
                                else{
                                    newPairs.put(ra, mainContext.getEntities(a));
                                }
                            }
                        }
                    }
            }
        }

        for(RelationalAttribute ra : newRa){
            mainContext.addAttribute(ra);
            for(Entity e : newPairs.get(ra)){
                mainContext.addPair(e, ra);
            }
            ra.setEntities(newPairs.get(ra));
        }

        mainContext.removeEmpties();


        generateConceptLattice(mainContext);
        for(ConceptLattice cl : conceptLattices){
            JFrame jfr = createGraph(cl);
            lattices.add(jfr);
        }

    }



    public void createOntology(String path, String name) throws OWLOntologyCreationException, OWLOntologyStorageException, InterruptedException {


        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();

        IRI ontologyIRI = IRI.create("http://www.semanticweb.org/mateiupatricia/ontologies/" + name + "_ontology");

        OWLOntology ontology = ontologyManager.createOntology(ontologyIRI);

        OWLDataFactory factory = ontologyManager.getOWLDataFactory();
        FunctionalSyntaxDocumentFormat pm = new FunctionalSyntaxDocumentFormat();
        ManchesterSyntaxDocumentFormat ms = new ManchesterSyntaxDocumentFormat();
        ms.setPrefix(":", "http://www.semanticweb.org/mateiupatricia/ontologies/" + name + "_ontology#");
        pm.setPrefix(":", "http://www.semanticweb.org/mateiupatricia/ontologies/" + name + "_ontology#");

        File file = new File("src/main/java/myalgorithm/examples/" + path + "/ontology/" + name + "_ontology.owl");

        ontologyManager.setOntologyFormat(ontology, pm);

        Map<Attribute, OWLClass> classItems = new HashMap<>();
        Map<Entity, OWLIndividual> individualItems = new HashMap<>();
        Map<String, OWLIndividual> individualItemsName = new HashMap<>();
        Map<String, OWLObjectProperty> objectProperties = new HashMap<>();


        for(Attribute atr : mainLattice.getBottom().getIntent()){
            if(atr instanceof RelationalAttribute){
                OWLObjectProperty op = factory.getOWLObjectProperty(((RelationalAttribute) atr).getValue(), pm);
                ontologyManager.addAxiom(ontology, factory.getOWLDeclarationAxiom(op));
                objectProperties.put(((RelationalAttribute) atr).getValue(), op);

            }
            else{
                OWLClass cl = factory.getOWLClass(atr.toString(), pm);
                classItems.put(atr, cl);
                ontologyManager.addAxiom(ontology, factory.getOWLDeclarationAxiom(cl));
            }
        }


        List<Concept> mainLatticeConcepts = new ArrayList<>(mainLattice.getConcepts());

        int j;
        int threadsNr = 32;
        OntologyThread[] threads = new OntologyThread[threadsNr];

        for(Entity e : mainLattice.getTop().getExtent()){
            OWLIndividual i = factory.getOWLNamedIndividual(e.toString(), pm);
            individualItems.put(e, i);
            individualItemsName.put(e.getName(), i);
            ontologyManager.addAxiom(ontology, factory.getOWLDeclarationAxiom((OWLEntity) i));
        }

        for(Concept c : mainLattice){
            for(Concept d : c.getChildren()){
                if(d.getSimplifiedIntent2(mainLattice.getTop().getIntent()).size() == 1){
                    for(Attribute attribute : c.getIntent()){
                        if(!c.isComplexConcept() && !d.isComplexConcept() && !d.getSimplifiedIntent2(mainLattice.getTop().getIntent()).contains(attribute)){
                            OWLAxiom axiom = factory.getOWLSubClassOfAxiom(classItems.get(d.getSimplifiedIntent2(mainLattice.getTop().getIntent()).toArray()[0]), classItems.get(attribute));
                            ontologyManager.addAxiom(ontology, axiom);
                        }
                    }
                }
            }
        }


        for(ConceptLattice conl : conceptLattices){
            if(!conl.equals(mainLattice)){
                for(Attribute atr : conl.getBottom().getIntent()){
                    OWLClass cl = factory.getOWLClass(atr.toString(), pm);
                    classItems.put(atr, cl);
                    ontologyManager.addAxiom(ontology, factory.getOWLDeclarationAxiom(cl));
                }
                for(Concept c : conl.getConcepts()){
                    for(Entity e : c.getExtent()){
                        for(Attribute a : c.getIntent()){
                            OWLAxiom axiom = factory.getOWLClassAssertionAxiom(classItems.get(a), individualItems.get(e));
                            ontologyManager.addAxiom(ontology, axiom);
                        }
                    }
                }
            }
        }


        for(String s : objectProperties.keySet()){
            if(symmetric.contains(s)){
                OWLAxiom axiom = factory.getOWLSymmetricObjectPropertyAxiom(objectProperties.get(s));
                ontologyManager.addAxiom(ontology, axiom);
            }
            if(asymmetric.contains(s)){
                OWLAxiom axiom = factory.getOWLAsymmetricObjectPropertyAxiom(objectProperties.get(s));
                ontologyManager.addAxiom(ontology, axiom);
            }
            if(functional.contains(s)){
                OWLAxiom axiom = factory.getOWLFunctionalObjectPropertyAxiom(objectProperties.get(s));
                ontologyManager.addAxiom(ontology, axiom);
            }
            if(inverseFunctional.contains(s)){
                OWLAxiom axiom = factory.getOWLInverseFunctionalObjectPropertyAxiom(objectProperties.get(s));
                ontologyManager.addAxiom(ontology, axiom);
            }

            if(reflexive.contains(s)){
                OWLAxiom axiom = factory.getOWLReflexiveObjectPropertyAxiom(objectProperties.get(s));
                ontologyManager.addAxiom(ontology, axiom);
            }

            if(irreflexive.contains(s)){
                OWLAxiom axiom = factory.getOWLIrreflexiveObjectPropertyAxiom(objectProperties.get(s));
                ontologyManager.addAxiom(ontology, axiom);
            }
        }



        for(Attribute a1 : mainContext.getAttributes()){
            for(Attribute a2 : mainContext.getAttributes()){
                if(a1 instanceof Facet && a2 instanceof Facet){
                    if(!((Facet) a1).getType().equals(((Facet) a2).getType()) && mainContext.getEntities(a1).equals(mainContext.getEntities(a2))) {
                        OWLAxiom axiom = factory.getOWLEquivalentObjectPropertiesAxiom(objectProperties.get(((Facet) a1).getType()), objectProperties.get(((Facet) a2).getType()));
                        ontologyManager.addAxiom(ontology, axiom);
                    }
                }
            }
        }


        for(int i = 0; i < threadsNr; i++) {
            Set<Concept> conceptSet = new HashSet<>();
            j = i;
            while(j < mainLatticeConcepts.size()){
                conceptSet.add(mainLatticeConcepts.get(j));
                j += threadsNr;
            }
            threads[i] = new OntologyThread(factory, ontologyManager, ontology, pm, conceptSet, classItems, objectProperties, individualItems, individualItemsName, objProp, relationalAttributeHashMap);
            threads[i].start();
        }

        for(int i = 0; i < threadsNr; i++) {
            threads[i].join();
        }


        ontologyManager.saveOntology(ontology, IRI.create(file.toURI()));
        o = ontology;
        manager = ontologyManager;
    }


    public void printResult(Set<? extends OWLEntity> entities, StringBuilder stringBuilder){
        int count = 0;
        if(!entities.isEmpty()){
            for(OWLEntity entity : entities){
                count++;
                String a = shortFormProvider.getShortForm(entity);
                stringBuilder.append(a);
                stringBuilder.append(" ");
                if(count == 10){
                    stringBuilder.append("\n");
                    count = 0;
                }
            }
        }
        else{
            stringBuilder.append("empty set\n");
        }
    }

    public String queryOntology(String ontology, String querys) throws OWLOntologyCreationException, OWLOntologyStorageException {
           manager = OWLManager.createOWLOntologyManager();
           o = manager.loadOntologyFromOntologyDocument(new File(ontology));
           System.out.println("Loaded ontology: " + o.getOntologyID());
           OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
           shortFormProvider = new SimpleShortFormProvider();
           Set<OWLOntology> importsClosure = o.getImportsClosure();
           BidirectionalShortFormProvider bsp =  new BidirectionalShortFormProviderAdapter(manager, importsClosure, shortFormProvider);
           reasoner = reasonerFactory.createReasoner(o);
           //reasoner = new Reasoner.ReasonerFactory().createReasoner(o);
           int index = ontology.lastIndexOf('/');
            StringBuilder stringBuilder = new StringBuilder();
            if (querys.length() == 0) {
                System.out.println("The query is invalid");
            }
            else{

                stringBuilder.append("Query : ");
                stringBuilder.append(querys);
                stringBuilder.append("\n");
            }
            if (querys.equalsIgnoreCase("x")) {
                return "";
            }
            OWLDataFactory factory = manager.getOWLDataFactory();

            ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(factory, querys);
            parser.setDefaultOntology(o);
            OWLEntityChecker entityChecker = new ShortFormEntityChecker(bsp);
            parser.setOWLEntityChecker(entityChecker);
            OWLClassExpression classExpression = parser.parseClassExpression();
            Set<OWLClass> superClasses;
            Set<OWLClass> equivalentClasses;
            Set<OWLClass> subClasses;
            Set<OWLNamedIndividual> individuals;
            superClasses = reasoner.getSuperClasses(classExpression, true).getFlattened();
            equivalentClasses = classExpression.isAnonymous()? reasoner.getEquivalentClasses(classExpression).getEntities() : reasoner.getEquivalentClasses(classExpression).getEntitiesMinus(classExpression.asOWLClass());
            subClasses = reasoner.getSubClasses(classExpression, true).getFlattened();
            individuals = reasoner.getInstances(classExpression, true).getFlattened();
            stringBuilder.append("\n");
            stringBuilder.append("Superclasses : \n");
            printResult(superClasses, stringBuilder);
            stringBuilder.append("\n\nSubclasses : \n");
            printResult(subClasses, stringBuilder);
            stringBuilder.append("\n\nEquivalent classes : \n");
            printResult(equivalentClasses, stringBuilder);
            stringBuilder.append("\n\nIndividuals : \n");
            printResult(individuals, stringBuilder);
            stringBuilder.append("\n\n\n");

            return stringBuilder.toString();

    }

    public static void main(String[] args) throws IOException {

        long start = System.currentTimeMillis();
        GUI gui = new GUI();
        gui.createAndShowGUI();

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;

        System.out.println("GUI displayed in " + (double) timeElapsed/1000 + " seconds");
    }
}
