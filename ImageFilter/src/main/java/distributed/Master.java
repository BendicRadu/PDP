package distributed;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class Master {

    private final RabbitTemplate rabbitTemplate;

    private static final String DIR = "D:\\Uni\\ImageFilter\\src\\main\\resources\\";
    private static final String IMAGE_IN  = "img_in.jpg";
    private static final String IMAGE_OUT  = "img_out.jpg";

    private BufferedImage myPicture = ImageIO.read(new File(DIR + IMAGE_IN));

    public List<List<Integer>> getRgb(int start, int stop){

        List<List<Integer>> lst = new ArrayList<>();

        int k = 0;

        for(int i = start; i < stop; i++){

            lst.add(new ArrayList<>());

            for (int j = 0; j < myPicture.getWidth(); j++){
                lst.get(k).add(myPicture.getRGB(j, i));
            }

            k++;
        }
        return lst;
    }



    public void sendAndReceive(Integer q, int start, int end){

        ImageData imageData = new ImageData(getRgb(start, end), start, end);
        imageData = (ImageData) rabbitTemplate.convertSendAndReceive("spring-boot-exchange", q + "." + q + "." + q, imageData);

        List<List<Integer>> newImg = imageData.getImage();

        for(int i = 0; i < newImg.size(); i++){
            for(int j = 0; j < newImg.get(0).size(); j++){
                myPicture.setRGB(j, i + imageData.getStartRow(), newImg.get(i).get(j));
            }
        }
    }

    public Master(RabbitTemplate rabbitTemplate) throws IOException {
        this.rabbitTemplate = rabbitTemplate;
    }


    public void run() throws IOException {
        System.out.println("Sending message...");

        int division = myPicture.getHeight() / 4;

        List<CompletableFuture> futureList = new ArrayList<>();

        long startMillis = System.currentTimeMillis();


        for (int k = 0; k < 4; k++){

            int startRow = division * k;
            int endRow = startRow + division >= myPicture.getHeight() ? myPicture.getHeight() - 1 : startRow + division;
            int queue = k + 1;

            futureList.add(
                    CompletableFuture.runAsync(() -> sendAndReceive(queue, startRow, endRow))
            );
        }

        futureList.forEach(x-> {
            try {
                x.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        System.out.println("Done!");
        long endMillis = System.currentTimeMillis();

        long time = endMillis - startMillis;

        System.out.println("Duration: " + time + "ms ");


        File outputFile = new File(DIR + IMAGE_OUT);
        outputFile.createNewFile();
        ImageIO.write(myPicture, "jpg", outputFile);


    }

}
