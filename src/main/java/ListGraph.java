// PROG2 VT2024, Inlämningsuppgift, del 1
// Grupp 100
// Andreas Hagenstam anha3549
/**
 * @author Andreas Hagenstam
 */
import java.io.Serializable; // Gör det möjligt att spara objekt till fil (serialisering)
import java.util.*; // Importerar samlingsklasser som Map, List, Set, etc.

/**
 * En implementation av Graph-interfacet som använder en lista (Map med Listor) för att hålla ordning på noder och deras grannar.
 * Klassen är generisk och kan hantera valfri nodtyp T.
 * Den implementerar även Serializable så att den kan sparas till fil.
 */
public class ListGraph<T> implements Graph<T>, Serializable {

    // En hashtabell där varje nod är en nyckel, och värdet är en lista med dess utgående kanter (edges).
    private Map<T, List<Edge<T>>> adjacencyList;

    // Konstruktor som skapar en tom graf med en tom hash map
    public ListGraph() {
        this.adjacencyList = new HashMap<>();
    }

    @Override
    public void add(T node) {
        // Lägger till noden endast om den inte redan finns
        if (!adjacencyList.containsKey(node)){
            adjacencyList.put(node, new ArrayList<>());
        }
    }

    @Override
    public void connect(T src, T dest, String name, int weight) {
        // Kontroll: båda noderna måste finnas i grafen
        if (!adjacencyList.containsKey(src) || !adjacencyList.containsKey(dest)){
            throw new NoSuchElementException("Graph doesn't contain node/nodes with the same name");
        }
        // Kontroll: negativ vikt är inte tillåten
        if (weight < 0){
            throw new IllegalArgumentException("Weight cannot be negative");
        }
        // Kontroll: det får inte redan finnas en kant mellan noderna
        if (getEdgeBetween(src, dest) != null){
            throw new IllegalStateException("There can only be one edge between nodes with the same name");
        }
        // Lägger till kanten i båda riktningarna (grafen är alltså icke-riktad)
        adjacencyList.get(src).add(new Edge<>(dest, weight, name));
        adjacencyList.get(dest).add(new Edge<>(src, weight, name));
    }

    @Override
    public void remove(T node) {
        // Kontroll: noden måste finnas
        if (!adjacencyList.containsKey(node)){
            throw new NoSuchElementException("Graph doesn't contain the given node");
        }

        // Tar bort noden från adjacencyList
        adjacencyList.remove(node);
        // Tar även bort alla kanter i andra noder som leder till denna nod
        adjacencyList.values().forEach(edges -> edges.removeIf(edge -> edge.getDestination().equals(node)));
    }

    @Override
    public void setConnectionWeight(T src, T dest, int newWeight) {
        // Kontroll: noder måste finnas
        if(!adjacencyList.containsKey(src) || !adjacencyList.containsKey(dest)){
            throw new NoSuchElementException("Graph doesn't contain node/nodes");
        }
        // Kontroll: vikten får inte vara negativ
        if (newWeight < 0){
            throw new IllegalArgumentException("Weight cannot be negative");
        }
        // Sätter ny vikt på kanten från src till dest
        List<Edge<T>> edges = adjacencyList.get(src);
        if (edges != null){
            for (Edge<T> edge : edges){
                if (edge.getDestination().equals(dest)){
                    edge.setWeight(newWeight);
                    break;
                }
            }
        }
        // Sätter ny vikt på kanten från dest till src (eftersom grafen är icke-riktad)
        List<Edge<T>> reverseEdges = adjacencyList.get(dest);
        if (reverseEdges != null){
            for (Edge<T> edge : reverseEdges){
                if (edge.getDestination().equals(src)){
                    edge.setWeight(newWeight);
                    break;
                }
            }
        }
    }

    @Override
    public Set<T> getNodes() {
        // Returnerar mängden av alla noder i grafen
        return adjacencyList.keySet();
    }

    @Override
    public Collection<Edge<T>> getEdgesFrom(T node) {
        // Kontroll: noden måste finnas
        if (!adjacencyList.containsKey(node)) {
            throw new NoSuchElementException("Graph doesn't contain node");
        }
        // Returnerar en oföränderlig lista av kanter från noden
        return Collections.unmodifiableCollection(adjacencyList.get(node));
    }

    @Override
    public Edge<T> getEdgeBetween(T src, T dest) {
        // Kontroll: båda noderna måste finnas
        if (!adjacencyList.containsKey(src) || !adjacencyList.containsKey(dest)) {
            throw new NoSuchElementException("Graph does not contain node/nodes by that name");

        }
        // Söker igenom srcs lista av kanter för att hitta en kant till dest
        List<Edge<T>> edges = adjacencyList.get(src);
        if(edges != null) {
            for (Edge<T> edge : edges){
                if (edge.getDestination().equals(dest)){
                    return edge;
                }
            }
        }
        return null;
    }

    @Override
    public void disconnect(T src, T dest) {
        if (!adjacencyList.containsKey(src) || !adjacencyList.containsKey(dest)){
            throw new NoSuchElementException("Graph doesn't contain node/nodes"); //invalid input
        }
        if (getEdgeBetween(src, dest) == null){
            throw new IllegalStateException("There is no connection between given nodes");
        }
        // Hämtar listor för båda noderna och tar bort kanterna mellan dem
        List<Edge<T>> srcEdges = adjacencyList.get(src);
        List<Edge<T>> destEdges = adjacencyList.get(dest);

        if (srcEdges != null) {
            srcEdges.removeIf(edge -> edge.getDestination().equals(dest));
        }

        if (destEdges != null){
            destEdges.removeIf(edge -> edge.getDestination().equals(src));
        }
    }
    // Hjälpmetod för DFS (depth-first search)
    private boolean dfs(T src, T dest, Set<T> visited, List<Edge<T>> path){
        // om src och dest är samma node, returnera true
        if (src.equals(dest)){
            return true;
        }
        // markera startnoden (src) som besökt
        visited.add(src);

        List<Edge<T>> edges = adjacencyList.get(src);
        if (edges != null){
            // går igenom startnodens edge-lista
            for (Edge<T> edge : edges){
                if (!visited.contains(edge.getDestination())){
                    path.add(edge);
                    // Rekursiv genomgång
                    if (dfs(edge.getDestination(), dest, visited, path)){
                        return true;
                    }
                    path.remove(path.size() - 1); // går tillbaka om ingen väg hittades via denna edge.
                }
            }
        }
        // om ingen väg hittades
        return false;
    }

    @Override
    public boolean pathExists(T src, T dest) {
        // Om någon av noderna inte finns kan ingen väg existera
        if (!adjacencyList.containsKey(src) || !adjacencyList.containsKey(dest)) {
            return false;
        }
        List<Edge<T>> path = new ArrayList<>(); // används inte här, men skickas till dfs
        Set<T> visited = new HashSet<>();
        return dfs(src, dest, visited, path);
    }

    @Override
    public List<Edge<T>> getPath(T src, T dest) {
        List<Edge<T>> path = new ArrayList<>();
        if (!adjacencyList.containsKey(src) || !adjacencyList.containsKey(dest)) {
            return null;
        }

        Set<T> visited = new HashSet<>();
        boolean pathFound = dfs(src, dest, visited, path);

        // Om pathfound returnerar true så returneras path, annars returneras null
        return pathFound ? path : null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Nodes: ");
        sb.append("/n");
        for (Map.Entry<T, List<Edge<T>>> entry : adjacencyList.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("/n");
        }
        return sb.toString();
    }
}