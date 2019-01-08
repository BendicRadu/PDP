import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class Main {

    private static final String DIR = "D:\\Uni\\ImageFilter\\src\\main\\resources\\";

    private static final String IMAGE_IN  = "img_in.jpg";
    private static final String IMAGE_OUT = "img_out.jpg";


    private static int removeColor(int pixelRgb){

        int a = (pixelRgb >> 24) & 0xff;

        int r = (pixelRgb >> 16) & 0xff;
        int g = (pixelRgb >> 8) & 0xff;
        int b = pixelRgb & 0xff;

        int rgbAvg = (r + g + b) / 3;

        r = rgbAvg;
        g = rgbAvg;
        b = rgbAvg;

        return  (a<<24) | (r<<16) | (g<<8) | b;

    }


    private static void applyFilterTask(BufferedImage image, int startRow, int endRow){

        for (int y = startRow; y < endRow; y++){

            for (int x = 0; x < image.getWidth(); x++){

                image.setRGB(x, y, removeColor(image.getRGB(x, y)));
            }
        }

    }



    public static void main(String[] args) throws IOException {

        BufferedImage myPicture = ImageIO.read(new File(DIR + IMAGE_IN));

        int[] noOfThreadsArr = new int[]{1, 2, 4, 8, 16, 32, 64, 128};


        for (int noOfThreads : noOfThreadsArr){

            long startMillis = System.currentTimeMillis();

            System.out.println("\nOn " + noOfThreads + " threads: ");

            List<CompletableFuture> futureList = new ArrayList<>();

            int division = myPicture.getHeight() / noOfThreads;

            for (int i = 0; i < myPicture.getHeight(); i += division){

                final int startRow = i;
                final int endRow = i + division >= myPicture.getHeight() ? myPicture.getHeight() - 1 : i + division;

                futureList.add(
                        CompletableFuture.runAsync(() -> applyFilterTask(myPicture, startRow, endRow))
                );

            }

            futureList.forEach(x -> {
                        try {
                            x.get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

            long endMillis = System.currentTimeMillis();

            long time = endMillis - startMillis;

            System.out.println("Duration: " + time + "ms ");

        }


        File outputFile = new File(DIR + IMAGE_OUT);
        ImageIO.write(myPicture, "jpg", outputFile);

    }


}
