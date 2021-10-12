package myalgorithm;

import myalgorithm.galatea.BinaryAttribute;
import myalgorithm.galatea.Concept;
import myalgorithm.galatea.Entity;

import java.util.HashSet;
import java.util.Set;

public class RelationalAttribute extends BinaryAttribute {


    private Concept concept;

    private Set<Entity> entities = new HashSet<>();

    public RelationalAttribute(String value, Concept concept) {
        super(value);
        this.concept = concept;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }


    //will be displayed in the graph lattice as label
    @Override
    public String toString() {
        return "(" + value + ", Concept " + concept.getIntent().toString() + ")";
    }

    public Set<Entity> getEntities() {
        return entities;
    }

    public void setEntities(Set<Entity> entities) {
        this.entities = entities;
    }

    public Concept getConcept() {
        return concept;
    }
}
