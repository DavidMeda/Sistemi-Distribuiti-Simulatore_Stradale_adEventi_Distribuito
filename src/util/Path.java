package util;

import java.util.LinkedList;

import network.NetEdge;

public class Path implements Comparable<Path> {
 
    LinkedList<NetEdge> lista;
    double length;
 
    public Path() {
        lista = new LinkedList<>();
    }
    
    public LinkedList<NetEdge> getEdgesList(){return lista;}
    
    public void add(NetEdge e) {
        lista.add(e);
        length += (double)e.getAttribute("length");
    }
    
    public NetEdge getFirstEdge() {return lista.getLast();}
    public int size() {return lista.size();}
    
    public double pathlength() {
        return length;
    }
    
    @Override
    public int compareTo(Path p) {
        if (this.pathlength()<p.pathlength()) return -1;
        if (this.pathlength()>p.pathlength()) return +1;
        return lista.size() - p.lista.size();
    }
   
    public String toString(){
        return "\nPATH = "+lista.toString();
    }
}
 