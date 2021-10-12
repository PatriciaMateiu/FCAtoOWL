package myalgorithm;
import myalgorithm.galatea.Attribute;
import myalgorithm.galatea.Concept;
import myalgorithm.galatea.Entity;
import myalgorithm.galatea.composite.Facet;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;

import java.util.*;

public class OntologyThread extends Thread{


    private OWLDataFactory factory;
    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private FunctionalSyntaxDocumentFormat  pm;
    private Set<Concept> concepts = new HashSet<>();
    private Map<Attribute, OWLClass> classItems = new HashMap<>();
    private Map<String, OWLObjectProperty> objectProperties = new HashMap<>();
    private Map<Entity, OWLIndividual> individualItems = new HashMap<>();
    private Map<String, OWLIndividual> individualItemsName = new HashMap<>();
    private Map<Facet, List<String>> objProp = new HashMap<>();
    private Map<Facet, RelationalAttribute> relationalAttributeMap = new HashMap<>();


    public OntologyThread(OWLDataFactory factory, OWLOntologyManager manager, OWLOntology ontology, FunctionalSyntaxDocumentFormat pm, Set<Concept> concepts, Map<Attribute, OWLClass> classItems, Map<String, OWLObjectProperty> objectProperties, Map<Entity, OWLIndividual> individualItems, Map<String, OWLIndividual> individualItemsName, Map<Facet, List<String>> objProp, Map<Facet, RelationalAttribute> relationalAttributeMap) {
        this.factory = factory;
        this.manager = manager;
        this.ontology = ontology;
        this.pm = pm;
        this.concepts = concepts;
        this.classItems = classItems;
        this.objectProperties = objectProperties;
        this.individualItems = individualItems;
        this.individualItemsName = individualItemsName;
        this.objProp = objProp;
        this.relationalAttributeMap = relationalAttributeMap;
    }

    public void run(){

        for(Concept c : concepts){
            for(Entity e : c.getExtent()){
                for(Attribute a : c.getIntent()){
                    if(!(a instanceof RelationalAttribute)) {
                        OWLAxiom axiom = factory.getOWLClassAssertionAxiom(classItems.get(a), individualItems.get(e));
                        manager.addAxiom(ontology, axiom);
                    }
                }
            }
            List<Attribute> list = new ArrayList<>();
            for(Attribute a : c.getIntent()){
                if(!(a instanceof RelationalAttribute)) {
                    list.add(a);
                }
            }

           for(Facet f : objProp.keySet()){
                for(String obj : objProp.get(f)){
                    OWLAxiom axiom = factory.getOWLObjectPropertyAssertionAxiom(objectProperties.get(f.getType()),individualItemsName.get(obj), individualItemsName.get(f.getValue()));
                    manager.addAxiom(ontology, axiom);
                }
               RelationalAttribute ra = relationalAttributeMap.get(f);
                for(Attribute a : ra.getConcept().getIntent()){
                    OWLAxiom axiom = factory.getOWLObjectPropertyRangeAxiom(objectProperties.get(ra.getValue()), classItems.get(a));
                    manager.addAxiom(ontology, axiom);
                }
           }


        }

    }

}
