package com.mutool.javafx.core.util.javafx;

import com.mutool.javafx.core.util.FileUtil;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.Imaging;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * @ClassName: ImageUtil
 * @Description: 图片工具类
 * @author: xufeng
 * @date: 2017/12/28 0028 22:03
 */
@Getter
@Setter
public class ImageUtil {

    /**
     * 获取图片BufferedImage
     * @param path 图片路径
     */
    public static BufferedImage getBufferedImage(String path) {
        return getBufferedImage(new File(path));
    }

    public static BufferedImage getBufferedImage(File file) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = Imaging.getBufferedImage(file);
        } catch (Exception e) {
            try {
                bufferedImage = ImageIO.read(file);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return bufferedImage;
    }

    /**
     * 获取javafx图片
     * @param path 图片路径
     */
    public static Image getFXImage(String path) {
        return getFXImage(new File(path));
    }

    public static Image getFXImage(File file) {
        Image image = null;
        try {
            image = SwingFXUtils.toFXImage(Imaging.getBufferedImage(file), null);
        } catch (Exception e) {
            image = new Image("file:" + file.getAbsolutePath());
        }
        return image;
    }

    public static Image getFXImage(byte[] bytes) {
        Image image = null;
        try {
            image = SwingFXUtils.toFXImage(Imaging.getBufferedImage(bytes), null);
        } catch (Exception e) {
            image = new Image(new ByteArrayInputStream(bytes));
        }
        return image;
    }


    /**
     * 保存图片
     * @param image
     * @param file
     */
    public static void writeImage(Image image, File file) throws Exception{
        writeImage(SwingFXUtils.fromFXImage(image, null),file);
    }

    public static void writeImage(BufferedImage bufferedImage, File file) throws Exception{
        try {
            Imaging.writeImage(bufferedImage,file, ImageFormats.valueOf(FileUtil.getFileSuffixName(file).toUpperCase()),null);
        } catch (Exception e) {
            e.printStackTrace();
            ImageIO.write(bufferedImage, FileUtil.getFileSuffixName(file),file);
        }
    }
}
