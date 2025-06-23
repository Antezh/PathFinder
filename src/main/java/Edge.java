// PROG2 VT2024, Inlämningsuppgift, del 1
// Grupp 100
// Andreas Hagenstam anha3549

import java.io.Serializable; // Gör det möjligt att spara objekt till fil (serialisering)

public class Edge<T> implements Serializable {

    private T destination;
    private int weight;
    private String name;

    // Initierar en ny kant med destination, vikt och namn.
    public Edge(T destination, int weight, String name){
        this.destination = destination;
        this.weight = weight;
        this.name = name;
    }

    public T getDestination(){
        return destination;
    }

    public int getWeight() {
        return weight;
    }

    public int setWeight(int weight) {
        if (weight < 0){
            throw new IllegalArgumentException("Weight must be a positive number");
        } else {
            return this.weight = weight;
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "till " + destination + " med " + name + " tar " + weight;
    }
}
