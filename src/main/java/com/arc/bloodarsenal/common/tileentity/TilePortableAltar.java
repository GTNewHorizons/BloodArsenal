package com.arc.bloodarsenal.common.tileentity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

import WayofTime.alchemicalWizardry.AlchemicalWizardry;
import WayofTime.alchemicalWizardry.api.altarRecipeRegistry.AltarRecipe;
import WayofTime.alchemicalWizardry.api.altarRecipeRegistry.AltarRecipeRegistry;
import WayofTime.alchemicalWizardry.api.items.interfaces.IBloodOrb;
import WayofTime.alchemicalWizardry.api.soulNetwork.SoulNetworkHandler;
import WayofTime.alchemicalWizardry.api.tile.IBloodAltar;
import WayofTime.alchemicalWizardry.common.spell.complex.effect.SpellHelper;

public class TilePortableAltar extends TileEntity implements IInventory, IFluidTank, IFluidHandler, IBloodAltar {

    public static final int sizeInv = 1;
    private ItemStack[] inv;
    private int resultID;
    private int resultDamage;
    private int upgradeLevel;
    public FluidStack fluid;
    public int capacity;
    private boolean isActive;
    public int liquidRequired; // mB
    private boolean canBeFilled;
    public int consumptionRate;
    private int drainRate;
    public float consumptionMultiplier;
    private float efficiencyMultiplier;
    private float sacrificeEfficiencyMultiplier;
    private float selfSacrificeEfficiencyMultiplier;
    private float capacityMultiplier;
    private float orbCapacityMultiplier;
    private float dislocationMultiplier;
    private boolean isUpgraded;
    private boolean isResultBlock;
    private int bufferCapacity;
    public FluidStack fluidOutput;
    public FluidStack fluidInput;
    public int progress;
    private int cooldownAfterCrafting = 500;

    public int speedUpgrades = 0;
    public int efficiencyUpgrades = 0;
    public int sacrificeUpgrades = 0;
    public int selfSacrificeUpgrades = 0;
    public int displacementUpgrades = 0;
    public int altarCapacitiveUpgrades = 0;
    public int orbCapacitiveUpgrades = 0;
    public int betterCapacitiveUpgrades = 0;
    public int accelerationUpgrades = 0;

    private int lockdownDuration;
    private int demonBloodDuration;

    public TilePortableAltar() {
        this.inv = new ItemStack[1];
        resultID = 0;
        resultDamage = 0;
        this.capacity = FluidContainerRegistry.BUCKET_VOLUME * 10;
        fluid = new FluidStack(AlchemicalWizardry.lifeEssenceFluid, 0);
        fluidOutput = new FluidStack(AlchemicalWizardry.lifeEssenceFluid, 0);
        fluidInput = new FluidStack(AlchemicalWizardry.lifeEssenceFluid, 0);
        bufferCapacity = FluidContainerRegistry.BUCKET_VOLUME;
        isActive = false;
        consumptionRate = 0;
        drainRate = 0;
        consumptionMultiplier = 0;
        efficiencyMultiplier = 0;
        capacityMultiplier = 1;
        isUpgraded = false;
        upgradeLevel = 0;
        isResultBlock = false;
        progress = 0;
        this.lockdownDuration = 0;
        this.demonBloodDuration = 0;
    }

    /**
     *
     * @return Amount filled
     */
    public int fillMainTank(int amount) // TODO
    {
        int filledAmount = Math.min(capacity - fluid.amount, amount);
        fluid.amount += filledAmount;

        return filledAmount;
    }

    public int getRSPowerOutput() {
        return 5;
    }

    public void addToDemonBloodDuration(int dur) {
        this.demonBloodDuration++;
    }

    public boolean hasDemonBlood() {
        return this.demonBloodDuration > 0;
    }

    public void decrementDemonBlood() {
        this.demonBloodDuration = Math.max(0, this.demonBloodDuration - 1);
    }

