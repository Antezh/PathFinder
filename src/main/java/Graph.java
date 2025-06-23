/**
 * @author Andreas Hagenstam
 */
import java.util.Collection; // Import för att använda Collection, en övergripande interface för samlingar (t.ex.
// List och Set)
import java.util.List; // Import för att använda List, en ordnad samling av element
import java.util.Set; // Import för att använda Set, en samling utan dubbletter

// Ett generellt interface för en grafstruktur. Den är generisk vilket betyder att den kan hantera valfri typ av nod (T).
public interface Graph<T> {

    // Lägger till en nod till grafen.
    void add(T node);

    // Lägger till en kant (edge) mellan två noder med ett givet namn och vikt.
    void connect(T node1, T node2, String name, int weight);

    // Sätter (eller uppdaterar) vikten på en befintlig kant mellan två noder
    void setConnectionWeight(T node1, T node2, int weight);

    // Returnerar alla noder i grafen som en Set (ingen dubblett av noder tillåts)
    Set<T> getNodes();

    // Returnerar alla kanter som utgår från en viss nod
    Collection<Edge<T>> getEdgesFrom(T node);

    // Returnerar kanten mellan två specifika noder, om den finns
    Edge<T> getEdgeBetween(T node1, T node2);

    // Tar bort kanten mellan två noder, om en sådan finns
    void disconnect(T node1, T node2);

    // Tar bort en nod från grafen, och även alla kanter som är kopplade till noden
    void remove(T node);

    // Returnerar true om det finns en väg mellan två noder, annars false
    boolean pathExists(T from, T to);

    // Returnerar den kortaste eller en möjlig väg mellan två noder som en lista av kanter
    List<Edge<T>> getPath(T from, T to);
}
