package myalgorithm;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

import java.util.Collection;

public class Lattice {

    Graph<String, String> graph = new SparseMultigraph<String, String>();

    public Lattice() {
    }

    public Graph<String, String> getGraph() {
        return graph;
    }

    public void setGraph(Graph<String, String> graph) {
        this.graph = graph;
    }

    void addVertex(String vertex){
        graph.addVertex(vertex);
    }

    void addEdge(String edge, String v1, String v2){   //did not choose the variant with 4 parameters because the graph will be directed
        graph.addEdge(edge, v1, v2, EdgeType.DIRECTED);
    }


    void removeVertex(String vertex){
        graph.removeVertex(vertex);
    }

    void removeEdge(String edge){
        graph.removeEdge(edge);
    }

    Collection<String> getVertices(){
        return graph.getVertices();
    }

    Collection<String> getEdges(){
        return graph.getEdges();
    }

    Collection<String> getInEdges(String v){
        return  graph.getInEdges(v);
    }

    Collection<String> getOutEdges(String v){
        return graph.getOutEdges(v);
    }

    public String getStartVertex(String e){
        return graph.getSource(e);
    }


}
