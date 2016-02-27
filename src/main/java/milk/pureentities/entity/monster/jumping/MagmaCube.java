package milk.pureentities.entity.monster.jumping;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.pureentities.entity.monster.JumpingMonster;

public class MagmaCube extends JumpingMonster{
    public static final int NETWORK_ID = 42;

    public MagmaCube(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId(){
        return NETWORK_ID;
    }

    @Override
    public float getWidth(){
        return 1.2f;
    }

    @Override
    public float getHeight(){
        return 1.2f;
    }

    @Override
    public double getSpeed(){
        return 0.8;
    }

    public void initEntity(){
        super.initEntity();

        this.fireProof = true;
        this.setDamage(new int[]{0, 3, 4, 6});
    }

    public String getName(){
        return "MagmaCube";
    }

    public void attackEntity(Entity player){
        if(this.attackDelay > 10 && this.distanceSquared(player) < 1){
            this.attackDelay = 0;

            EntityDamageEvent ev = new EntityDamageByEntityEvent(this, player, EntityDamageEvent.CAUSE_ENTITY_ATTACK, this.getDamage());
            player.attack(ev);
        }
    }

    public Item[] getDrops(){
    	return new Item[0];
    }

}