    public void readNBTOnPlace(NBTTagCompound tag) {
        fluid.amount = tag.getInteger("currentEssence");
        upgradeLevel = tag.getInteger("upgradeLevel");
        isActive = tag.getBoolean("isActive");
        liquidRequired = tag.getInteger("liquidRequired");
        canBeFilled = tag.getBoolean("canBeFilled");
        isUpgraded = tag.getBoolean("isUpgraded");
        consumptionRate = tag.getInteger("consumptionRate");
        drainRate = tag.getInteger("drainRate");
        consumptionMultiplier = tag.getFloat("consumptionMultiplier");
        efficiencyMultiplier = tag.getFloat("efficiencyMultiplier");
        selfSacrificeEfficiencyMultiplier = tag.getFloat("selfSacrificeEfficiencyMultiplier");
        sacrificeEfficiencyMultiplier = tag.getFloat("sacrificeEfficiencyMultiplier");
        capacityMultiplier = tag.getFloat("capacityMultiplier");
        orbCapacityMultiplier = tag.getFloat("orbCapacityMultiplier");
        dislocationMultiplier = tag.getFloat("dislocationMultiplier");
        capacity = tag.getInteger("capacity");
        bufferCapacity = tag.getInteger("bufferCapacity");
        progress = tag.getInteger("progress");
        isResultBlock = tag.getBoolean("isResultBlock");
        lockdownDuration = tag.getInteger("lockdownDuration");
        accelerationUpgrades = tag.getInteger("accelerationUpgrades");
        demonBloodDuration = tag.getInteger("demonBloodDuration");
        cooldownAfterCrafting = tag.getInteger("cooldownAfterCrafting");
    }

    public void writeNBTOnHarvest(NBTTagCompound tag) {
        tag.setInteger("currentEssence", fluid.amount);
        tag.setInteger("upgradeLevel", upgradeLevel);
        tag.setBoolean("isActive", isActive);
        tag.setInteger("liquidRequired", liquidRequired);
        tag.setBoolean("canBeFilled", canBeFilled);
        tag.setBoolean("isUpgraded", isUpgraded);
        tag.setInteger("consumptionRate", consumptionRate);
        tag.setInteger("drainRate", drainRate);
        tag.setFloat("consumptionMultiplier", consumptionMultiplier);
        tag.setFloat("efficiencyMultiplier", efficiencyMultiplier);
        tag.setFloat("sacrificeEfficiencyMultiplier", sacrificeEfficiencyMultiplier);
        tag.setFloat("selfSacrificeEfficiencyMultiplier", selfSacrificeEfficiencyMultiplier);
        tag.setBoolean("isResultBlock", isResultBlock);
        tag.setFloat("capacityMultiplier", capacityMultiplier);
        tag.setFloat("orbCapacityMultiplier", orbCapacityMultiplier);
        tag.setFloat("dislocationMultiplier", dislocationMultiplier);
        tag.setInteger("capacity", capacity);
        tag.setInteger("progress", progress);
        tag.setInteger("bufferCapacity", bufferCapacity);
        tag.setInteger("lockdownDuration", lockdownDuration);
        tag.setInteger("accelerationUpgrades", this.accelerationUpgrades);
        tag.setInteger("demonBloodDuration", demonBloodDuration);
        tag.setInteger("cooldownAfterCrafting", cooldownAfterCrafting);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        NBTTagList tagList = par1NBTTagCompound.getTagList("Inventory", Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            int slot = tag.getByte("Slot");

            if (slot >= 0 && slot < inv.length) {
                inv[slot] = ItemStack.loadItemStackFromNBT(tag);
            }
        }

        resultID = par1NBTTagCompound.getInteger("resultID");
        resultDamage = par1NBTTagCompound.getInteger("resultDamage");

        if (!par1NBTTagCompound.hasKey("Empty")) {
            FluidStack fluid = this.fluid.loadFluidStackFromNBT(par1NBTTagCompound);

            if (fluid != null) {
                setMainFluid(fluid);
            }

            FluidStack fluidOut = new FluidStack(
                    AlchemicalWizardry.lifeEssenceFluid,
                    par1NBTTagCompound.getInteger("outputAmount"));

            if (fluidOut != null) {
                setOutputFluid(fluidOut);
            }

            FluidStack fluidIn = new FluidStack(
                    AlchemicalWizardry.lifeEssenceFluid,
                    par1NBTTagCompound.getInteger("inputAmount"));

            if (fluidIn != null) {
                setInputFluid(fluidIn);
            }
        }

        fluid.amount = par1NBTTagCompound.getInteger("currentEssence");
        upgradeLevel = par1NBTTagCompound.getInteger("upgradeLevel");
        isActive = par1NBTTagCompound.getBoolean("isActive");
        liquidRequired = par1NBTTagCompound.getInteger("liquidRequired");
        canBeFilled = par1NBTTagCompound.getBoolean("canBeFilled");
        isUpgraded = par1NBTTagCompound.getBoolean("isUpgraded");
        consumptionRate = par1NBTTagCompound.getInteger("consumptionRate");
        drainRate = par1NBTTagCompound.getInteger("drainRate");
        consumptionMultiplier = par1NBTTagCompound.getFloat("consumptionMultiplier");
        efficiencyMultiplier = par1NBTTagCompound.getFloat("efficiencyMultiplier");
        selfSacrificeEfficiencyMultiplier = par1NBTTagCompound.getFloat("selfSacrificeEfficiencyMultiplier");
        sacrificeEfficiencyMultiplier = par1NBTTagCompound.getFloat("sacrificeEfficiencyMultiplier");
        capacityMultiplier = par1NBTTagCompound.getFloat("capacityMultiplier");
        orbCapacityMultiplier = par1NBTTagCompound.getFloat("orbCapacityMultiplier");
        dislocationMultiplier = par1NBTTagCompound.getFloat("dislocationMultiplier");
        capacity = par1NBTTagCompound.getInteger("capacity");
        bufferCapacity = par1NBTTagCompound.getInteger("bufferCapacity");
        progress = par1NBTTagCompound.getInteger("progress");
        isResultBlock = par1NBTTagCompound.getBoolean("isResultBlock");
        lockdownDuration = par1NBTTagCompound.getInteger("lockdownDuration");
        accelerationUpgrades = par1NBTTagCompound.getInteger("accelerationUpgrades");
        demonBloodDuration = par1NBTTagCompound.getInteger("demonBloodDuration");
        cooldownAfterCrafting = par1NBTTagCompound.getInteger("cooldownAfterCrafting");
    }

