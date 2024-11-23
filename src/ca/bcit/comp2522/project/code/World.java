import java.util.Map;

public class World
{
    private final Map<String, Country> countryMap;

    public World(Map<String, Country> countryMap)
    {
        this.countryMap = countryMap;
    }

    /**
     * Getter for countryMap
     *
     * @return countryMap
     */
    public Map<String,Country> getCountryMap()
    {
        return countryMap;
    }
}
