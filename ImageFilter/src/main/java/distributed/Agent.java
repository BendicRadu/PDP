package distributed;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Scope("prototype")
public class Agent {


    @RabbitListener(queues = {"q1", "q2", "q3", "q4"})
    public ImageData receiveMessage(ImageData data) {

        System.out.println("\nReceived");

        List<List<Integer>> image = data.getImage();

        for (List<Integer> anImage : image) {
            for (int j = 0; j < image.get(0).size(); j++) {
                anImage.set(j, removeColor(anImage.get(j)));
            }
        }

        data.setImage(image);
        return data;
    }



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


}