package hu.open.assistant.commons.graphical;

import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;

/**
 * A versatile image used by all Assistant applications. The image can be loaded either from disk, resource, or
 * created from other image classes. The image can resize itself (aligned) and can have a white placeholder when size
 * is given and the image source is not available.
 */
public class AssImage extends ImageIcon {

	private static final Color BORDER_COLOR = AssColor.GENERIC_BORDER_GREY.getColor();
	private static final Color FILL_COLOR = Color.white;

	private final String name;
	private String path = "";

	/**
	 * Create the image from JAR resource.
	 *
	 * @param url - of the resource
	 */
	public AssImage(URL url) {
		this(url, "");
	}

	/**
	 * Create the image from JAR resource with name.
	 *
	 * @param url  - of the resource
	 * @param name - of the image
	 */
	public AssImage(URL url, String name) {
		super(url);
		this.name = name;
	}

	/**
	 * Create the image from file.
	 *
	 * @param path - of the file
	 */
	public AssImage(String path) {
		this(path, "");
	}

	/**
	 * Create the image from file with name.
	 *
	 * @param path - of the file
	 * @param name - of the image
	 */
	public AssImage(String path, String name) {
		super(path);
		this.name = name;
		this.path = path;
	}

	/**
	 * Create the image from an Image object.
	 *
	 * @param image - Image
	 */
	public AssImage(Image image) {
		this(image, "");
	}

	/**
	 * Create the image from an Image object with name.
	 *
	 * @param image - Image
	 * @param name  - of the image
	 */
	public AssImage(Image image, String name) {
		super(image);
		this.name = name;
	}

	/**
	 * Create the image from a BufferedImage object.
	 *
	 * @param image - BufferedImage
	 */
	public AssImage(BufferedImage image) {
		this(image, "");
	}

	/**
	 * Create the image from a BufferedImage object with name.
	 *
	 * @param image - Image
	 * @param name  - of the image
	 */
	public AssImage(BufferedImage image, String name) {
		super(image);
		this.name = name;
	}

	/**
	 * Return the image name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the same image with border included.
	 */
	public AssImage getBorderedImage() {
		AssImage borderedImage = null;
		if (getIconWidth() != -1 && getIconHeight() != -1) {
			int width = getIconWidth();
			int height = getIconHeight();
			BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics graphics = newImage.createGraphics();
			paintIcon(null, graphics, 0, 0);
			graphics.setColor(BORDER_COLOR);
			graphics.drawRect(0, 0, width - 1, height - 1);
			graphics.dispose();
			borderedImage = new AssImage(newImage, name);
		}
		return borderedImage;
	}

	/**
	 * Return the same image resized to the given size and centered. Fill empty area with white color and whole
	 * area if image resource is not available.
	 *
	 * @param targetWidth  - new image width
	 * @param targetHeight - new image height
	 */
	public AssImage getResizedImage(int targetWidth, int targetHeight) {
		AssImage resizedImage;
		if (getIconWidth() != -1 && getIconHeight() != -1) {
			int width = getIconWidth();
			int widthAdjust = 0;
			int height = getIconHeight();
			int heightAdjust = 0;
			if (width > height) {
				heightAdjust = width - height;
			} else if (height > width) {
				widthAdjust = height - width;
			}
			BufferedImage oldImage = new BufferedImage(width + widthAdjust, height + heightAdjust, BufferedImage.TYPE_INT_RGB);
			Graphics graphics = oldImage.createGraphics();
			graphics.setColor(FILL_COLOR);
			graphics.fillRect(0, 0, width + widthAdjust, height + heightAdjust);
			paintIcon(null, graphics, widthAdjust / 2, heightAdjust / 2);
			graphics.dispose();
			Image newImage = oldImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
			resizedImage = new AssImage(newImage, name);
		} else {
			BufferedImage emptyImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
			Graphics graphics = emptyImage.createGraphics();
			graphics.setColor(FILL_COLOR);
			graphics.fillRect(0, 0, targetWidth, targetHeight);
			graphics.dispose();
			resizedImage = new AssImage(emptyImage, name);
		}
		return resizedImage;
	}

	/**
	 * Return the image force reloaded from disk (JVM caches images loaded from disk). Use this if image was changed
	 * while program was running.
	 */
	public AssImage getReloadedImage() {
		if (!path.isBlank()) {
            getImage().flush();
            setImage(new ImageIcon(path).getImage());
        }
		return this;
	}
}
