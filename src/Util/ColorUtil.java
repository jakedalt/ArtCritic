package Util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Definitions.Color;
import Definitions.ColorGroup;

public class ColorUtil {

	public Color getAverageColor(BufferedImage bi) {
		System.out.println("getAverageColor commencing...");
		Color res = new Color(0, 0, 0);
		int avgR = 0;
		int avgG = 0;
		int avgB = 0;

		long sumR = 0;
		long sumG = 0;
		long sumB = 0;

		int w = bi.getWidth();
		int h = bi.getHeight();
		long total = w * h;

		if (total > 38000000000000000L) {
			return null;
		}

		System.out.println("Total Pixels: " + total + "\nW: " + w + " x H: " + h);

		int n = 0;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				n++;
				Color pixel = new Color(bi.getRGB(i, j));
				sumR += pixel.getR();
				sumG += pixel.getG();
				sumB += pixel.getB();
			}
		}

		avgR = (int) (sumR / n);
		avgG = (int) (sumG / n);
		avgB = (int) (sumB / n);

		res = new Color(avgR, avgG, avgB);
		return res;
	}

	public double standardDeviationColors(Color avgColor, BufferedImage bi) {

		double averageVariation = 0.0;
		BigDecimal varSum = new BigDecimal(0);

		int w = bi.getWidth();
		int h = bi.getHeight();

		int n = 0;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				n++;
				Color pixel = new Color(bi.getRGB(i, j));
				varSum = varSum.add(new BigDecimal(colorDistance(pixel, avgColor)));
			}
		}

		averageVariation = (varSum.divide(new BigDecimal(n), 2, RoundingMode.HALF_UP).doubleValue());
		return averageVariation;
	}

	public double standardDeviationGroup(ArrayList<ColorGroup> cgs, BufferedImage bi) {
		double averageVariation = 0.0;
		BigDecimal varSum = new BigDecimal(0);

		int w = bi.getWidth();
		int h = bi.getHeight();

		int n = 0;

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				n++;
				Color pixel = new Color(bi.getRGB(i, j));
				double minDev = 999.0;
				for (ColorGroup cg : cgs) {
					double currDist = colorDistance(pixel, cg.getGroupAverage());
					if (currDist < minDev) {
						minDev = currDist;
					}
				}
				varSum = varSum.add(new BigDecimal(minDev));
			}
		}

		averageVariation = (varSum.divide(new BigDecimal(n), 2, RoundingMode.HALF_UP).doubleValue());
		return averageVariation;
	}

	public boolean similarColors(Color c1, Color c2, double dist) {
		double distance = Math.pow((c1.getR() - c2.getR()) * (c1.getR() - c2.getR())
				+ (c1.getG() - c2.getG()) * (c1.getG() - c2.getG()) + (c1.getB() - c2.getB()) * (c1.getB() - c2.getB()),
				0.5);
		if (distance < dist) {
			return true;
		} else {
			return false;
		}
	}

	public double colorDistance(Color c1, Color c2) {

		double distance = Math.pow((c1.getR() - c2.getR()) * (c1.getR() - c2.getR())
				+ (c1.getG() - c2.getG()) * (c1.getG() - c2.getG()) + (c1.getB() - c2.getB()) * (c1.getB() - c2.getB()),
				0.5);

		return distance;
	}

	/**
	 * Suggested diffT values: Set number of very solid colors: 1750/standard dev
	 * Many thousands unique colors: sdc * 2
	 * 
	 * @param diffThreshold
	 * @param bi
	 * @return
	 */
	public ArrayList<ColorGroup> getColorGroups(double diffThreshold, BufferedImage bi) {

		ArrayList<ColorGroup> colorGroups = new ArrayList<ColorGroup>();

		int w = bi.getWidth();
		int h = bi.getHeight();

		boolean done = false;

		while (!done) {
			colorGroups.clear();

			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					Color pixel = new Color(bi.getRGB(i, j));
					if (colorGroups.size() == 0) {
						ColorGroup ncg = new ColorGroup();
						ncg.addColor(pixel);
						colorGroups.add(ncg);
					} else {
						boolean groupFound = false;
						for (ColorGroup cg : colorGroups) {
							if (similarColors(pixel, cg.getGroupAverage(), diffThreshold)) {
								cg.addColor(pixel);
								groupFound = true;
							}
						}
						if (!groupFound) {
							ColorGroup ncg = new ColorGroup();
							ncg.addColor(pixel);
							colorGroups.add(ncg);
						}
					}
				}
			}
			if (colorGroups.size() < 32) {
				// double sdg = standardDeviationGroup(colorGroups, bi);
				done = true;
			}
			diffThreshold *= 1.25;
		}

		return colorGroups;
	}

	public void createGroupOnlyImage(ArrayList<ColorGroup> cgs, BufferedImage bi) {
		BufferedImage newImage = new BufferedImage(bi.getWidth(), bi.getHeight(), 1);

		int w = bi.getWidth();
		int h = bi.getHeight();

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				Color pixel = new Color(bi.getRGB(i, j));
				double minDev = 999.0;
				ColorGroup closestMatch = null;
				for (ColorGroup cg : cgs) {
					double currDist = colorDistance(pixel, cg.getGroupAverage());
					if (currDist < minDev) {
						minDev = currDist;
						closestMatch = cg;
					}
				}
				newImage.setRGB(i, j, closestMatch.getGroupAverage().getRGB());
			}
		}
		File outputfile = new File("groupOnly.jpg");
		try {
			ImageIO.write(newImage, "jpg", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}

	public ArrayList<String> getColorDesc(Color c) {

		ArrayList<String> descs = new ArrayList<String>();

		int avgVal = (c.getR() + c.getG() + c.getB()) / 3;

		if (c.getR() > 200 && c.getG() > 200 && c.getB() > 200) {
			descs.add("Light");
		}
		if (c.getR() < 75 && c.getG() < 75 && c.getB() < 75) {
			descs.add("Dark");
		}
		if (avgVal > 190 && avgVal < 215) {
			descs.add("Pastel");
		}
		if (c.getB() < 100 && c.getG() > 100 && c.getG() < 150 && c.getR() > 100 && c.getR() < 150) {
			descs.add("Earthy");
		}
		if (c.getR() > 70 && c.getR() < 180 && c.getG() > 70 && c.getG() < 180 && c.getB() > 70 && c.getB() < 180) {
			descs.add("Natural");
		}

		return descs;

	}

	public ArrayList<Color> defaultColorGroups(BufferedImage bi, boolean createDefGroupOnly) {

		BufferedImage newImage = new BufferedImage(bi.getWidth(), bi.getHeight(), 1);

		Color[] defColors = new Color[] { new Color(255, 0, 0), new Color(255, 255, 0), new Color(0, 255, 255),
				new Color(255, 0, 255), new Color(255, 128, 0), new Color(128, 255, 0), new Color(0, 255, 128),
				new Color(0, 255, 255), new Color(0, 128, 255), new Color(128, 0, 255), new Color(255, 0, 255),
				new Color(255, 0, 128) };

		ArrayList<Color> closestColors = new ArrayList<Color>();

		int w = bi.getWidth();
		int h = bi.getHeight();

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				Color pixel = new Color(bi.getRGB(i, j));
				double minDev = 999.0;
				Color closestMatch = null;
				for (int k = 0; k < defColors.length; k++) {
					double currDist = colorDistance(pixel, defColors[k]);
					if (currDist < minDev) {
						minDev = currDist;
						closestMatch = defColors[k];
					}
				}
				if (createDefGroupOnly)
					newImage.setRGB(i, j, closestMatch.getRGB());
				if (closestColors.size() == 0) {
					closestColors.add(closestMatch);
				}
				boolean found = false;
				for (Color co : closestColors) {
					if (co.equals(closestMatch)) {
						found = true;
					}
				}
				if (!found)
					closestColors.add(closestMatch);
			}
		}
		if (createDefGroupOnly) {
			File outputfile = new File("defGroupOnly.jpg");
			try {
				ImageIO.write(newImage, "jpg", outputfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}
		}
		return closestColors;
	}
}
