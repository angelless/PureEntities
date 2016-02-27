package milk.pureentities.entity.monster.walking;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import milk.pureentities.entity.monster.WalkingMonster;
import milk.pureentities.util.Utils;

import java.util.HashMap;

public class Spider extends WalkingMonster{
    public static final int NETWORK_ID = 35;

    public Spider(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId(){
        return NETWORK_ID;
    }

    @Override
    public float getWidth(){
        return 1.3f;
    }

    @Override
    public float getHeight(){
        return 1.12f;
    }

    @Override
    public float getEyeHeight(){
        return 1;
    }

    @Override
    public double getSpeed(){
        return 1.13;
    }

    public void initEntity(){
        super.initEntity();

        this.setMaxHealth(16);
        this.setDamage(new int[]{0, 2, 2, 3});
    }

    @Override
    public String getName(){
        return "Spider";
    }

    @Override
    public boolean onUpdate(int currentTick){
        if(this.server.getDifficulty() < 1){
            this.close();
            return false;
        }

        if(!this.isAlive()){
            if(++this.deadTicks >= 23){
                this.close();
                return false;
            }
            return true;
        }

        int tickDiff = currentTick - this.lastUpdate;
        this.lastUpdate = currentTick;
        this.entityBaseTick(tickDiff);

        if(!this.isMovement()){
            return true;
        }

        if(this.isKnockback()){
            this.move(this.motionX * tickDiff, this.motionY, this.motionZ * tickDiff);
            this.motionY -= 0.15 * tickDiff;
            this.updateMovement();
            return true;
        }

        Vector3 before = this.baseTarget;
        this.checkTarget();
        if(this.baseTarget instanceof EntityCreature || before != this.baseTarget){
            double x = this.baseTarget.x - this.x;
            double y = this.baseTarget.y - this.y;
            double z = this.baseTarget.z - this.z;

            Vector3 target = this.baseTarget;
            double diff = Math.abs(x) + Math.abs(z);
            double distance = Math.sqrt(Math.pow(this.x - target.x, 2) + Math.pow(this.z - target.z, 2));
            if(distance <= 2){
                if(target instanceof EntityCreature){
                    if(distance <= (this.getWidth() + 0.0d) / 2 + 0.05){
                        if(this.attackDelay < 10){
                            this.motionX = this.getSpeed() * 0.1 * (x / diff);
                            this.motionZ = this.getSpeed() * 0.1 * (z / diff);
                        }else{
                            this.motionX = 0;
                            this.motionZ = 0;
                            this.attackEntity((Entity) target);
                        }
                    }else{
                        if(!this.isFriendly()){
                            this.motionY = 0.15;
                        }
                        this.motionX = this.getSpeed() * 0.15 * (x / diff);
                        this.motionZ = this.getSpeed() * 0.15 * (z / diff);
                    }
                }else if(Math.pow(this.x - target.x, 2) + Math.pow(this.z - target.z, 2) <= 1){
                    this.moveTime = 0;
                }
            }else{
                this.motionX = this.getSpeed() * 0.15 * (x / diff);
                this.motionZ = this.getSpeed() * 0.15 * (z / diff);
            }
            this.yaw = Math.toDegrees(-Math.atan2(x / diff, z / diff));
            this.pitch = y == 0 ? 0 : Math.toDegrees(-Math.atan2(y, Math.sqrt(x * x + z * z)));
        }

        double dx = this.motionX * tickDiff;
        double dz = this.motionZ * tickDiff;
        if(this.stayTime > 0){
            boolean isJump = this.checkJump(dx, dz);
            this.stayTime -= tickDiff;

            this.move(0, this.motionY * tickDiff, 0);
            if(!isJump){
                if(this.onGround){
                    this.motionY = 0;
                }else if(this.motionY > -this.getGravity() * 4){
                    this.motionY = -this.getGravity() * 4;
                }else{
                    this.motionY -= this.getGravity() * tickDiff;
                }
            }
        }else{
            boolean isJump = this.checkJump(dx, dz);

            Vector2 be = new Vector2(this.x + dx, this.z + dz);
            this.move(dx, this.motionY * tickDiff, dz);
            Vector2 af = new Vector2(this.x, this.z);

            if((be.x != af.x || be.y != af.y) && !isJump){
                this.moveTime -= 90 * tickDiff;
            }

            if(!isJump){
                if(this.onGround){
                    this.motionY = 0;
                }else if(this.motionY > -this.getGravity() * 4){
                    this.motionY = -this.getGravity() * 4;
                }else{
                    this.motionY -= this.getGravity() * tickDiff;
                }
            }
        }
        this.updateMovement();
        return true;
    }

    @Override
    public Vector3 updateMove(int tickDiff){
        return null;
    }

    @Override
    public void attackEntity(Entity player){
        if(((this.isFriendly() && !(player instanceof Player)) || !this.isFriendly())){
            this.attackDelay = 0;
            HashMap<Integer, Float> damage = new HashMap<>();
            damage.put(EntityDamageEvent.MODIFIER_BASE, (float) this.getDamage());

            if(player instanceof Player){
                HashMap<Integer, Float> armorValues = new HashMap<Integer, Float>() {{
                    put(Item.LEATHER_CAP, 1f);
                    put(Item.LEATHER_TUNIC, 3f);
                    put(Item.LEATHER_PANTS, 2f);
                    put(Item.LEATHER_BOOTS, 1f);
                    put(Item.CHAIN_HELMET, 1f);
                    put(Item.CHAIN_CHESTPLATE, 5f);
                    put(Item.CHAIN_LEGGINGS, 4f);
                    put(Item.CHAIN_BOOTS, 1f);
                    put(Item.GOLD_HELMET, 1f);
                    put(Item.GOLD_CHESTPLATE, 5f);
                    put(Item.GOLD_LEGGINGS, 3f);
                    put(Item.GOLD_BOOTS, 1f);
                    put(Item.IRON_HELMET, 2f);
                    put(Item.IRON_CHESTPLATE, 6f);
                    put(Item.IRON_LEGGINGS, 5f);
                    put(Item.IRON_BOOTS, 2f);
                    put(Item.DIAMOND_HELMET, 3f);
                    put(Item.DIAMOND_CHESTPLATE, 8f);
                    put(Item.DIAMOND_LEGGINGS, 6f);
                    put(Item.DIAMOND_BOOTS, 3f);
                }};

                float points = 0;
                for (Item i : ((Player) player).getInventory().getArmorContents()) {
                    points += armorValues.getOrDefault(i.getId(), 0f);
                }

                damage.put(EntityDamageEvent.MODIFIER_ARMOR, (float) (damage.getOrDefault(EntityDamageEvent.MODIFIER_ARMOR, 0f) - Math.floor(damage.getOrDefault(EntityDamageEvent.MODIFIER_BASE, 1f) * points * 0.04)));
            }
            player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.CAUSE_ENTITY_ATTACK, damage));
        }
    }

    @Override
    public Item[] getDrops(){
        return this.lastDamageCause instanceof EntityDamageByEntityEvent ? new Item[]{Item.get(Item.STRING, 0, Utils.rand(0, 3))} : new Item[0];
    }

}