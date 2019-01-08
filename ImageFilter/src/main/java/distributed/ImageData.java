package distributed;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.List;

public class ImageData implements Serializable{

    private List<List<Integer>> image;
    private int startRow;
    private int endRow;

    public ImageData(List<List<Integer>> image, int startRow, int endRow){
        this.image = image;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    public List<List<Integer>> getImage() {
        return image;
    }

    public void setImage(List<List<Integer>> image) {
        this.image = image;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getEndRow() {
        return endRow;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }
}
