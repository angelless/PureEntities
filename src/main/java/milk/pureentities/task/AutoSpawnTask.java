package milk.pureentities.task;

import java.util.Iterator;
import java.util.Map.Entry;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.biome.Biome;
import milk.pureentities.PureEntities;
import milk.pureentities.util.Utils;

public class AutoSpawnTask implements Runnable{

    public void run(){
        Iterator<?> iter = Server.getInstance().getOnlinePlayers().entrySet().iterator();
        while(iter.hasNext()){
            Entry<?, ?> entry = (Entry<?, ?>) iter.next();
            if(Utils.rand(1, 100) > 25){
                continue;
            }

            Player player = (Player) entry.getValue();
            Position pos = player.getPosition();

            pos.x += this.getRandomSafeXZCoord(50, 26, 6);
            pos.z += this.getRandomSafeXZCoord(50, 26, 6);
            pos.y = this.getSafeYCoord(player.getLevel(), pos, 3);

            if(
                pos.y > 127
                || pos.y < 1
                || player.getLevel().getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z) == Block.AIR
            ){
                continue;
            }

            int blockId = player.getLevel().getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z);
            int biomeId = player.getLevel().getBiomeId((int) pos.x, (int) pos.z);
            int blockLightLevel = player.getLevel().getBlockLightAt((int) pos.x, (int) pos.y, (int) pos.z);

            // int blockSkyLightLevel = player.getLevel().getBlockSkyLightAt((int) pos.x, (int) pos.y, (int) pos.z);

            if(blockId == Block.NETHER_BRICK_BLOCK || blockId == Block.NETHER_BRICK_FENCE || blockId == Block.NETHER_BRICKS_STAIRS || blockId == Block.SOUL_SAND || blockId == Block.NETHERRACK){
                if(Utils.rand(1, 100) <= 20){
                    this.createEntity("Blaze", pos.add(0, 2.8, 0));
                    return;
                }
                if(Utils.rand(1, 100) <= 20){
                    this.createEntity("Ghast", pos.add(0, 5, 0));
                    return;
                }
                if(Utils.rand(1, 100) <= 30){
                    this.createEntity("MagmaCube", pos.add(0, 2.2, 0));
                    return;
                }
                if(Utils.rand(1, 100) <= 50){
                    this.createEntity("PigZombie", pos.add(0, 3.8, 0));
                    return;
                }
            }

