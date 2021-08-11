package Definitions;

import java.util.ArrayList;

public class ColorGroup {
	
	private Color groupAverage;
	private ArrayList<Color> colors;
	
	public ColorGroup() {
		this.groupAverage = null;
		this.colors = new ArrayList<Color>();
	}
	
	private int addToAverage(int currNumColors, int avgVal, int newVal) {
		int newAvg = 0;
		
		newAvg = avgVal + ((newVal - avgVal) / (currNumColors + 1));
		
		return newAvg;
	}
	
	public Color getGroupAverage() {
		return groupAverage;
	}
	public void setGroupAverage(Color groupAverage) {
		this.groupAverage = groupAverage;
	}
	public ArrayList<Color> getColors() {
		return colors;
	}
	public void setColors(ArrayList<Color> colors) {
		this.colors = colors;
	}
	public void addColor(Color c) {
		int currNumColors = colors.size();
		colors.add(c);
		if(currNumColors == 0) {
			groupAverage = c;
		} else {
			groupAverage = new Color(
					addToAverage(currNumColors, groupAverage.getR(), c.getR()),
					addToAverage(currNumColors, groupAverage.getG(), c.getG()),
					addToAverage(currNumColors, groupAverage.getB(), c.getB()));
		}
	}

}