    public void setMainFluid(FluidStack fluid) {
        this.fluid = fluid;
    }

    public void setOutputFluid(FluidStack fluid) {
        this.fluidOutput = fluid;
    }

    public void setInputFluid(FluidStack fluid) {
        this.fluidInput = fluid;
    }

    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        NBTTagList itemList = new NBTTagList();

        for (int i = 0; i < inv.length; i++) {
            if (inv[i] != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte) i);
                inv[i].writeToNBT(tag);
                itemList.appendTag(tag);
            }
        }

        par1NBTTagCompound.setInteger("resultID", resultID);
        par1NBTTagCompound.setInteger("resultDamage", resultDamage);
        par1NBTTagCompound.setTag("Inventory", itemList);

        if (fluid != null) {
            fluid.writeToNBT(par1NBTTagCompound);
        } else {
            par1NBTTagCompound.setString("Empty", "");
        }

        if (fluidOutput != null) {
            par1NBTTagCompound.setInteger("outputAmount", fluidOutput.amount);
        }

        if (fluidInput != null) {
            par1NBTTagCompound.setInteger("inputAmount", fluidInput.amount);
        }

        par1NBTTagCompound.setInteger("currentEssence", fluid.amount);
        par1NBTTagCompound.setInteger("upgradeLevel", upgradeLevel);
        par1NBTTagCompound.setBoolean("isActive", isActive);
        par1NBTTagCompound.setInteger("liquidRequired", liquidRequired);
        par1NBTTagCompound.setBoolean("canBeFilled", canBeFilled);
        par1NBTTagCompound.setBoolean("isUpgraded", isUpgraded);
        par1NBTTagCompound.setInteger("consumptionRate", consumptionRate);
        par1NBTTagCompound.setInteger("drainRate", drainRate);
        par1NBTTagCompound.setFloat("consumptionMultiplier", consumptionMultiplier);
        par1NBTTagCompound.setFloat("efficiencyMultiplier", efficiencyMultiplier);
        par1NBTTagCompound.setFloat("sacrificeEfficiencyMultiplier", sacrificeEfficiencyMultiplier);
        par1NBTTagCompound.setFloat("selfSacrificeEfficiencyMultiplier", selfSacrificeEfficiencyMultiplier);
        par1NBTTagCompound.setBoolean("isResultBlock", isResultBlock);
        par1NBTTagCompound.setFloat("capacityMultiplier", capacityMultiplier);
        par1NBTTagCompound.setFloat("orbCapacityMultiplier", orbCapacityMultiplier);
        par1NBTTagCompound.setFloat("dislocationMultiplier", dislocationMultiplier);
        par1NBTTagCompound.setInteger("capacity", capacity);
        par1NBTTagCompound.setInteger("progress", progress);
        par1NBTTagCompound.setInteger("bufferCapacity", bufferCapacity);
        par1NBTTagCompound.setInteger("lockdownDuration", lockdownDuration);
        par1NBTTagCompound.setInteger("accelerationUpgrades", this.accelerationUpgrades);
        par1NBTTagCompound.setInteger("demonBloodDuration", demonBloodDuration);
        par1NBTTagCompound.setInteger("cooldownAfterCrafting", cooldownAfterCrafting);
    }

    @Override
    public int getSizeInventory() {
        return inv.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inv[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amt) {
        ItemStack stack = getStackInSlot(slot);

        if (stack != null) {
            if (stack.stackSize <= amt) {
                setInventorySlotContents(slot, null);
            } else {
                stack = stack.splitStack(amt);

                if (stack.stackSize == 0) {
                    setInventorySlotContents(slot, null);
                }
            }
        }

        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack stack = getStackInSlot(slot);

        if (stack != null) {
            setInventorySlotContents(slot, null);
        }

        return stack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack) {
        inv[slot] = itemStack;
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

        if (itemStack != null && itemStack.stackSize > getInventoryStackLimit()) {
            itemStack.stackSize = getInventoryStackLimit();
        }
    }

    @Override
    public String getInventoryName() {
        return "TilePortableAltar";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
                && entityPlayer.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    // IFluidTank methods
    @Override
    public FluidStack getFluid() {
        return fluid;
    }

    @Override
    public int getFluidAmount() {
        if (fluid == null) {
            return 0;
        }

        return fluid.amount;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public int getCurrentBlood() {
        return getFluidAmount();
    }

    public void setCurrentBlood(int amount) {
        this.fluid.amount = amount;
    }

    @Override
    public int getTier() {
        return upgradeLevel;
    }

    public void setTier(int tier) {
        this.upgradeLevel = tier;
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public float getSacrificeMultiplier() {
        return sacrificeEfficiencyMultiplier;
    }

    public void setSacrificeMultiplier(float sacrificeMultiplier) {
        this.sacrificeEfficiencyMultiplier = sacrificeMultiplier;
    }

    @Override
    public float getSelfSacrificeMultiplier() {
        return selfSacrificeEfficiencyMultiplier;
    }

    public void setSelfSacrificeMultiplier(float selfSacrificeMultiplier) {
        this.selfSacrificeEfficiencyMultiplier = selfSacrificeMultiplier;
    }

    @Override
    public float getOrbMultiplier() {
        return orbCapacityMultiplier;
    }

    public void setOrbMultiplier(float orbMultiplier) {
        this.orbCapacityMultiplier = orbMultiplier;
    }

    @Override
    public float getDislocationMultiplier() {
        return dislocationMultiplier;
    }

    public void setDislocationMultiplier(float dislocationMultiplier) {
        this.dislocationMultiplier = dislocationMultiplier;
    }

    @Override
    public int getBufferCapacity() {
        return bufferCapacity;
    }

    public void setBufferCapacity(int bufferCapacity) {
        this.bufferCapacity = bufferCapacity;
    }

    public void setConsumptionMultiplier(float consumptionMultiplier) {
        this.consumptionMultiplier = consumptionMultiplier;
    }

    public void setEfficiencyMultiplier(float efficiencyMultiplier) {
        this.efficiencyMultiplier = efficiencyMultiplier;
    }

    public void setCapacityMultiplier(float capacityMultiplier) {
        this.capacityMultiplier = capacityMultiplier;
    }

    public void setUpgrades(int speedUpgrades, int efficiencyUpgrades, int sacrificeUpgrades, int selfSacrificeUpgrades,
            int displacementUpgrades, int altarCapacitiveUpgrades, int orbCapacitiveUpgrades,
            int betterCapacitiveUpgrades, int accelerationUpgrades) {
        this.speedUpgrades = speedUpgrades;
        this.efficiencyUpgrades = efficiencyUpgrades;
        this.sacrificeUpgrades = sacrificeUpgrades;
        this.selfSacrificeUpgrades = selfSacrificeUpgrades;
        this.displacementUpgrades = displacementUpgrades;
        this.altarCapacitiveUpgrades = altarCapacitiveUpgrades;
        this.orbCapacitiveUpgrades = orbCapacitiveUpgrades;
        this.betterCapacitiveUpgrades = betterCapacitiveUpgrades;
        this.accelerationUpgrades = accelerationUpgrades;
    }

    @Override
    public FluidTankInfo getInfo() {
        return new FluidTankInfo(this);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        TileEntity tile = this;

        if (resource == null) {
            return 0;
        }

        if (resource.getFluid().getID()
                != (new FluidStack(AlchemicalWizardry.lifeEssenceFluid, 1)).getFluid().getID()) {
            return 0;
        }

        if (!doFill) {
            if (fluidInput == null) {
                return Math.min(bufferCapacity, resource.amount);
            }

            if (!fluidInput.isFluidEqual(resource)) {
                return 0;
            }

            return Math.min(bufferCapacity - fluidInput.amount, resource.amount);
        }

        if (fluidInput == null) {
            fluidInput = new FluidStack(resource, Math.min(bufferCapacity, resource.amount));

            FluidEvent.fireEvent(
                    new FluidEvent.FluidFillingEvent(
                            fluidInput,
                            tile.getWorldObj(),
                            tile.xCoord,
                            tile.yCoord,
                            tile.zCoord,
                            this,
                            fluidInput.amount));

            return fluidInput.amount;
        }

        if (!fluidInput.isFluidEqual(resource)) {
            return 0;
        }

        int filled = bufferCapacity - fluidInput.amount;

        if (resource.amount < filled) {
            fluidInput.amount += resource.amount;
            filled = resource.amount;
        } else {
            fluidInput.amount = bufferCapacity;
        }

        FluidEvent.fireEvent(
                new FluidEvent.FluidFillingEvent(
                        fluidInput,
                        tile.getWorldObj(),
                        tile.xCoord,
                        tile.yCoord,
                        tile.zCoord,
                        this,
                        filled));

        return filled;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (fluidOutput == null) {
            return null;
        }

        int drained = maxDrain;

        if (fluidOutput.amount < drained) {
            drained = fluidOutput.amount;
        }

        FluidStack stack = new FluidStack(fluidOutput, drained);

        if (doDrain) {
            fluidOutput.amount -= drained;

            if (fluidOutput.amount <= 0) {
                fluidOutput = null;
            }

            FluidEvent.fireEvent(
                    new FluidEvent.FluidDrainingEvent(
                            fluidOutput,
                            this.worldObj,
                            this.xCoord,
                            this.yCoord,
                            this.zCoord,
                            this,
                            drained));

        }

        if (fluidOutput == null) {
            fluidOutput = new FluidStack(AlchemicalWizardry.lifeEssenceFluid, 0);
        }

        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }

        return stack;
    }

    // Logic for the actual block is under here
    @Override
    public void updateEntity() {
        this.decrementDemonBlood();

        if (this.hasDemonBlood() && !worldObj.isRemote) {
            SpellHelper.sendIndexedParticleToAllAround(
                    worldObj,
                    xCoord,
                    yCoord,
                    zCoord,
                    20,
                    worldObj.provider.dimensionId,
                    1,
                    xCoord,
                    yCoord,
                    zCoord);
        }

        if (this.lockdownDuration > 0) {
            this.lockdownDuration--;
        }

        if (!worldObj.isRemote && worldObj.getWorldTime() % 20 == 0) {
            {
                Block block = worldObj.getBlock(xCoord + 1, yCoord, zCoord);
                block.onNeighborBlockChange(worldObj, xCoord + 1, yCoord, zCoord, block);
                block = worldObj.getBlock(xCoord - 1, yCoord, zCoord);
                block.onNeighborBlockChange(worldObj, xCoord - 1, yCoord, zCoord, block);
                block = worldObj.getBlock(xCoord, yCoord + 1, zCoord);
                block.onNeighborBlockChange(worldObj, xCoord, yCoord + 1, zCoord, block);
                block = worldObj.getBlock(xCoord, yCoord - 1, zCoord);
                block.onNeighborBlockChange(worldObj, xCoord, yCoord - 1, zCoord, block);
                block = worldObj.getBlock(xCoord, yCoord, zCoord + 1);
                block.onNeighborBlockChange(worldObj, xCoord, yCoord, zCoord + 1, block);
                block = worldObj.getBlock(xCoord, yCoord, zCoord - 1);
                block.onNeighborBlockChange(worldObj, xCoord, yCoord, zCoord - 1, block);
            }

            if (AlchemicalWizardry.lockdownAltar) {
                List<EntityPlayer> list = SpellHelper
                        .getPlayersInRange(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, 15, 15);
                for (EntityPlayer player : list) {
                    PotionEffect regenEffect = player.getActivePotionEffect(Potion.regeneration);
                    if (regenEffect != null && regenEffect.getAmplifier() >= 2) {
                        this.lockdownDuration += 20;
                    }
                }
            }

            if (AlchemicalWizardry.causeHungerWithRegen) {
                List<EntityPlayer> list = SpellHelper
                        .getPlayersInRange(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, 15, 15);
                for (EntityPlayer player : list) {
                    PotionEffect regenEffect = player.getActivePotionEffect(Potion.regeneration);
                    if (regenEffect != null && regenEffect.getAmplifier() > 0) {
                        player.addPotionEffect(
                                new PotionEffect(Potion.hunger.id, 40, regenEffect.getAmplifier() * 2 - 2));
                    }
                }
            }
        }

        if (worldObj.getWorldTime() % Math.max(20 - this.accelerationUpgrades, 1) == 0) {
            int syphonMax = (int) (20 * this.dislocationMultiplier);
            int fluidInputted;
            int fluidOutputted;
            fluidInputted = Math.min(syphonMax, -this.fluid.amount + capacity);
            fluidInputted = Math.min(this.fluidInput.amount, fluidInputted);
            this.fluid.amount += fluidInputted;
            this.fluidInput.amount -= fluidInputted;
            fluidOutputted = Math.min(syphonMax, this.bufferCapacity - this.fluidOutput.amount);
            fluidOutputted = Math.min(this.fluid.amount, fluidOutputted);
            this.fluidOutput.amount += fluidOutputted;
            this.fluid.amount -= fluidOutputted;
        }

        if (worldObj.getWorldTime() % 100 == 0 && (this.isActive || this.cooldownAfterCrafting <= 0)) {
            startCycle();
        }

        if (!isActive) {
            if (cooldownAfterCrafting > 0) {
                cooldownAfterCrafting--;
            }
            return;
        }

        if (getStackInSlot(0) == null) {
            return;
        }

        int worldTime = (int) (worldObj.getWorldTime() % 24000);

        if (worldObj.isRemote) {
            return;
        }

        if (worldTime % 1 == 0) {
            if (!canBeFilled) {
                if (fluid != null && fluid.amount >= 1) {
                    int stackSize = getStackInSlot(0).stackSize;
                    int liquidDrained = Math.min(
                            (int) (upgradeLevel >= 2 ? consumptionRate * (1 + consumptionMultiplier) : consumptionRate),
                            fluid.amount);

                    if (liquidDrained > (liquidRequired * stackSize - progress)) {
                        liquidDrained = liquidRequired * stackSize - progress;
                    }

                    fluid.amount = fluid.amount - liquidDrained;
                    progress += liquidDrained;

                    if (worldTime % 4 == 0) {
                        SpellHelper.sendIndexedParticleToAllAround(
                                worldObj,
                                xCoord,
                                yCoord,
                                zCoord,
                                20,
                                worldObj.provider.dimensionId,
                                1,
                                xCoord,
                                yCoord,
                                zCoord);
                    }

                    if (progress >= liquidRequired * stackSize) {
                        ItemStack result;
                        result = AltarRecipeRegistry.getItemForItemAndTier(this.getStackInSlot(0), this.upgradeLevel);
                        if (result != null) {
                            result.stackSize *= stackSize;
                        }

                        setInventorySlotContents(0, result);
                        progress = 0;

                        for (int i = 0; i < 8; i++) {
                            SpellHelper.sendIndexedParticleToAllAround(
                                    worldObj,
                                    xCoord,
                                    yCoord,
                                    zCoord,
                                    20,
                                    worldObj.provider.dimensionId,
                                    4,
                                    xCoord + 0.5f,
                                    yCoord + 1.0f,
                                    zCoord + 0.5f);
                        }
                        this.isActive = false;
                    }
                } else if (progress > 0) {
                    progress -= (int) (efficiencyMultiplier * drainRate);

                    if (worldTime % 2 == 0) {
                        SpellHelper.sendIndexedParticleToAllAround(
                                worldObj,
                                xCoord,
                                yCoord,
                                zCoord,
                                20,
                                worldObj.provider.dimensionId,
                                2,
                                xCoord,
                                yCoord,
                                zCoord);
                    }
                }
            } else {
                ItemStack returnedItem = getStackInSlot(0);

                if (!(returnedItem.getItem() instanceof IBloodOrb)) {
                    return;
                }

                IBloodOrb item = (IBloodOrb) (returnedItem.getItem());
                NBTTagCompound itemTag = returnedItem.stackTagCompound;

                if (itemTag == null) {
                    return;
                }

                String ownerName = itemTag.getString("ownerName");

                if (ownerName.equals("")) {
                    return;
                }

                if (fluid != null && fluid.amount >= 1) {
                    int liquidDrained = Math.min(
                            (int) (upgradeLevel >= 2 ? consumptionRate * (1 + consumptionMultiplier) : consumptionRate),
                            fluid.amount);

                    int drain = SoulNetworkHandler.addCurrentEssenceToMaximum(
                            ownerName,
                            liquidDrained,
                            (int) (item.getMaxEssence() * this.orbCapacityMultiplier));

                    fluid.amount = fluid.amount - drain;

                    if (worldTime % 4 == 0) {
                        SpellHelper.sendIndexedParticleToAllAround(
                                worldObj,
                                xCoord,
                                yCoord,
                                zCoord,
                                20,
                                worldObj.provider.dimensionId,
                                3,
                                xCoord,
                                yCoord,
                                zCoord);
                    }
                }
            }
            if (worldObj != null) {
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        }
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isActive() {
        return isActive;
    }

    public void sacrificialDaggerCall(int amount, boolean isSacrifice) {
        if (!isSacrifice && this.lockdownDuration > 0) {
            int amt = (int) Math.min(
                    bufferCapacity - fluidInput.amount,
                    (isSacrifice ? 1 + sacrificeEfficiencyMultiplier : 1 + selfSacrificeEfficiencyMultiplier) * amount);
            fluidInput.amount += amt;
        } else {
            fluid.amount += Math.min(
                    capacity - fluid.amount,
                    (isSacrifice ? 1 + sacrificeEfficiencyMultiplier : 1 + selfSacrificeEfficiencyMultiplier) * amount);
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        return PacketHandler.getPacket(this);
    }

    public void handlePacketData(int[] intData, int[] fluidData, int capacity) {
        if (intData == null) {
            return;
        }

        if (intData.length == 3) {
            for (int i = 0; i < 1; i++) {
                if (intData[i * 3 + 2] != 0) {
                    ItemStack is = new ItemStack(
                            Item.getItemById(intData[i * 3]),
                            intData[i * 3 + 2],
                            intData[i * 3 + 1]);
                    inv[i] = is;
                } else {
                    inv[i] = null;
                }
            }
        }

        FluidStack flMain = new FluidStack(fluidData[0], fluidData[1]);
        FluidStack flIn = new FluidStack(fluidData[2], fluidData[3]);
        FluidStack flOut = new FluidStack(fluidData[4], fluidData[5]);

        this.setMainFluid(flMain);
        this.setInputFluid(flIn);
        this.setOutputFluid(flOut);

        this.capacity = capacity;
    }

    public int[] buildIntDataList() {
        int[] sortList = new int[3]; // 1 * 3
        int pos = 0;

        for (ItemStack is : inv) {
            if (is != null) {
                sortList[pos++] = Item.getIdFromItem(is.getItem());
                sortList[pos++] = is.getItemDamage();
                sortList[pos++] = is.stackSize;
            } else {
                sortList[pos++] = 0;
                sortList[pos++] = 0;
                sortList[pos++] = 0;
            }
        }

        return sortList;
    }

    public void startCycle() {
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }

        this.checkAndSetAltar();

        if (fluid == null || fluid.amount <= 0) {
            return;
        }

        if (getStackInSlot(0) == null) {
            return;
        }

        if (!isActive) {
            progress = 0;
        }

        if (AltarRecipeRegistry.isRequiredItemValid(getStackInSlot(0), upgradeLevel)) {
            AltarRecipe recipe = AltarRecipeRegistry.getAltarRecipeForItemAndTier(getStackInSlot(0), upgradeLevel);
            this.isActive = true;
            this.liquidRequired = recipe.getLiquidRequired();
            this.canBeFilled = recipe.getCanBeFilled();
            this.consumptionRate = recipe.getConsumptionRate();
            this.drainRate = recipe.drainRate;
            return;
        }

        isActive = false;
    }

    public void checkAndSetAltar() {
        if (this.fluid.amount > this.capacity) {
            this.fluid.amount = this.capacity;
        }

        if (this.fluidOutput.amount > this.bufferCapacity) {
            this.fluidOutput.amount = this.bufferCapacity;
        }

        if (this.fluidInput.amount > this.bufferCapacity) {
            this.fluidInput.amount = this.bufferCapacity;
        }

        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
        return slot == 0;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        // TODO
        if (resource == null) {
            return 0;
        }

        resource = resource.copy();
        int totalUsed = 0;
        int used = this.fill(resource, doFill);
        resource.amount -= used;
        totalUsed += used;
        this.startCycle();
        return totalUsed;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null) {
            return null;
        }

        if (!resource.isFluidEqual(fluidOutput)) {
            return null;
        }

        return drain(from, resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxEmpty, boolean doDrain) {
        return this.drain(maxEmpty, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        // I changed this, since fluidstack != fluid... :p dunno if it was a accident? so you might wanna check this
        return this.fluidInput != null && this.fluid.getFluid().equals(fluidInput.getFluid());
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        FluidTank compositeTank = new FluidTank(capacity);
        compositeTank.setFluid(fluid);
        return new FluidTankInfo[] { compositeTank.getInfo() };
    }

    public int[] buildFluidList() {
        int[] sortList = new int[6];

        if (this.fluid == null) {
            sortList[0] = AlchemicalWizardry.lifeEssenceFluid.getID();
            sortList[1] = 0;
        } else {
            sortList[0] = this.fluid.getFluid().getID();
            sortList[1] = this.fluid.amount;
        }

        if (this.fluidInput == null) {
            sortList[2] = AlchemicalWizardry.lifeEssenceFluid.getID();
            sortList[3] = 0;
        } else {
            sortList[2] = this.fluidInput.getFluid().getID();
            sortList[3] = this.fluidInput.amount;
        }

        if (this.fluidOutput == null) {
            sortList[4] = AlchemicalWizardry.lifeEssenceFluid.getID();
            sortList[5] = 0;
        } else {
            sortList[4] = this.fluidOutput.getFluid().getID();
            sortList[5] = this.fluidOutput.amount;
        }

        return sortList;
    }

    public void sendChatInfoToPlayer(EntityPlayer player) {
        player.addChatMessage(
                new ChatComponentText(
                        StatCollector.translateToLocal("message.altarEssence") + " " + this.fluid.amount + "LP"));
        player.addChatMessage(
                new ChatComponentText(
                        StatCollector.translateToLocal("message.altarCurrentTier") + " " + this.upgradeLevel));
        player.addChatMessage(
                new ChatComponentText(
                        StatCollector.translateToLocal("message.capacity") + " " + this.getCapacity() + "LP"));
    }

    public void sendMoreChatInfoToPlayer(EntityPlayer player) {
        if (getStackInSlot(0) != null) {
            int stackSize = getStackInSlot(0).stackSize;
            player.addChatMessage(
                    new ChatComponentText(
                            StatCollector.translateToLocal("message.altarProgress") + " "
                                    + progress
                                    + "LP/"
                                    + liquidRequired * stackSize
                                    + "LP"));
            player.addChatMessage(
                    new ChatComponentText(
                            StatCollector.translateToLocal("message.consumptionRate") + " "
                                    + (int) (consumptionRate * (1 + consumptionMultiplier))
                                    + "LP/t"));
        }
        player.addChatMessage(
                new ChatComponentText(
                        StatCollector.translateToLocal("message.altarEssence") + " " + this.fluid.amount + "LP"));
        player.addChatMessage(
                new ChatComponentText(
                        StatCollector.translateToLocal("message.inputTank") + " " + this.fluidInput.amount + "LP"));
        player.addChatMessage(
                new ChatComponentText(
                        StatCollector.translateToLocal("message.outputTank") + " " + this.fluidOutput.amount + "LP"));
    }

    @Override
    public void requestPauseAfterCrafting(int amount) {
        if (this.isActive) {
            this.cooldownAfterCrafting = amount;
        }
    }
}
