package state;
import java.util.HashMap;

public class Context
{
    private final HashMap<String,State> states;

    public Context()
    {
        states = new HashMap<>();
    }

    public void putState(String stateName,State state)
    {
        states.put(stateName,state);
    }

    public State getState(String stateName)
    {
        return states.get(stateName);
    }

    public void removeState(String stateName)
    {
        states.remove(stateName);
    }
}