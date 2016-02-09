package milk.entitymanager.task;

import cn.nukkit.Server;
import cn.nukkit.level.Position;
import cn.nukkit.math.NukkitMath;
import milk.entitymanager.EntityManager;
import milk.entitymanager.util.Utils;

import java.util.List;
import java.util.Map;

public class EntitySpawnerTask implements Runnable{

    @Override
    public void run(){
        EntityManager owner = (EntityManager) Server.getInstance().getPluginManager().getPlugin("EntityManager");
        final int[] rand = {1, 4};
        try{
            String[] rs = owner.getData("spawner.rand", "1/4").split("/");
            rand[0] = Integer.parseInt(rs[0]);
            rand[1] = Integer.parseInt(rs[1]);
        }catch(Exception ignore){}

        EntityManager.spawner.forEach((key, k) -> {
            if(
                !(k instanceof Map)
                || Utils.rand(rand[0], rand[1]) > rand[0]
            ){
                return;
            }

            Map data = (Map) k;
            int radius = (int) data.get("radius");

            String[] vec = key.split(":");
            Position pos = new Position(Integer.parseInt(vec[0]) + 0.5 + Utils.rand(-radius, radius), Integer.parseInt(vec[1]), Integer.parseInt(vec[2]) + 0.5 + Utils.rand(-radius, radius));
            pos.level = Server.getInstance().getLevelByName(vec[3]);
            pos.y = pos.level.getHighestBlockAt(NukkitMath.floorDouble(pos.x), NukkitMath.floorDouble(pos.z)) + 1;

            if(!(data.get("mob-list") instanceof List)){
                return;
            }

            List list = (List) data.get("mob-list");
            EntityManager.create(list.get(Utils.rand(1, list.size()) - 1), pos);
        });
    }

}