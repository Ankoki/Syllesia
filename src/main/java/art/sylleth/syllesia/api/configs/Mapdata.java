package art.sylleth.syllesia.api.configs;

import art.sylleth.syllesia.Syllesia;
import art.sylleth.syllesia.files.ConfigurationFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapdata extends ConfigurationFile {

    private static final int[][] LABYRINTH = {
            {1, 1, 1, 1, 1, 1, 1, 1, 5, 5, 5, 5, 5, 3, 3},
            {1, 0, 0, 0, 0, 0, 0, 0, 5, 0, 5, 5, 5, 0, 3},
            {1, 0, 5, 5, 5, 5, 5, 0, 0, 0, 0, 0, 0, 0, 3},
            {2, 0, 5, 0, 0, 0, 5, 0, 6, 0, 0, 0, 0, 0, 3},
            {1, 0, 5, 0, 0, 0, 5, 0, 6, 5, 5, 0, 5, 6, 5},
            {1, 0, 5, 0, 0, 0, 5, 0, 5, 0, 0, 0, 0, 0, 5},
            {1, 0, 5, 2, 0, 5, 5, 0, 5, 0, 0, 0, 0, 0, 5},
            {1, 0, 0, 4, 0, 4, 0, 0, 5, 0, 0, 0, 0, 0, 5},
            {1, 6, 1, 4, 0, 4, 1, 1, 4, 4, 4, 0, 4, 4, 4},
            {1, 0, 0, 0, 0, 0, 1, 4, 0, 0, 0, 0, 0, 0, 4},
            {1, 0, 0, 0, 6, 0, 1, 4, 0, 0, 0, 0, 0, 0, 4},
            {1, 0, 0, 2, 0, 0, 1, 4, 0, 3, 3, 6, 3, 0, 6},
            {1, 0, 0, 0, 0, 0, 1, 6, 0, 3, 3, 3, 3, 0, 4},
            {2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 4, 0, 0, 4},
            {1, 1, 6, 1, 1, 1, 1, 4, 2, 4, 4, 4, 4, 4, 4}
    };

    private static final Map<String, Object> DEFAULTS = new HashMap<>();

    static {
        List<List<Integer>> ruinsList = new ArrayList<>();
        for (int[] ruin : LABYRINTH) {
            List<Integer> row = new ArrayList<>();
            for (int i : ruin)
                row.add(i);
            ruinsList.add(row);
        }
        DEFAULTS.put("labyrinth", ruinsList);
    }

    /**
     * Creates a new configuration file storing mapdata.
     */
    public Mapdata() {
        super(FileType.JSON, true);
    }

    @Override
    @NotNull
    public Map<String, Object> getDefaults() {
        return Mapdata.DEFAULTS;
    }

    @Override
    @NotNull
    public String getId() {
        return ConfigurationFile.MAPDATA;
    }

    @Override
    @NotNull
    public String getPath() {
        return "mapdata.json";
    }

    @Override
    public void processData(Map<String, Object> data) {
        this.validateMap(data);
        Syllesia.getInstance().clearMapRegistry();
        List<Object> ruinsList = (List<Object>) data.get("labyrinth");
        int[][] ruinsMatrix = new int[ruinsList.size()][((List<Integer>) ruinsList.get(0)).size()];
        int x = 0;
        for (Object obj : ruinsList) {
            if (obj instanceof Integer)
                continue;
            List<Integer> row = (List<Integer>) obj;
            int y = 0;
            for (Integer i : row) {
                ruinsMatrix[x][y] = i;
                y++;
            }
            x++;
        }
        Syllesia.getInstance().registerMap(new art.sylleth.syllesia.api.world.Map("labyrinth", ruinsMatrix));
        Syllesia.getInstance().getLogger().debug("Labyrinth map registered.");
    }

    @Override
    public void writeData() {
        Map<String, Object> data = new HashMap<>();
        for (art.sylleth.syllesia.api.world.Map map : Syllesia.getInstance().getMaps()) {
            List<List<Integer>> mapList = new ArrayList<>();
            for (int[] ruin : map.getMatrix()) {
                List<Integer> row = new ArrayList<>();
                boolean onlyAir = true;
                for (int i : ruin) {
                    if (i > 0)
                        onlyAir = false;
                    row.add(i);
                }
                if (!onlyAir) // There's a bug where excess rows are added containing only air, this should bypass that.
                    mapList.add(row);
            }
            data.put(map.getName(), mapList);
        }
        this.writeFile(data);
    }

}
