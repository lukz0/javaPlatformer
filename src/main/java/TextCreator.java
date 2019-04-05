import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class TextCreator {
    private final String filepath;

    private final ByteBuffer ttf;
    private final STBTTFontinfo info;

    private final int ascent, descent, lineGap;

    TextCreator(String filepath) {
        this.filepath = filepath;
        this.ttf = this.ioResourceToByteBuffer();
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

    ByteBuffer createBitmap() {
        final int BITMAP_W = 1000, BITMAP_H = 1000;
        ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_W*BITMAP_H);
        STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);
        stbtt_BakeFontBitmap(this.ttf, 24, bitmap, BITMAP_W, BITMAP_H, 32, cdata);
        return bitmap;
    }

    private ByteBuffer ioResourceToByteBuffer() {
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
    }
}
