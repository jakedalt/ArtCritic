package Default;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Definitions.Color;
import Definitions.ColorGroup;
import Util.ColorUtil;

public class Workspace {

	static ColorUtil cu = new ColorUtil();
	final private static String IMAGES_FOLDER = "src/Resources/Images/";
	final private static String RESOURCE_FOLDER = "src/Resources/";
	final private static String HTML_NAME = "color_html.html";

	public static void main(String[] args) {

		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(IMAGES_FOLDER + "chrome-red.jpg"));
			Color avgColor = cu.getAverageColor(img);
			if (avgColor != null) {
				updateHTMLColor(avgColor);
				System.out.println(avgColor.getName() + ": RGB(" + avgColor.getR() + ", " + avgColor.getG() + ", "
						+ avgColor.getB() + ")");
				double sdc = cu.standardDeviationColors(avgColor, img);
				System.out.println("AvgVar: " + sdc);
				ArrayList<ColorGroup> cgs = cu.getColorGroups(sdc/4, img);
				System.out.println("Num color groups: " + cgs.size());
				System.out.println("Accuracy: " + cu.standardDeviationGroup(cgs, img));
				for(ColorGroup cg : cgs) {
					Color avgColor2 = cg.getGroupAverage();
					System.out.println(avgColor2.getName() + ": RGB(" + avgColor2.getR() + ", " + avgColor2.getG() + ", "
							+ avgColor2.getB() + ")");
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}

	private static void updateHTMLColor(Color c) {

		String content = "<html>\n\t<head>\n\t\t<style>\n\t\t\tbody {\n\t\t\t\tbackground-color: rgb(" + c.getR() + ","
				+ c.getG() + "," + c.getB() + ");\n\t\t\t}\n\t\t</style>\n\t</head>\n<html>";

		File htmlFile = new File(RESOURCE_FOLDER + HTML_NAME);
		try {
			FileWriter fw = new FileWriter(htmlFile, false);
			fw.write(content);
			fw.close();
			System.out.println("HTML Overwritten");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
