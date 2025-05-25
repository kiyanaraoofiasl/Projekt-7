package state;
import java.util.HashMap;

public class State
{
    private String stateName;
    private final HashMap<String,Object> items;

    public State()
    {
        items = new HashMap<>();
    }

    public Object getItem(String key)
    {
        return items.get(key);
    }

    public void putItem(String key,Object item)
    {
        items.put(key,item);
    }

    public void removeItem(String key)
    {
        items.remove(key);
    }

    public void emptyState()
    {
        items.clear();
    }
}