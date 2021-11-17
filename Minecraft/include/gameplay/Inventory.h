#ifndef MINECRAFT_INVENTORY_H
#define MINECRAFT_INVENTORY_H
#include "core.h"
#include "utils/Constants.h"

namespace Minecraft
{
	struct InventorySlot
	{
		int16 blockId;
		int8 count;
	};

	struct Inventory
	{
		union
		{
			struct
			{
				InventorySlot hotbar[Player::numHotbarSlots];
				InventorySlot mainInventory[Player::numMainInventorySlots];
			};
			InventorySlot slots[Player::numHotbarSlots + Player::numMainInventorySlots];
		};
		int currentHotbarSlot;
	};
}

#endif 