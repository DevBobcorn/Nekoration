package com.devbobcorn.nekoration.common.event;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.Explosion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Shakaraka {
	@SubscribeEvent
    public void letsrock(ServerChatEvent event)
    {
        if(event.getMessage().equals("Bye")) {
            event.setCanceled(true);
            PlayerEntity player = event.getPlayer();
            EventShakaraka ev = new EventShakaraka(player);
            MinecraftForge.EVENT_BUS.post(ev);
            if(ev.getResult() == Result.ALLOW) {
                List<Entity> list = player.level.getEntities(player, AxisAlignedBB.of(new MutableBoundingBox((int)player.xo - 30, (int)player.yo - 20, (int)player.zo - 30, (int)player.xo + 30, (int)player.yo + 20, (int)player.zo + 30)), LIVING);
                for(Iterator<Entity> iterator = list.iterator();iterator.hasNext();){
                    Entity entity = iterator.next();
                    player.level.explode(player, entity.xo, entity.yo - 1, entity.zo, 4f, Explosion.Mode.DESTROY); 
                }
            }
        }
    }

	public static final Predicate<Entity> LIVING = new Predicate<Entity>() {
		@Override
		public boolean test(Entity s) {
			return s instanceof LivingEntity;
		}
	};
 
	public class EventShakaraka extends Event{
		public final PlayerEntity player;

		public EventShakaraka(PlayerEntity p){
			super();
			this.player = p;
		}
	}

    @SubscribeEvent
    public void shakaraka(EventShakaraka event) {
		event.player.sendMessage(ITextComponent.nullToEmpty("Shakaraka!"), event.player.getUUID());
        event.setResult(Result.ALLOW);
    }
}
