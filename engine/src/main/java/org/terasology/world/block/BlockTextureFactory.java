package org.terasology.world.block;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/*
 * Class to generate the blocks' image.
 * 
 */
public final class BlockTextureFactory {

  private static final int MAXLENGTH = 16;
  private static final int RESOLUTION = 16;

  private static BlockTextureFactory fact;
  private HashMap<String, BufferedImage> pics;

  private BlockTextureFactory() {
    pics = new HashMap<String, BufferedImage>();
  }



  public static BlockTextureFactory getFactory() {
    if (fact == null)
      fact = new BlockTextureFactory();
    return fact;
  }

  /**
   * Defaults the pos an total parameters to 1.
   */
  public BufferedImage getImage(int line1, int line2) {
    return getImage(line1, line2, 1, 1);
  }

  /**
   * Returns the image for the block stored in the map, if it doesn't exist it's
   * created
   * 
   * @param line1
   *          Length of the first line of code
   * @param line2
   *          Length of the second line
   * @param pos
   *          Position of the texture in the building if the width is bigger
   *          than one.
   * @param total
   *          Width of the building
   * 
   * @return Texture image
   */
  public BufferedImage getImage(int line1, int line2, int pos, int total) {
    line1 = trimLine(line1, pos, total);
    line2 = trimLine(line2, pos, total);
    if (!pics.containsKey(line1 + "_" + line2)) {
      createImage(line1, line2);
    }
    return pics.get(line1 + "_" + line2);
  }

  private void createImage(int line1, int line2) {
    String text = generateImageText(line1, line2);

    Font font = new Font("Arial", Font.PLAIN, 3);
    BufferedImage image = new BufferedImage(RESOLUTION, RESOLUTION, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = image.createGraphics();
    g2d.setFont(font);
    FontMetrics fm = g2d.getFontMetrics();
    g2d.setColor(Color.WHITE);
    g2d.fillRect(0, 0, 16, 16);
    g2d.setColor(Color.BLACK);

    String[] split = text.split("\n");
    int y = 0;
    for (String str : split) {
      g2d.drawString(str, 0, y += fm.getAscent());
    }
    g2d.dispose();

    pics.put(line1 + "_" + line2, image);
  }

  private String generateImageText(int line1, int line2) {
    StringBuilder sb = new StringBuilder();
    sb.append(" \n");
    for (int i = 0; i < line1; i++) {
      sb.append("�");
    }
    sb.append("\n \n");
    for (int i = 0; i < line2; i++) {
      sb.append("�");
    }
    sb.append("\n ");
    return sb.toString();
  }

  private int trimLine(int line, int pos, int total) {
    double max = MAXLENGTH;
    if (line + 1 < max / total * (pos - 1)) {
      return 0;
    } else if (line + 1 < max / total * (pos)) {
      return (int) (total * (line + 1 - max / total * (pos - 1)));
    } else {
      return (int) MAXLENGTH;
    }
  }

  /**
   * Paints the texture to the desired colour for metrics.
   * 
   * @param pic
   *          Picture to paint.
   * @param colour
   *          Color to paint the picture.
   * @return Painted picture.
   */
  public BufferedImage paintTexture(BufferedImage pic, String colour) {
    //TODO implement.
    return null;
  }
}
