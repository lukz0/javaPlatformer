import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class TextCreator {
    private final String filepath;

    private ByteBuffer ttf;
    private STBTTFontinfo info;

    private final int ascent, descent, lineGap;

    TextCreator(String filepath) {
        this.filepath = filepath;
        try {
            this.ttf = ioResourceToByteBuffer(this.filepath, 512*1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.info = STBTTFontinfo.create();
        if (!stbtt_InitFont(info, ttf)) {
            System.err.println("[TEXT_CREATOR] Failed to initialize font!");
        }

        try(MemoryStack stack = stackPush()) {
            IntBuffer pAscent = stack.mallocInt(1);
            IntBuffer pDescent = stack.mallocInt(1);
            IntBuffer pLineGap = stack.mallocInt(1);

            stbtt_GetFontVMetrics(this.info, pAscent, pDescent, pLineGap);

            this.ascent = pAscent.get(0);
            this.descent = pDescent.get(0);
            this.lineGap = pLineGap.get(0);
        }
    }

    Texture.BitmapAndSize createBitmap() {
        final int BITMAP_W = 1000, BITMAP_H = 1000;
        ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_W*BITMAP_H);
        STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);
        stbtt_BakeFontBitmap(this.ttf, 100, bitmap, BITMAP_W, BITMAP_H, 32, cdata);
        return new Texture.BitmapAndSize(bitmap, BITMAP_W, BITMAP_H);
    }

    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;
        File file = new File(resource);
            FileInputStream fis = new FileInputStream(file);
            FileChannel fc = fis.getChannel();
            buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            fc.close();
            fis.close();
        return buffer;
    }

    /*private ByteBuffer ioResourceToByteBuffer() {
        try {
            FileInputStream fi = new FileInputStream(this.filepath);
            FileChannel fChan = fi.getChannel();
            try {
                ByteBuffer mBuf = ByteBuffer.allocate((int)fChan.size());
                fChan.read(mBuf);
                mBuf.rewind();
                fChan.close();
                fi.close();
                return mBuf;
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    fChan.close();
                    fi.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                return null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }*/
}
