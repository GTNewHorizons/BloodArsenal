package com.arc.bloodarsenal.common.tinkers;

import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;

import com.arc.bloodarsenal.common.BloodArsenal;
import com.google.common.base.Throwables;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public abstract class TextureResourcePackBase implements IResourcePack, IResourceManagerReloadListener {

    public static List<IResourcePack> packs;
    protected static DirectColorModel rgb = new DirectColorModel(32, 16711680, '\uff00', 255, -16777216);
    protected final String name;
    public HashMap<ResourceLocation, byte[]> cachedImages = new HashMap();
    protected IResourcePack delegate;
    protected List<IResourcePack> resourcePackz = null;

    public TextureResourcePackBase(String name) {
        this.name = name.toLowerCase();
        this.delegate = FMLClientHandler.instance().getResourcePackFor("TConstruct");
    }

    public int brightness(int col) {
        return this.brightness(rgb.getRed(col), rgb.getGreen(col), rgb.getBlue(col));
    }

    public int brightness(int r, int g, int b) {
        return (int) ((float) r * 0.2126F + (float) g * 0.7152F + (float) b * 0.0722F);
    }

    public void register() {
        List packs = this.getiResourcePacks();
        packs.add(this);
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
        MinecraftForge.EVENT_BUS.register(this);
        BloodArsenal.logger
                .info("Registered TCon Resource Pack (" + this.name + ") - " + this.getClass().getSimpleName());
    }

    public List<IResourcePack> getiResourcePacks() {
        List packs1 = packs;
        if (packs1 == null) {
            packs1 = (List) ObfuscationReflectionHelper.getPrivateValue(
                    FMLClientHandler.class,
                    FMLClientHandler.instance(),
                    new String[] { "resourcePackList" });
        }

        return packs1;
    }

    public InputStream getStream(ResourceLocation location) {
        InputStream stream = null;
        Iterator i$ = this.getPacks().iterator();

        while (i$.hasNext()) {
            IResourcePack iResourcePack = (IResourcePack) i$.next();
            if (iResourcePack.resourceExists(location)) {
                try {
                    stream = iResourcePack.getInputStream(location);
                } catch (IOException var6) {}
            }
        }

        return stream;
    }

    public List<IResourcePack> getPacks() {
        if (this.resourcePackz == null) {
            this.resourcePackz = new ArrayList();
            this.resourcePackz.add(this.delegate);
            List t = Minecraft.getMinecraft().getResourcePackRepository().getRepositoryEntries();
            Iterator i$ = t.iterator();

            while (i$.hasNext()) {
                ResourcePackRepository.Entry entry = (ResourcePackRepository.Entry) i$.next();
                IResourcePack resourcePack = entry.getResourcePack();
                if (resourcePack.getResourceDomains().contains("tinker")) {
                    this.resourcePackz.add(resourcePack);
                }
            }
        }

        return this.resourcePackz;
    }

    public InputStream getInputStream(ResourceLocation p_110590_1_) throws IOException {
        byte[] bytes = (byte[]) this.cachedImages.get(p_110590_1_);
        if (bytes == null) {
            ResourceLocation location = new ResourceLocation(
                    "tinker",
                    p_110590_1_.getResourcePath().replace(this.name, ""));
            InputStream inputStream = this.getStream(location);
            if (inputStream == null) {
                location = new ResourceLocation("tinker", p_110590_1_.getResourcePath().replace(this.name, "iron"));
                inputStream = this.getStream(location);
            }

            if (inputStream == null) {
                location = new ResourceLocation("tinker", p_110590_1_.getResourcePath().replace(this.name, "stone"));
                inputStream = this.getStream(location);
            }

            if (inputStream == null) {
                return this.delegate.getInputStream(p_110590_1_);
            }

            BufferedImage bufferedimage;
            try {
                bufferedimage = ImageIO.read(inputStream);
            } catch (IOException var9) {
                throw Throwables.propagate(var9);
            }

            BufferedImage image;
            try {
                image = this.modifyImage(bufferedimage);
            } catch (Throwable var8) {
                var8.printStackTrace();
                return this.delegate.getInputStream(location);
            }

            bytes = writePng(image);
            this.cachedImages.put(p_110590_1_, bytes);
        }

        return new ByteArrayInputStream(bytes);
    }

    public abstract BufferedImage modifyImage(BufferedImage var1);

    public boolean resourceExists(ResourceLocation p_110589_1_) {
        if (!"tinker".equals(p_110589_1_.getResourceDomain())) {
            return false;
        } else {
            String resourcePath = p_110589_1_.getResourcePath();
            return resourcePath.startsWith("textures/items/") && resourcePath.endsWith(".png")
                    ? (this.delegate.resourceExists(p_110589_1_) ? false
                            : (!resourcePath.contains(this.name) ? false
                                    : this.delegate.resourceExists(
                                            new ResourceLocation("tinker", resourcePath.replace(this.name, "stone")))
                                            || this.delegate.resourceExists(
                                                    new ResourceLocation(
                                                            "tinker",
                                                            resourcePath.replace(this.name, "iron")))
                                            || this.delegate.resourceExists(
                                                    new ResourceLocation(
                                                            "tinker",
                                                            resourcePath.replace(this.name, "")))))
                    : false;
        }
    }

    public Set getResourceDomains() {
        return this.delegate.getResourceDomains();
    }

    public IMetadataSection getPackMetadata(IMetadataSerializer p_135058_1_, String p_135058_2_) throws IOException {
        return null;
    }

    public BufferedImage getPackImage() throws IOException {
        return null;
    }

    public String getPackName() {
        return "BA_Delegate_Pack";
    }

    public void onResourceManagerReload(IResourceManager p_110549_1_) {
        this.cachedImages.clear();
        this.resourcePackz = null;
    }

    // The reload listener runs after the atlas is stitched, so clear here too or a resource pack change would reuse the
    // old images.
    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        this.cachedImages.clear();
    }

    // Minecraft only decodes these again, so a plain RGBA PNG with fast compression is enough. ImageIO is a lot slower.
    protected static byte[] writePng(BufferedImage image) throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] argb = image.getRGB(0, 0, width, height, null, 0, width);
        byte[] raw = new byte[height * (width * 4 + 1)];
        int pos = 0;
        for (int y = 0; y < height; y++) {
            raw[pos++] = 0;
            for (int x = 0; x < width; x++) {
                int pixel = argb[y * width + x];
                raw[pos++] = (byte) (pixel >>> 16);
                raw[pos++] = (byte) (pixel >>> 8);
                raw[pos++] = (byte) pixel;
                raw[pos++] = (byte) (pixel >>> 24);
            }
        }

        Deflater deflater = new Deflater(Deflater.BEST_SPEED);
        ByteArrayOutputStream compressed = new ByteArrayOutputStream(raw.length / 2 + 64);
        try {
            deflater.setInput(raw);
            deflater.finish();
            byte[] buffer = new byte[8192];
            while (!deflater.finished()) {
                compressed.write(buffer, 0, deflater.deflate(buffer));
            }
        } finally {
            deflater.end();
        }

        ByteArrayOutputStream header = new ByteArrayOutputStream(13);
        DataOutputStream headerData = new DataOutputStream(header);
        headerData.writeInt(width);
        headerData.writeInt(height);
        // 8 bits per channel, RGBA, no interlace
        headerData.write(new byte[] { 8, 6, 0, 0, 0 });

        ByteArrayOutputStream stream = new ByteArrayOutputStream(compressed.size() + 64);
        DataOutputStream out = new DataOutputStream(stream);
        out.write(new byte[] { (byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n' });
        writeChunk(out, "IHDR", header.toByteArray());
        writeChunk(out, "IDAT", compressed.toByteArray());
        writeChunk(out, "IEND", new byte[0]);
        return stream.toByteArray();
    }

    private static void writeChunk(DataOutputStream out, String type, byte[] content) throws IOException {
        byte[] typeBytes = type.getBytes("US-ASCII");
        out.writeInt(content.length);
        out.write(typeBytes);
        out.write(content);
        CRC32 crc = new CRC32();
        crc.update(typeBytes);
        crc.update(content);
        out.writeInt((int) crc.getValue());
    }
}
