package com.eagle.adventurersalchemy.networking;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by AEnterprise
 */
public class MessageByteBuff implements IMessage, IMessageHandler<MessageByteBuff, IMessage> {

	public ISynchronizedTile tile;
	public int x, y, z, identifier;

	public MessageByteBuff() {
	}

	public MessageByteBuff(ISynchronizedTile tile) {
		this.tile = tile;
		x = tile.getX();
		y = tile.getY();
		z = tile.getZ();
		identifier = tile.getIdentifier();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		identifier = buf.readInt();
		if (FMLClientHandler.instance().getClient().theWorld != null) {
			TileEntity entity = FMLClientHandler.instance().getClient().theWorld.getTileEntity(x, y, z);
			if (entity instanceof ISynchronizedTile) {
				tile = (ISynchronizedTile) entity;
				if (identifier != tile.getIdentifier())
					return;
				try {
					tile.readFromByteBuff(buf);
				} catch (Throwable t) {
					System.out.println("Error while reading message, TE:" + entity.getClass()); //maybe replace this with a propper logger later?
				}
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(identifier);
		tile.writeToByteBuff(buf);
	}

	@Override
	public IMessage onMessage(MessageByteBuff message, MessageContext ctx) {
		//don't do anything, message delivery has already been handled
		return null;
	}
}