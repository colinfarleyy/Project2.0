/*
import java.util.HashMap;
import java.util.Map;

public class TranspositionTable {
    private final Map<Board, MinimaxInfo> table;

    public TranspositionTable() {
        table = new HashMap<>();
    }

    // Adds a state to the transposition table
    public void put(Board state, MinimaxInfo info) {
        table.put(state, info);
    }

    // Retrieves the info for a given state, if it exists
    public MinimaxInfo get(Board state) {
        return table.get(state);
    }

    // Checks if a state is already in the table
    public boolean contains(Board state) {
        return table.containsKey(state);
    }

    // Returns the number of states in the table
    public int size() {
        return table.size();
    }
} */