            // if (blockSkyLightLevel >= 10) {
            if(!this.isNight(player.getLevel().getTime())){
                if(blockId == Block.MYCELIUM && Utils.rand(1, 100) <= 70){
                    this.createEntity("Mooshroom", pos.add(0, 2.12, 0));
                    return;
                }
                if((blockId == Block.GRASS || blockId == Block.LEAVE) && Utils.rand(1, 100) <= 20){
                    this.createEntity("Ocelot", pos.add(0, 1.9, 0));
                    return;
                }
                if(biomeId == Biome.SWAMP && Utils.rand(1, 100) <= 30){
                    this.createEntity("Slime", pos.add(0, 2.2, 0));
                    return;
                }
                if(blockId == Block.GRASS && Utils.rand(1, 100) <= 30){
                    if(biomeId == Biome.FOREST || biomeId == Biome.BIRCH_FOREST || biomeId == Biome.TAIGA){
                        this.createEntity("Wolf", pos.add(0, 1.9, 0));
                        return;
                    }
                }
                if(blockId == Block.GRASS && Utils.rand(1, 100) <= 10){
                    this.createEntity("IronGolem", pos.add(0, 3.1, 0));
                    return;
                }

                switch(Utils.rand(1, 5)){
                    case 1:
                        this.createEntity("Chicken", pos.add(0, 1.7, 0));
                        break;
                    case 2:
                        this.createEntity("Cow", pos.add(0, 2.3, 0));
                        break;
                    case 3:
                        this.createEntity("Pig", pos.add(0, 1.9, 0));
                        break;
                    case 4:
                        this.createEntity("Rabbit", pos.add(0, 1.75, 0));
                        break;
                    case 5:
                        this.createEntity("Sheep", pos.add(0, 2.3, 0));
                        break;
                }
            }else{
                if(blockLightLevel <= 3 && pos.y <= 63 && Utils.rand(1, 100) <= 20){
                    this.createEntity("Bat", pos.add(0, 1.3, 0));
                    return;
                }
                if(blockLightLevel <= 3 && pos.y <= 63 && Utils.rand(1, 100) <= 20){
                    this.createEntity("CaveSpider", pos.add(0, 1.8, 0));
                    return;
                }
                if(blockLightLevel < 2 && Utils.rand(1, 100) <= 20 && blockId == Block.STONE){
                    this.createEntity("Silverfish", pos.add(0, 1.3, 0));
                    return;
                }
                switch(Utils.rand(1, 6)){
                    case 1:
                        this.createEntity("Creeper", pos.add(0, 2.8, 0));
                        break;
                    case 2:
                        this.createEntity("Enderman", pos.add(0, 3.8, 0));
                        break;
                    case 3:
                        this.createEntity("Skeleton", pos.add(0, 2.8, 0));
                        break;
                    case 4:
                        this.createEntity("Spider", pos.add(0, 2.12, 0));
                        break;
                    case 5:
                        this.createEntity("Zombie", pos.add(0, 2.8, 0));
                        break;
                    case 6:
                        this.createEntity("ZombieVillager", pos.add(0, 2.8, 0));
                        break;
                }
            }
        }
    }

    public void createEntity(Object type, Position pos){
        Entity entity = PureEntities.create(type, pos);
        if(entity != null){
            entity.spawnToAll();
        }
    }

    public boolean isNight(int tick){
        int totalhour = (tick / 1000) + 6;
        int totalday = (int) Math.floor(totalhour / 24);
        int nowhour = (int) Math.floor(((int) Math.floor(totalhour) - totalday * 24));
        return nowhour >= 18 || nowhour < 6;
    }

    public int getRandomSafeXZCoord(int degree, int safeDegree, int correctionDegree){
        int addX = Utils.rand(degree / 2 * -1, degree / 2);
        if(addX >= 0){
            if(degree < safeDegree){
                addX = safeDegree;
                addX += Utils.rand(correctionDegree / 2 * -1, correctionDegree / 2);
            }
        }else{
            if(degree > safeDegree){
                addX = -safeDegree;
                addX += Utils.rand(correctionDegree / 2 * -1, correctionDegree / 2);
            }
        }
        return addX;
    }

    public int getSafeYCoord(Level level, Position pos, int needDegree){
        int x = (int) pos.x;
        int y = (int) pos.y;
        int z = (int) pos.z;

        if(level.getBlockIdAt(x, y, z) == Block.AIR){
            while(true){
                y--;
                if(y > 127){
                    y = 128;
                    break;
                }
                if(y < 1){
                    y = 0;
                    break;
                }
                if(level.getBlockIdAt(x, y, z) != Block.AIR){
                    int checkNeedDegree = needDegree;
                    int checkY = y;
                    while(true){
                        checkY++;
                        checkNeedDegree--;
                        if(checkY > 255 || checkY < 1 || level.getBlockIdAt(x, checkY, z) != Block.AIR){
                            break;
                        }

                        if(checkNeedDegree <= 0){
                            return y;
                        }
                    }
                }
            }
        }else{
            while(true){
                y++;
                if(y > 127){
                    y = 128;
                    break;
                }

                if(y < 1){
                    y = 0;
                    break;
                }

                if(level.getBlockIdAt(x, y, z) != Block.AIR){
                    int checkNeedDegree = needDegree;
                    int checkY = y;
                    while(true){
                        checkY--;
                        checkNeedDegree--;
                        if(checkY > 255 || checkY < 1 || level.getBlockIdAt(x, checkY, z) != Block.AIR){
                            break;
                        }

                        if(checkNeedDegree <= 0){
                            return y;
                        }
                    }
                }
            }
        }
        return y;
    }
}